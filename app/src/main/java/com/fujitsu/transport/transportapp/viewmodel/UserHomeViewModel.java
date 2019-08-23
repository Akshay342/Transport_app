package com.fujitsu.transport.transportapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.fujitsu.transport.transportapp.base.MutableResponse;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.model.RoutResponse;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserHomeViewModel extends ViewModel {
    private final RestRepository restRepository;
    private CompositeDisposable disposable;


    @Inject
    public UserHomeViewModel(RestRepository restRepository) {
        this.restRepository = restRepository;
        this.disposable = new CompositeDisposable();
    }

    public MutableLiveData<MutableResponse<List<Rout>>> getActiveRoutes(){
        final MutableLiveData<MutableResponse<List<Rout>>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<List<Rout>> mutableResponse = new MutableResponse<>();
        disposable.add(restRepository.getActiveRoutes()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<RoutResponse>(){
            @Override
            public void onSuccess(RoutResponse response) {
                mutableResponse.setBody(response.getRouts());
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
