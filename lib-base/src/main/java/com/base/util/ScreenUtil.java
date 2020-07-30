package com.base.util;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by sun.cunxing on 2018/7/16.
 */
public class ScreenUtil {
    public static int getSceenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getWidth();
    }

    public static int getSceenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return manager.getDefaultDisplay().getHeight();
    }
    public static int getSceenHeightExceptStatusBar(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int statusBarHeight = 0;
        int resId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId != 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resId); // 通过dimenId（资源文件）得到其代表的px大小
        }
        return manager.getDefaultDisplay().getHeight() - statusBarHeight;
    }

    public static int dip2px(Context context,float dpValue){
        final float scale = context.getResources ().getDisplayMetrics ().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
