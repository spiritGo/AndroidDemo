package com.example.spirit.androiddemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.spirit.androiddemo.service.VideoService;
import com.example.spirit.androiddemo.utils.ConstanceField;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.Timer;
import java.util.TimerTask;

public class VideoShowActivity extends Activity implements View.OnClickListener {
    private VideoView vvVideo;
    private LinearLayout llTop;
    private ImageView ivExit;
    private TextView tvTitle;
    private LinearLayout llBottom;
    private ImageView ivPlay;
    private String title;
    private SeekBar sbBar;
    private TextView tvDuration;
    private MediaPlayer player;
    private boolean isStatusVisible = true;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateProgress();
        }
    };
    private Timer timer;
    private int top;
    private int bottom;
    private ImageView smallWin;
    private String path;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_show);
        initView();
        initVariable();
        initUI();
    }

    private void initView() {
        vvVideo = findViewById(R.id.vv_video);
        llTop = findViewById(R.id.ll_top);
        ivExit = findViewById(R.id.iv_exit);
        tvTitle = findViewById(R.id.tv_title);
        llBottom = findViewById(R.id.ll_bottom);
        ivPlay = findViewById(R.id.iv_play);
        sbBar = findViewById(R.id.sb_bar);
        tvDuration = findViewById(R.id.tv_duration);
        smallWin = findViewById(R.id.smallWin);
    }

    private void initVariable() {
        Intent intent = getIntent();
        title = intent.getStringExtra(ConstanceField.TITLE);
        path = intent.getStringExtra(ConstanceField.PATH);
        vvVideo.setVideoPath(path);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        ivExit.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivExit.setColorFilter(Color.WHITE);
        tvTitle.setText(title);
        vvVideo.start();

        vvVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setPlayer(mp);
                sbBar.setMax(vvVideo.getDuration());
                if (vvVideo.canPause()) {
                    ivPlay.setImageResource(R.mipmap.pause);
                }
                updateProgress();
            }
        });

        sbBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                vvVideo.seekTo(seekBar.getProgress());
            }
        });

        vvVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.pause();
                ivPlay.setImageResource(R.mipmap.play);

            }
        });

        llTop.measure(0, 0);
        llBottom.measure(0, 0);
        sbBar.measure(0, 0);
        int sbBarHeight = sbBar.getMeasuredHeight();
        int bottomHeight = llBottom.getMeasuredHeight();
        int topHeight = llTop.getMeasuredHeight();
        top = topHeight + Util.getStatusBarHeight();
        bottom = bottomHeight - sbBarHeight;

        timer = new Timer();
        hideStatusTimer();

        sbBar.setThumb(null);
        vvVideo.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isStatusVisible) {
                    showStatus();

                    hideStatusTimer();
                } else {
                    timer.cancel();
                    hideStatus();
                }
                return false;
            }
        });

        smallWin.setColorFilter(Color.WHITE);
        smallWin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideoShowActivity.this, VideoService.class);
                intent.putExtra(ConstanceField.PATH, path);
                startService(intent);
                SPUtil.putInt(SPUtil.VIDEO_CURRENT_POSITION, vvVideo.getCurrentPosition());
                finish();
            }
        });
    }

    private void showStatus() {
        llTop.animate().translationY(0).start();
        llBottom.animate().translationY(0).start();
        isStatusVisible = true;
    }

    private void hideStatus() {
        llTop.animate().translationY(-top).start();
        llBottom.animate().translationY(bottom).start();
        isStatusVisible = false;
    }

    private void hideStatusTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        hideStatus();
                        timer.cancel();
                    }
                });
            }
        }, 1000 * 10);
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }

    private void updateProgress() {
        sbBar.setProgress(vvVideo.getCurrentPosition());
        tvDuration.setText(Util.getTextDuration(vvVideo.getDuration() - vvVideo
                .getCurrentPosition()));
        handler.sendEmptyMessageDelayed(0, 350);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_exit:
                finish();
                break;
            case R.id.iv_play:
                if (player.isPlaying()) {
                    pause();
                    ivPlay.setImageResource(R.mipmap.play);
                    handler.removeCallbacksAndMessages(null);
                } else {
                    start();
                    ivPlay.setImageResource(R.mipmap.pause);
                    handler.sendEmptyMessageDelayed(0, 350);
                }
                break;
        }
    }

    private void start() {
        if (!player.isPlaying()) {
            vvVideo.start();
        }
    }

    private void pause() {
        if (player.isPlaying()) {
            vvVideo.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vvVideo.pause();
        handler.removeCallbacksAndMessages(null);
        timer.purge();
    }
}
