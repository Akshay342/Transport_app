package com.fujitsu.transport.transportapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.service.autofill.UserData;

import com.fujitsu.transport.transportapp.base.MutableResponse;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.RoutResponse;
import com.fujitsu.transport.transportapp.model.TokenUpdateResponse;
import com.fujitsu.transport.transportapp.model.UserDetails;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginViewModel extends ViewModel {
    private final RestRepository restRepository;
    private CompositeDisposable disposable;

    @Inject
    public LoginViewModel(RestRepository restRepository) {
        this.restRepository = restRepository;
        this.disposable = new CompositeDisposable();
    }

    public MutableLiveData<MutableResponse<UserDetails>> login(String username, String password){
        final MutableLiveData<MutableResponse<UserDetails>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<UserDetails> userDataMutableResponse = new MutableResponse<>();
        disposable.add(restRepository.login(username,password)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<UserDetails>(){
            @Override
            public void onSuccess(UserDetails response) {
                userDataMutableResponse.setBody(response);
                mutableLiveData.setValue(userDataMutableResponse);
            }

            @Override
            public void onError(Throwable e) {
                userDataMutableResponse.setError(e.getMessage());
                mutableLiveData.setValue(userDataMutableResponse);
            }
        }));
        return mutableLiveData;
    }

    public MutableLiveData<MutableResponse<TokenUpdateResponse>> updateToken(String userid, String routeid){
        final MutableLiveData<MutableResponse<TokenUpdateResponse>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<TokenUpdateResponse> mutableData = new MutableResponse<>();
        disposable.add(restRepository.updateToken(userid, routeid)
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
