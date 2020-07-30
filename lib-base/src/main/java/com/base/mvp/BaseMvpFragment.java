package com.base.mvp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

/**
 * Created by sun.cunxing on 2018/9/27.
 * fragment的presenter在presenter的实现类里设置
 */
public abstract class BaseMvpFragment<D extends ViewDataBinding, P extends BasePresenter> extends Fragment implements BaseView<P>{
    protected P presenter;
    protected D dataBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = createPresenter();
        presenter.setView(this);
        getLifecycle().addObserver(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, getContentLayoutId(), container, false);
        initView();
        return dataBinding.getRoot();
    }

    protected abstract int getContentLayoutId();

    protected abstract void initView();

    protected abstract P createPresenter();

    @Override
    public P getPresenter() {
        return presenter;
    }
}
