package com.base;

import android.content.pm.ActivityInfo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;

/**
 * Created by sun.cunxing on 2018/12/18.
 */
public class ScreenOrientationConfig {
    public static final int SCREEN_ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static final int SCREEN_ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    @ScreenOrientation
    private static int SCREEN_ORIENTATION = SCREEN_ORIENTATION_PORTRAIT;

    public static void setScreenOrientation(@ScreenOrientationConfig.ScreenOrientation int screenOrientation) {
        SCREEN_ORIENTATION = screenOrientation;
    }

    public static int getScreenOrientation() {
        return SCREEN_ORIENTATION;
    }

    @IntDef({
            SCREEN_ORIENTATION_LANDSCAPE,
            SCREEN_ORIENTATION_PORTRAIT
    })

    // 怎么对应到IntDef里面去的
    @Retention(RetentionPolicy.SOURCE)
    public @interface ScreenOrientation {
    }
}
