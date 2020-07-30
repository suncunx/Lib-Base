package com.base.mvp;


import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * Created by sun.cunxing on 2018/9/25.
 * 包含生命周期的presenter
 * 推荐实现onCreate() 和 onDestroy()
 */
public interface BasePresenter<V extends BaseView> extends DefaultLifecycleObserver {

    void setView(V view);

    // 在Activity或Fragment的onCreate方法执行之后会调用此方法
    @Override
    void onCreate(@NonNull LifecycleOwner owner);

    // 在Activity或Fragment的onDestroy方法执行之前会调用此方法
    @Override
    void onDestroy(@NonNull LifecycleOwner owner);
}
