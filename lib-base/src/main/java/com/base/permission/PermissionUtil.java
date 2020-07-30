package com.base.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * 权限申请的工具类
 */

public class PermissionUtil {
    public static final String TAG = "PermissionUtil";

    private PermissionFragment mRxPermissionsFragment;

    public PermissionUtil(@NonNull Activity activity) {
        mRxPermissionsFragment = getRxPermissionsFragment(activity);
    }

    private void registerRequestPermissionListener(RequestPermissionListener listener) {
        mRxPermissionsFragment.registerRequestPermissionListener(listener);
    }

    private PermissionFragment getRxPermissionsFragment(Activity activity) {
        PermissionFragment rxPermissionsFragment = null;
        try {
            rxPermissionsFragment = findRxPermissionsFragment(activity);
            boolean isNewInstance = rxPermissionsFragment == null;
            if (isNewInstance) {
                rxPermissionsFragment = new PermissionFragment();
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager
                        .beginTransaction()
                        .add(rxPermissionsFragment, TAG)
                        .commitAllowingStateLoss();
                fragmentManager.executePendingTransactions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rxPermissionsFragment;
    }

    private PermissionFragment findRxPermissionsFragment(Activity activity) {
        return (PermissionFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public void setLogging(boolean logging) {
        mRxPermissionsFragment.setLogging(logging);
    }


    public void request(String permission, RequestPermissionListener requestPermissionListener) {
        request(new String[]{permission}, requestPermissionListener);

    }

    public void request(final String[] permissions, RequestPermissionListener requestPermissionListener) {
        if (requestPermissionListener == null) {
            Log.e(TAG, "request: requestPermissionListener is null");
            return;
        }
        // 当前有人正在当前Activity调用此接口申请权限
        if (mRxPermissionsFragment.getRequestPermissionListener() != null) {
            Log.e(TAG, "request: mRxPermissionsFragment's requestPermissionListener is not null");
            return;
        }
        registerRequestPermissionListener(requestPermissionListener);
        mRxPermissionsFragment.initData(permissions);
        requestImplementation(permissions, requestPermissionListener);
    }

    public void requestOverlay(RequestOverlayPermissionListener requestOverlayPermissionListener) {
        if (requestOverlayPermissionListener == null) {
            Log.e(TAG, "request: requestOverlayPermissionListener is null");
            return;
        }
        // 当前有人正在当前Activity调用此接口申请权限
        if (mRxPermissionsFragment.getRequestOverlayPermissionListener() != null) {
            Log.e(TAG, "request: mRxPermissionsFragment's requestOverlayPermissionListener is not null");
            return;
        }
        if (!isMarshmallow() || Settings.canDrawOverlays(mRxPermissionsFragment.getContext())) {
            requestOverlayPermissionListener.onRequestOverlayPermissionResult(true);
        } else {
            mRxPermissionsFragment.registerRequestOverlayPermissionListener(requestOverlayPermissionListener);
            mRxPermissionsFragment.requestOverlay();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestImplementation(final String[] permissions, RequestPermissionListener requestPermissionListener) {
        List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (int i = 0;i < permissions.length; ++i) {


            String permission = permissions[i];
            mRxPermissionsFragment.log("Requesting permission " + permission);
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                mRxPermissionsFragment.setPermissionResult(i, true, false);
                continue;
            }

            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                mRxPermissionsFragment.setPermissionResult(i, false, false);
                continue;
            }
            // Create a new subject if not exists
            if (mRxPermissionsFragment.getPermission(permission) == null) {
                Permission subject = new Permission(permission, false, false);
                unrequestedPermissions.add(permission);
                mRxPermissionsFragment.setSubjectForPermission(permission, subject);
            }
        }

        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        } else {
            requestPermissionListener.onRequestPermissionResult(permissions, mRxPermissionsFragment.getGrants(), mRxPermissionsFragment.getShouldShowRationales());
            mRxPermissionsFragment.unregisterRequestPermissionListener();
        }
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        if (!isMarshmallow()) {
            return false;
        }
        return shouldShowRequestPermissionRationaleImplementation(activity, permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mRxPermissionsFragment.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mRxPermissionsFragment.requestPermissions(permissions);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() || mRxPermissionsFragment.isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked (restricted) by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() && mRxPermissionsFragment.isRevoked(permission);
    }

    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults) {
        mRxPermissionsFragment.onRequestPermissionsResult(permissions, grantResults, new boolean[permissions.length]);
    }

    // 判断是否有悬浮窗权限
    public static boolean isOverlayGranted(Context context) {
        boolean granted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
        Log.d(TAG, "isAlertGranted granted = " + granted);
        return granted;
    }
}
