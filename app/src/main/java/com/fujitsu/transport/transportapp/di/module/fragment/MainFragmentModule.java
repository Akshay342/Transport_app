package com.fujitsu.transport.transportapp.di.module.fragment;


import com.fujitsu.transport.transportapp.view.DriverFragment;
import com.fujitsu.transport.transportapp.view.UserHomeFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MainFragmentModule {

  /*  @ContributesAndroidInjector
    abstract HomeFragment provideHomeFragment();

    @ContributesAndroidInjector
    abstract CreateMemoFragment provideCreateMemoFragment();

    @ContributesAndroidInjector
    abstract CallHistoryFragment provideCallHistoryFragment();

    @ContributesAndroidInjector
    abstract MemoDatabaseFragment provideMemoDatabaseFragment();

    @ContributesAndroidInjector
    abstract EventDetailsFragment provideEventDetailsFragment();*/

    @ContributesAndroidInjector
    abstract DriverFragment provideDriverFragment();

    @ContributesAndroidInjector
    abstract UserHomeFragment provideUserHomeFragment();
}
