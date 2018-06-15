package com.example.spirit.androiddemo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.spirit.androiddemo.utils.Util;
import com.wanjian.cockroach.Cockroach;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {
    private static Context mContext;
    private static String name;

    public static Context getmContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();


        Cockroach.install(new Cockroach.ExceptionHandler() {
            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("AndroidRuntime", "--->CockroachException:" + thread + "<---",
                                throwable);

                        Date date = new Date();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new
                                SimpleDateFormat("MM-dd HH:mm:ss");

                        String format = simpleDateFormat.format(date);
                        String s = format + "\n" + name + "\n" + throwable.toString() + "\n\n";
                        Util.saveFile("error", s, MODE_APPEND);
                    }
                });
            }
        });
    }

    public static void setName(String name) {
        MyApplication.name = name;
    }
}
