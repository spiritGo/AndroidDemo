package com.example.spirit.androiddemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.spirit.androiddemo.R;

public class Dialog {

    private static AlertDialog dialog;

    public static void createDialog(Context context) {
        final View view = Util.inflateView(R.layout.progress);
        dialog = new AlertDialog.Builder(context).setView(view).show();
        dialog.setCanceledOnTouchOutside(false);
        Window dialogWindow = dialog.getWindow();
        if (dialogWindow != null) {
            WindowManager.LayoutParams attributes = dialogWindow.getAttributes();
            attributes.width = Util.dp2px(200);
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            dialogWindow.setAttributes(attributes);
        }
    }

    public static void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
