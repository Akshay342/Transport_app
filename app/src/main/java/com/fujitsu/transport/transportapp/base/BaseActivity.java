package com.fujitsu.transport.transportapp.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.ButterKnife;
import dagger.android.support.DaggerAppCompatActivity;

public abstract class BaseActivity<VM extends ViewModel> extends DaggerAppCompatActivity {
    public VM viewModel;

    protected abstract Class<VM> viewModelType();

    @Inject
    ViewModelFactory viewModelFactory;

    private  final String TAG = BaseActivity.class.getSimpleName();
    @LayoutRes
    protected abstract int layoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelType());
        setContentView(layoutRes());
        ButterKnife.bind(this);
    }



}