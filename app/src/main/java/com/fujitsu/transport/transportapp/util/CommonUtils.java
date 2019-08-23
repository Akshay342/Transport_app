package com.fujitsu.transport.transportapp.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.fujitsu.transport.transportapp.R;


public class CommonUtils {

    public static ProgressDialog showProgressDialog(Activity mContext) {
        ProgressDialog mProgressDialog = new ProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public static void dismissProgressDialog(ProgressDialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(String message, View view) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }


}
