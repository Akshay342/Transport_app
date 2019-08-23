package com.fujitsu.transport.transportapp.di.module.activity;


import com.fujitsu.transport.transportapp.di.module.fragment.MainFragmentModule;
import com.fujitsu.transport.transportapp.view.LoginActivity;
import com.fujitsu.transport.transportapp.view.MainActivity;
import com.fujitsu.transport.transportapp.view.MapsActivity;
import com.fujitsu.transport.transportapp.view.RegistrationActivity;

import dagger.Component;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector(modules = {MainFragmentModule.class})
    abstract MainActivity bindMainActivity();

    @ContributesAndroidInjector()
    abstract LoginActivity bindLoginActivity();

    @ContributesAndroidInjector()
    abstract MapsActivity bindMapsActivity();

    @ContributesAndroidInjector()
    abstract RegistrationActivity bindRegistrationActivity();

}
