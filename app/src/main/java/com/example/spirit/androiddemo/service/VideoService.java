package com.example.spirit.androiddemo.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.io.IOException;

public class VideoService extends Service {

    private MediaPlayer mediaPlayer;
    private String path;
    private int windHeight;
    private int windWidth;
    private int limitWidth = Util.dp2px(20);
    private boolean isComplite = false;
    private WindowManager wm;
    private View view;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initVariable();
        showWindow();
    }

    private void initVariable() {
        Point windowSize = Util.getWindowSize();
        if (windowSize != null) {
            windWidth = windowSize.x - Util.getStatusBarHeight();
            windHeight = windowSize.y;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        path = intent.getStringExtra(ConstanceField.PATH);
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showWindow() {
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager
                .LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 0;
        params.y = 0;
        params.width = Util.dp2px(230);
        params.height = Util.dp2px(160);
        //final View view = LayoutInflater.from(this).inflate(R.layout.video_window_layout, null);
        view = Util.inflateView(R.layout.video_window_layout);
        ImageView ivCancel = view.findViewById(R.id.iv_cancel);
        final SurfaceView svWindow = view.findViewById(R.id.sv_window);
        ivCancel.setAlpha(0.8f);
        if (wm != null) {
            wm.addView(view, params);
        }

        playVideo(svWindow);
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), VideoService.class));
                if (wm != null) {
                    wm.removeView(view);
                }
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {

            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x += event.getRawX() - startX;
                        params.y += event.getRawY() - startY;

                        if (wm != null) {
                            wm.updateViewLayout(view, params);
                        }

                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (wm != null) {
                            if (params.x >= windHeight - params.width / 2) {
                                params.x = windHeight - limitWidth;
                                mediaPlayer.pause();
                            } else if (params.x <= -params.width / 2) {
                                params.x = limitWidth - params.width;
                                mediaPlayer.pause();
                            } else if (params.y >= windWidth - params.height / 2) {
                                params.y = windWidth - limitWidth;
                                mediaPlayer.pause();
                            } else if (params.y <= -params.height / 2) {
                                params.y = limitWidth - params.height;
                                mediaPlayer.pause();
                            } else {
                                if (!mediaPlayer.isPlaying() && !isComplite) {
                                    mediaPlayer.start();
                                }
                            }
                            wm.updateViewLayout(view, params);
                            System.out.println(mediaPlayer.getCurrentPosition() + "," +
                                    mediaPlayer.getDuration() + ", ");
                        }
                        break;
                }
                return true;
            }
        });
    }

    private void playVideo(SurfaceView svWindow) {
        SurfaceHolder holder = svWindow.getHolder();
        holder.addCallback(callback);
    }

    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(path);
                    mediaPlayer.setDisplay(holder);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    mediaPlayer.seekTo(SPUtil.getInt(SPUtil.VIDEO_CURRENT_POSITION, 0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isComplite = true;
                }
            });
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeView(view);
    }
}
