package com.fujitsu.transport.transportapp.base;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.support.DaggerFragment;

public abstract class BaseFragment<VM extends ViewModel>  extends DaggerFragment {

    private Unbinder unbinder;
    private AppCompatActivity activity;
    private  final String TAG = BaseFragment.class.getSimpleName();
    public VM viewModel;

    protected abstract Class<VM> viewModelType();

    @Inject
    ViewModelFactory viewModelFactory;

    @LayoutRes
    protected abstract int layoutRes();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(viewModelType());
        View view = inflater.inflate(layoutRes(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        activity = (AppCompatActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public AppCompatActivity getBaseActivity() {
        return activity;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(unbinder != null) {
            unbinder.unbind();
            unbinder = null;
        }
        Log.d(TAG, "onDestroyView: ");
    }
}