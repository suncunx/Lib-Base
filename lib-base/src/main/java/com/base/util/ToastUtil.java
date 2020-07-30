package com.base.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast
 */
public class ToastUtil {
    private static final int SHORT_SHOW_ACTION = 1;
    private static final int LONG_SHOW_ACTION = 2;

    private static Toast mToast;


    public static void shortShow(Context context, int message) {
        cancel();
        mToast = getToast(context, message, SHORT_SHOW_ACTION);
        mToast.show();
    }

    public static void shortShow(Context context, String message) {
        if (!message.isEmpty()) {
            cancel();
            mToast = getToast(context, message, SHORT_SHOW_ACTION);
            mToast.show();
        }
    }

    public static void longShow(Context context, int message) {
        cancel();
        mToast = getToast(context, message, LONG_SHOW_ACTION);
        mToast.show();

    }

    public static void longShow(Context context, String message) {
        if (!message.isEmpty()) {
            cancel();
            mToast = getToast(context, message, LONG_SHOW_ACTION);
            mToast.show();
        }
    }

    private static Toast getToast(Context context, String message, int action) {

        switch (action) {
            case LONG_SHOW_ACTION:
                mToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_LONG);
                break;
            default:
                mToast = Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT);
                break;
        }

        return mToast;
    }

    private static Toast getToast(Context context, int message, int action) {
        switch (action) {
            case LONG_SHOW_ACTION:
                mToast = Toast.makeText(context.getApplicationContext(), context.getText(message), Toast.LENGTH_LONG);
                break;
            default:
                mToast = Toast.makeText(context.getApplicationContext(), context.getText(message), Toast.LENGTH_SHORT);
                break;
        }

        return mToast;
    }

    private static void cancel() {
        if (null != mToast) {
            mToast.cancel();
        }
    }

}
