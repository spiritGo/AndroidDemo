package com.example.spirit.androiddemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.eftimoff.androipathview.PathView;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    private PathView pvPathView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
        initUI();
    }

    private void initView() {
        pvPathView = findViewById(R.id.pv_pathView);
    }

    private void initUI() {
        pvPathView.getPathAnimator()
                .delay(500)
                .duration(2000)
                .listenerEnd(new PathView.AnimatorBuilder.ListenerEnd() {
                    @Override
                    public void onAnimationEnd() {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String versionName = SPUtil.getString(SPUtil
                                                .VERSION_NAME, "");
                                        if (!versionName.equals(Util.getVersionName())) {
                                            showDialog();
                                            SPUtil.putString(SPUtil.VERSION_NAME, Util
                                                    .getVersionName());
                                        } else {
                                            intoMainActivity();
                                        }
                                    }
                                });
                            }
                        }, 1000);
                    }
                })
                .start();
        pvPathView.setFillAfter(true);
    }

    private void showDialog() {
        View view = Util.inflateView(R.layout.dialog_update_info);
        final AlertDialog dialog = new AlertDialog.Builder(SplashActivity.this)
                .setView(view)
                .show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Point windowSize = Util.getWindowSize();
        Window window = dialog.getWindow();
        if (window != null && windowSize != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = Math.round(windowSize.x * 0.8f);
            attributes.height = Math.round(windowSize.y * 0.8f);
            window.setAttributes(attributes);
        }
        Button btnInto = view.findViewById(R.id.btn_into);
        btnInto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                intoMainActivity();
            }
        });
        pvPathView.setVisibility(View.INVISIBLE);
    }

    private void intoMainActivity() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}
