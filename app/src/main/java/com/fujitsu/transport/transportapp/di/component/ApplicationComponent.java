package com.fujitsu.transport.transportapp.di.component;

import android.app.Application;

import com.fujitsu.transport.transportapp.base.BaseApplication;
import com.fujitsu.transport.transportapp.di.module.activity.ActivityModule;
import com.fujitsu.transport.transportapp.di.module.ApplicationModule;
import com.fujitsu.transport.transportapp.di.module.ContextModule;
import com.fujitsu.transport.transportapp.util.UploadFCMTokenService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules = {ContextModule.class, ApplicationModule.class, AndroidSupportInjectionModule.class, ActivityModule.class})
public interface ApplicationComponent extends AndroidInjector<DaggerApplication> {

    void inject(BaseApplication application);
    void inject(UploadFCMTokenService uploadFCMTokenService);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        ApplicationComponent build();
    }
}