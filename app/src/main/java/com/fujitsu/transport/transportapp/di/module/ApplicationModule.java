package com.fujitsu.transport.transportapp.di.module;


import android.content.Context;

import com.fujitsu.transport.transportapp.data.rest.RestService;
import com.fujitsu.transport.transportapp.di.module.viewmodel.ViewModelModule;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.util.Constants;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
@Module(includes = {ViewModelModule.class})
public class ApplicationModule {

    @Singleton
    @Provides
    public static Retrofit provideRetrofit() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(40, TimeUnit.SECONDS).build();
        return new Retrofit.Builder().baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    static RestService provideRestService(Retrofit retrofit) {
        return retrofit.create(RestService.class);
    }

    @Singleton
    @Provides
    static Session provideSession(Context context){
        return new Session(context);
    }

}
