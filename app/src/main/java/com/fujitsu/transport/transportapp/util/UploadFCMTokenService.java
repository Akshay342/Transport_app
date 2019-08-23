package com.fujitsu.transport.transportapp.util;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.fujitsu.transport.transportapp.base.MutableResponse;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.model.TokenUpdateResponse;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UploadFCMTokenService {
    private RestRepository restRepository;
    private Session session;

    @Inject
    public UploadFCMTokenService(RestRepository restRepository, Session session) {
        this.restRepository = restRepository;
        this.session = session;
    }

    public MutableLiveData<MutableResponse<TokenUpdateResponse>> updateToken(){
        if(!session.isLoggedIn() || session.getUserId().length() <1)
            return null;

        CompositeDisposable disposable =  new CompositeDisposable();
        final MutableLiveData<MutableResponse<TokenUpdateResponse>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<TokenUpdateResponse> mutableData = new MutableResponse<>();
        disposable.add(restRepository.updateToken(session.getUserId(), session.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<TokenUpdateResponse>(){
                    @Override
                    public void onSuccess(TokenUpdateResponse response) {
                        mutableData.setBody(response);
                        mutableLiveData.setValue(mutableData);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mutableData.setError(e.getMessage());
                        mutableLiveData.setValue(mutableData);
                    }
                }));
        return mutableLiveData;
    }
}
