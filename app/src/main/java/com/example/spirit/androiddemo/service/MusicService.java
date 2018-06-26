package com.example.spirit.androiddemo.service;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.spirit.androiddemo.R;
import com.example.spirit.androiddemo.fragment.MusicFragment;
import com.example.spirit.androiddemo.interfaces.OnMusicCompletionListener;
import com.example.spirit.androiddemo.modle.MusicBean;
import com.example.spirit.androiddemo.utils.MediaPlayerUtil;
import com.example.spirit.androiddemo.utils.SPUtil;
import com.example.spirit.androiddemo.utils.Util;

import java.util.ArrayList;

public class MusicService extends Service {

    private static ArrayList<MusicBean> musicBeans;
    private MusicBroadCast musicBroadCast;
    private MediaPlayerUtil mediaPlayerUtil;
    private int currentItem = 0;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private RemoteViews remoteViews;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            updateProgress();
        }
    };

    public static void setMusicBeans(ArrayList<MusicBean> musicBeans) {
        MusicService.musicBeans = musicBeans;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initVariable();
        initUI();
    }

    private void initVariable() {
        registerMusicBroadCast();
        mediaPlayerUtil = MediaPlayerUtil.getMediaPlayerUtil();
        currentItem = SPUtil.getInt(MediaPlayerUtil.CURRENT_ITEM, 0);
    }

    private void initUI() {
        mediaPlayerUtil.setData(musicBeans.get(currentItem));
        createNotification();
        mediaPlayerUtil.setCompletionListener(new OnMusicCompletionListener() {
            @Override
            public void onMusicCompletion() {
                next();
                SPUtil.putInt(MediaPlayerUtil.CURRENT_ITEM, currentItem);
                remoteViewSet();
                MusicFragment.getMusicFragment().setCurrentItem(currentItem);
            }
        });
    }

    public void next() {
        if (currentItem >= musicBeans.size() - 1) {
            currentItem = 0;
        } else {
            currentItem++;
        }
        mediaPlayerUtil.reStart(musicBeans.get(currentItem));
    }

    public void prev() {
        if (currentItem <= 0) {
            currentItem = musicBeans.size() - 1;
        } else {
            currentItem--;
        }
        mediaPlayerUtil.reStart(musicBeans.get(currentItem));
    }

    private void createNotification() {
        notificationManager = (NotificationManager) getSystemService
                (NOTIFICATION_SERVICE);
        builder = new NotificationCompat.Builder(Util.getMContext());
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_layout);
        builder.setSmallIcon(R.mipmap.music2).setOngoing(true);
        builder.setContent(remoteViews);

        remoteViewSet();
    }

    private void remoteViewSet() {
        currentItem = SPUtil.getInt(MediaPlayerUtil.CURRENT_ITEM, 0);
        remoteViews.setTextViewText(R.id.tv_title, musicBeans.get(currentItem).getMusicName());
        remoteViews.setTextViewText(R.id.tv_duration, mediaPlayerUtil.getCurrentDuration());
        remoteViews.setOnClickPendingIntent(R.id.iv_prev, PendingIntent.getBroadcast(Util
                .getMContext(), 0, new Intent(MediaPlayerUtil.PREV_ACTION), PendingIntent
                .FLAG_UPDATE_CURRENT));
        remoteViews.setOnClickPendingIntent(R.id.iv_next, PendingIntent.getBroadcast(Util
                .getMContext(), 2, new Intent(MediaPlayerUtil.NEXT_ACTION), PendingIntent
                .FLAG_UPDATE_CURRENT));

        if (mediaPlayerUtil.isPlaying()) {
            setPlayPendingIntent(remoteViews, R.mipmap.pause, MediaPlayerUtil.PAUSE_ACTION);
        } else {
            setPlayPendingIntent(remoteViews, R.mipmap.play, MediaPlayerUtil.START_ACTION);
        }

        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    private void updateProgress() {
        remoteViews.setTextViewText(R.id.tv_duration, mediaPlayerUtil.getCurrentDuration());
        handler.sendEmptyMessageDelayed(0, 350);

        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }

    private void setPlayPendingIntent(RemoteViews remoteViews, int pause, String pauseAction) {
        remoteViews.setImageViewResource(R.id.iv_play, pause);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Util.getMContext(), 1, new
                Intent(pauseAction), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.iv_play, pendingIntent);
    }

    private void registerMusicBroadCast() {
        musicBroadCast = new MusicBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaPlayerUtil.RESTART_ACTION);
        filter.addAction(MediaPlayerUtil.START_ACTION);
        filter.addAction(MediaPlayerUtil.PAUSE_ACTION);
        filter.addAction(MediaPlayerUtil.STOP_ACTION);
        filter.addAction(MediaPlayerUtil.NEXT_ACTION);
        filter.addAction(MediaPlayerUtil.PREV_ACTION);
        registerReceiver(musicBroadCast, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicBroadCast != null) {
            unregisterReceiver(musicBroadCast);
        }

        if (mediaPlayerUtil != null) {
            mediaPlayerUtil.stop();
        }
    }

    private void musicRestart(Intent intent) {
        if (musicBeans != null) {
            currentItem = intent.getIntExtra(MediaPlayerUtil.CURRENT_ITEM, 0);
            mediaPlayerUtil.reStart(musicBeans.get(currentItem));
        }
    }


    class MusicBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MediaPlayerUtil.RESTART_ACTION.equals(action)) {
                musicRestart(intent);
                SPUtil.putInt(MediaPlayerUtil.CURRENT_ITEM, currentItem);
                MusicFragment.getMusicFragment().setCurrentItem(currentItem);
                handler.sendEmptyMessageDelayed(0, 350);
            } else if (MediaPlayerUtil.START_ACTION.equals(action)) {
                mediaPlayerUtil.start();
                handler.sendEmptyMessageDelayed(0, 350);
            } else if (MediaPlayerUtil.PAUSE_ACTION.equals(action)) {
                mediaPlayerUtil.pause();
                handler.removeCallbacksAndMessages(null);
            } else if (MediaPlayerUtil.STOP_ACTION.equals(action)) {
                mediaPlayerUtil.stop();
                handler.removeCallbacksAndMessages(null);
            } else if (MediaPlayerUtil.NEXT_ACTION.equals(action)) {
                next();
                SPUtil.putInt(MediaPlayerUtil.CURRENT_ITEM, currentItem);
                MusicFragment.getMusicFragment().setCurrentItem(currentItem);
            } else if (MediaPlayerUtil.PREV_ACTION.equals(action)) {
                prev();
                SPUtil.putInt(MediaPlayerUtil.CURRENT_ITEM, currentItem);
                MusicFragment.getMusicFragment().setCurrentItem(currentItem);
            }

            remoteViewSet();
        }
    }
}
