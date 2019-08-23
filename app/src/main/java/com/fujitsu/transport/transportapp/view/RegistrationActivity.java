package com.fujitsu.transport.transportapp.view;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseActivity;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.util.CommonUtils;
import com.fujitsu.transport.transportapp.util.NetworkUtils;
import com.fujitsu.transport.transportapp.util.UploadFCMTokenService;
import com.fujitsu.transport.transportapp.viewmodel.RegistrationViewModel;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class RegistrationActivity extends BaseActivity<RegistrationViewModel> {

    @BindView(R.id.et_fname)
    EditText etFname;
    @BindView(R.id.lastbame)
    EditText lastbame;
    @BindView(R.id.email)
    EditText et_email;
    @BindView(R.id.pass)
    EditText pass;
    @BindView(R.id.spinner_job)
    Spinner spinnerJob;
    @BindView(R.id.mobile)
    EditText et_mobile;
    @BindView(R.id.btn_register)
    Button btnRegister;
    private ProgressDialog dialog;

    @Inject
    Session session;

    @Inject
    RestRepository restRepository;

    @Override
    protected int layoutRes() {
        return R.layout.activity_registration;
    }
    @Override
    protected Class<RegistrationViewModel> viewModelType(){
        return RegistrationViewModel.class;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait.");
    }

    @OnClick(R.id.btn_register)
    public void onViewClicked() {
        if (NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            final String firstName = etFname.getText().toString();
            final String lastName = lastbame.getText().toString();
            final String password = pass.getText().toString();
            final String email = et_email.getText().toString();
            final String mobile = et_mobile.getText().toString();
            final String jobrole = String.valueOf(spinnerJob.getSelectedItem());
            if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || email.isEmpty() || mobile.isEmpty() || jobrole.isEmpty()) {
                CommonUtils.showToast(getApplicationContext(), "All fields are required");
                return;
            }
            if(!isEmailValid(email)){
                CommonUtils.showToast(getApplicationContext(), "Invalid email address");
                return;
            }
            if(!isPasswordStrong(password)){
                return;
            }
            if(spinnerJob.getSelectedItemPosition() == 0){
                CommonUtils.showToast(getApplicationContext(), "Please select a job role");
                return;
            }
            if(mobile.length() < 10){
                CommonUtils.showToast(getApplicationContext(), "Invalid mobile number");
                return;
            }


            showProgressDialog();
            viewModel.register(jobrole, mobile, email, lastName, firstName, password, firstName).observe(this, mutableLiveData -> {
                if (mutableLiveData != null && mutableLiveData.getBody() != null && mutableLiveData.getBody().getSUCCESS()) {
                    session.setLoggedIn(true, mutableLiveData.getBody().getRole(), mutableLiveData.getBody().getId(), mutableLiveData.getBody().getFirstname());
                    new UploadFCMTokenService(restRepository, session).updateToken();

                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                } else {
                    session.logout();
                    CommonUtils.showToast(getApplicationContext(), "Registration failed.");
                }
                hideProgressDialog();
            });
        }
    }
    private static boolean isEmailValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean isPasswordStrong(String password){
        boolean hasLetter = false;
        boolean hasDigit = false;

        if (password.length() >= 8) {
            Log.i("PWD","length > 8");
            for (int i = 0; i < password.length(); i++) {
                char x = password.charAt(i);
                if (Character.isLetter(x)) {

                    hasLetter = true;
                    Log.i("PWD","hasChar");
                }

                else if (Character.isDigit(x)) {
                    Log.i("PWD","hasDigit");
                    hasDigit = true;
                }
                if(hasLetter && hasDigit){
                    Log.i("PWD","Has char digit");
                    break;
                }

            }
            if (hasLetter && hasDigit) {
                Log.i("PWD","Password strong");
                return true;
            } else {
                Log.i("PWD","Password weak");
                CommonUtils.showToast(getApplicationContext(), "Password should contain at least 1 letter and 1 digit");
                return false;
            }
        } else {
            Log.i("PWD","length less than 8");
            CommonUtils.showToast(getApplicationContext(), "Password should have atleast 8 characters");
            return false;
        }
    }

    private void showProgressDialog() {
        dialog.show();
    }

    private void hideProgressDialog() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
