package com.fujitsu.transport.transportapp.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseActivity;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.util.CommonUtils;
import com.fujitsu.transport.transportapp.util.NetworkUtils;
import com.fujitsu.transport.transportapp.util.UploadFCMTokenService;
import com.fujitsu.transport.transportapp.viewmodel.LoginViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginViewModel> {

    @BindView(R.id.tv_heading)
    TextView tvHeading;
    @BindView(R.id.tv_sub_heading)
    TextView tvSubHeading;
    @BindView(R.id.et_login_name)
    EditText etLoginName;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.check_terms)
    CheckBox checkTerms;
    @BindView(R.id.tv_terms)
    TextView tvTerms;
    @BindView(R.id.btn_login)
    Button btn_login;
    @BindView(R.id.btn_registerhere)
    TextView btnRegisterhere;
    private ProgressDialog dialog;

    @Inject
    Session session;
    @Inject
    RestRepository restRepository;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected int layoutRes() {
        return R.layout.activity_login;
    }

    @Override
    protected Class<LoginViewModel> viewModelType(){
        return LoginViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait.");
    }


    @OnClick({R.id.btn_login, R.id.btn_registerhere})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                String loginValue = etLoginName.getText().toString();
                String password = etPassword.getText().toString();
                if (loginValue.length() > 0 && password.length() > 0) {
                    if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
                        showProgressDialog();
                        viewModel.login(loginValue, password).observe(this, mutableLiveData -> {
                            if (mutableLiveData != null && mutableLiveData.getBody() != null) {
                                if (mutableLiveData.getBody().getSUCCESS()) {
                                    session.setLoggedIn(true, mutableLiveData.getBody().getRole(), mutableLiveData.getBody().getId(), mutableLiveData.getBody().getFirstname());
                                    new UploadFCMTokenService(restRepository, session).updateToken();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    session.logout();
                                    CommonUtils.showToast(getApplicationContext(), "Login failed.");
                                }
                            } else {
                                CommonUtils.showToast(getApplicationContext(), "Login failed.");
                            }
                            hideProgressDialog();
                        });
                    }
                } else {
                    session.logout();
                    CommonUtils.showToast(getApplicationContext(), "Please provide your credentials.");
                }
                break;
            case R.id.btn_registerhere:
                startActivity(new Intent(this, RegistrationActivity.class));
                break;
        }
    }

    public void showProgressDialog() {
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
