package com.fujitsu.transport.transportapp.di.module.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.fujitsu.transport.transportapp.base.dagger.ViewModelKey;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.viewmodel.DriverViewModel;
import com.fujitsu.transport.transportapp.viewmodel.LoginViewModel;
import com.fujitsu.transport.transportapp.viewmodel.MainViewModel;
import com.fujitsu.transport.transportapp.viewmodel.MapsViewModel;
import com.fujitsu.transport.transportapp.viewmodel.RegistrationViewModel;
import com.fujitsu.transport.transportapp.viewmodel.UserHomeViewModel;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Singleton
@Module
public abstract class ViewModelModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DriverViewModel.class)
    abstract ViewModel bindDriverViewModel(DriverViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MapsViewModel.class)
    abstract ViewModel bindMapsViewModel(MapsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel.class)
    abstract ViewModel bindRegistrationViewModel(RegistrationViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UserHomeViewModel.class)
    abstract ViewModel bindUserHomeViewModel(UserHomeViewModel viewModel);
}
