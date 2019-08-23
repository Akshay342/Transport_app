package com.fujitsu.transport.transportapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.fujitsu.transport.transportapp.base.MutableResponse;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.RoutResponse;
import java.util.List;
import javax.inject.Inject;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class DriverViewModel extends ViewModel {
    private final RestRepository restRepository;
    private CompositeDisposable disposable;

    @Inject
    public DriverViewModel(RestRepository restRepository) {
        this.restRepository = restRepository;
        this.disposable = new CompositeDisposable();
    }

    public MutableLiveData<MutableResponse<RoutResponse>> getRoutes(){
        final MutableLiveData<MutableResponse<RoutResponse>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<RoutResponse> mutableResponse = new MutableResponse<>();
        disposable.add(restRepository.getRoutes()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<RoutResponse>(){
            @Override
            public void onSuccess(RoutResponse response) {
                mutableResponse.setBody(response);
                mutableLiveData.setValue(mutableResponse);
            }

            @Override
            public void onError(Throwable e) {
                mutableResponse.setError(e.getMessage());
                mutableLiveData.setValue(mutableResponse);
            }
        }));
        return mutableLiveData;
    }

    public MutableLiveData<MutableResponse<RoutResponse>> get_active_route_for_driver(String driverId){
        final MutableLiveData<MutableResponse<RoutResponse>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<RoutResponse> mutableResponse = new MutableResponse<>();
        disposable.add(restRepository.get_active_route_for_driver(driverId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<RoutResponse>(){
            @Override
            public void onSuccess(RoutResponse response) {
                mutableResponse.setBody(response);
                mutableLiveData.setValue(mutableResponse);
            }

            @Override
            public void onError(Throwable e) {
                mutableResponse.setError(e.getMessage());
                mutableLiveData.setValue(mutableResponse);
            }
        }));
        return mutableLiveData;
    }
}
