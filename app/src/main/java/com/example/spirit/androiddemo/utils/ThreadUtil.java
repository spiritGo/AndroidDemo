package com.example.spirit.androiddemo.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;

public abstract class ThreadUtil<T> {
    final private int UPDATE_START = 0;
    final private int UPDATE_FINISHED = 1;
    private ArrayList<T> list;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_START:
                    updateStart();
                    break;
                case UPDATE_FINISHED:
                    updateFinished(list);
                    break;
            }
        }
    };

    public void newStartThread() {
        if (list == null) {
            new Thread() {
                @Override
                public void run() {
                    handler.sendEmptyMessage(UPDATE_START);
                    list = getList();
                    handler.sendEmptyMessage(UPDATE_FINISHED);
                }
            }.start();
        } else {
            handler.sendEmptyMessage(UPDATE_START);
            handler.sendEmptyMessage(UPDATE_FINISHED);
        }
    }

    protected abstract ArrayList<T> getList();

    protected abstract void updateStart();

    protected abstract void updateFinished(ArrayList<T> list);
}
