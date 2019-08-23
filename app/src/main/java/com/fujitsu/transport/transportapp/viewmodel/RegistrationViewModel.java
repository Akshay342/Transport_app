package com.fujitsu.transport.transportapp.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.fujitsu.transport.transportapp.base.MutableResponse;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.UserDetails;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegistrationViewModel extends ViewModel {
    private final RestRepository restRepository;
    private CompositeDisposable disposable;

    @Inject
    public RegistrationViewModel(RestRepository restRepository) {
        this.restRepository = restRepository;
        this.disposable = new CompositeDisposable();
    }

    public MutableLiveData<MutableResponse<UserDetails>> register(String role, String number, String email, String lname, String fname, String pass, String uname){
        final MutableLiveData<MutableResponse<UserDetails>> mutableLiveData = new MutableLiveData<>();
        MutableResponse<UserDetails> mutableResponse = new MutableResponse<>();
        disposable.add(restRepository.registerUser(role, number, email, lname, fname, pass, uname)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<UserDetails>(){
                    @Override
                    public void onSuccess(UserDetails response) {
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
