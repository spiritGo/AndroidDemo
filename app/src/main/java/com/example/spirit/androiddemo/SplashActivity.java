package com.example.spirit.androiddemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.eftimoff.androipathview.PathView;

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
                                        startActivity(new Intent(SplashActivity.this,
                                                MainActivity.class));
                                        finish();
                                    }
                                });
                            }
                        }, 1000);
                    }
                })
                .start();
        pvPathView.setFillAfter(true);
    }
}
