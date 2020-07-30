package com.base.mvp;

import android.os.Bundle;


import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by sun.cunxing on 2018/9/25.
 * activity的presenter自己创建
 */
public abstract class BaseMvpActivity<D extends ViewDataBinding, P extends BasePresenter> extends BaseActivity implements BaseView<P>{
    protected P presenter;
    protected D dataBinding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, getContentLayoutId());
        presenter = createPresenter();
        getLifecycle().addObserver(presenter);
        presenter.setView(this);
        initView();
    }

    protected abstract int getContentLayoutId();

    protected abstract P createPresenter();

    @Override
    public P getPresenter() {
        return presenter;
    }

    protected void initView(){}
}
