package com.base.mvp;

import android.os.Bundle;

import com.base.ScreenOrientationConfig;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by sun.cunxing on 2018/12/13.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 默认为横屏版本
        // 在activity里面获取当前设置的屏幕方向方法为 getRequestedOrientation()
        setRequestedOrientation(ScreenOrientationConfig.getScreenOrientation());
        super.onCreate(savedInstanceState);
    }
}
