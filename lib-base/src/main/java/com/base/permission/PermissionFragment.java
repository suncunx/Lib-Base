package com.base.permission;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static com.base.permission.PermissionUtil.TAG;


/**
 * 用于申请权限的辅助fragment
 */

public class PermissionFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private Map<String, Permission> mSubjects = new HashMap<>();
    private Map<String, Integer> hmIndex = new HashMap<>();
    private boolean mLogging = true;
    private RequestPermissionListener requestPermissionListener;
    private RequestOverlayPermissionListener requestOverlayPermissionListener;
    private String[] permissions;
    private boolean[] grants, shouldShowRationales;

    public PermissionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions) {
        requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }

        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    void onRequestPermissionsResult(String[] permissions, int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            // Find the corresponding subject
            Permission subject = mSubjects.get(permissions[i]);
            if (subject == null) {
                // No subject found
                Log.e(TAG, "PermissionUtil.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            mSubjects.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            setPermissionResult(hmIndex.get(permissions[i]), granted, shouldShowRequestPermissionRationale[i]);
        }
        if (requestPermissionListener != null) {
            requestPermissionListener.onRequestPermissionResult(this.permissions, grants, shouldShowRationales);
        }
        unregisterRequestPermissionListener();
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    public Permission getPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public Permission setSubjectForPermission(@NonNull String permission, @NonNull Permission subject) {
        return mSubjects.put(permission, subject);
    }

    public void initData(String[] permissions) {
        this.permissions = new String[permissions.length];
        this.grants = new boolean[permissions.length];
        this.shouldShowRationales = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            this.permissions[i] = permissions[i];
            grants[i] = false;
            shouldShowRationales[i] = false;
            hmIndex.put(permissions[i], i);
        }
    }

    void log(String message) {
        if (mLogging) {
            Log.d(TAG, message);
        }
    }

    public void registerRequestPermissionListener(RequestPermissionListener listener) {
        requestPermissionListener = listener;
    }

    public void unregisterRequestPermissionListener() {
        Log.d(TAG, "unregisterRequestPermissionListener: ");
        if (requestPermissionListener != null) {
            requestPermissionListener = null;
        }
    }

    public void setPermissionResult(int i, boolean granted, boolean shouldShowRationale) {
        grants[i] = granted;
        shouldShowRationales[i] = shouldShowRationale;
    }

    public boolean[] getGrants() {
        return grants;
    }

    public boolean[] getShouldShowRationales() {
        return shouldShowRationales;
    }

    public RequestPermissionListener getRequestPermissionListener() {
        return requestPermissionListener;
    }

    public void registerRequestOverlayPermissionListener(RequestOverlayPermissionListener listener) {
        requestOverlayPermissionListener = listener;
    }

    public void unregisterRequestOverlayPermissionListener() {
        Log.d(TAG, "unregisterRequestOverlayPermissionListener: ");
        if (requestOverlayPermissionListener != null) {
            requestOverlayPermissionListener = null;
        }
    }

    public RequestOverlayPermissionListener getRequestOverlayPermissionListener() {
        return requestOverlayPermissionListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            boolean granted = false;
            if (Settings.canDrawOverlays(getContext())) {
                granted = true;
            }
            if (requestOverlayPermissionListener != null) {
                requestOverlayPermissionListener.onRequestOverlayPermissionResult(granted);
            }
            unregisterRequestOverlayPermissionListener();
        }
    }

    // 申请悬浮窗权限
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void requestOverlay() {
        //没有悬浮窗权限m,去开启悬浮窗权限
        try {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
