package com.fujitsu.transport.transportapp.view;

import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseActivity;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.util.Constants;
import com.fujitsu.transport.transportapp.viewmodel.MainViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainViewModel> {

    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;

    @Inject
    Session session;
    @BindView(R.id.iv_logout)
    ImageView ivLogout;
    @BindView(R.id.tv_username)
    TextView tvUsername;

    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected Class<MainViewModel> viewModelType() {
        return MainViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        navigateToScreen(session.getUserType());

        tvUsername.setText(session.getUserName().substring(0,1).toUpperCase() + session.getUserName().substring(1).toLowerCase() + " (" + session.getUserType().substring(0,1).toUpperCase() + session.getUserType().substring(1).toLowerCase() + ")");
    }

    public void navigateToScreen(String role) {
        if (role.equalsIgnoreCase(Constants.USER_TYPE_USER)) {
            replaceFragment(new UserHomeFragment());
        } else if (role.equalsIgnoreCase(Constants.USER_TYPE_DRIVER)) {
            replaceFragment(new DriverFragment());
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.iv_logout)
    public void onViewClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to log out ?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                session.logout();
                startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        }) .setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
