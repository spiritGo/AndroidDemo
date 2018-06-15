package com.example.spirit.androiddemo.utils;

import android.media.MediaPlayer;

import com.example.spirit.androiddemo.interfaces.OnMusicCompletionListener;
import com.example.spirit.androiddemo.modle.MusicBean;

import java.io.IOException;

public class MediaPlayerUtil {
    final public static String START_ACTION = "start_action";
    final public static String RESTART_ACTION = "reStart_action";
    final public static String PAUSE_ACTION = "pause_action";
    final public static String STOP_ACTION = "stop_action";
    final public static String NEXT_ACTION = "next_action";
    final public static String PREV_ACTION = "prev_action";
    final public static String CURRENT_ITEM = "current_item";

    private static MediaPlayerUtil mediaPlayerUtil;
    private MediaPlayer player;

    private MediaPlayerUtil() {
        player = new MediaPlayer();
    }

    public static MediaPlayerUtil getMediaPlayerUtil() {
        if (mediaPlayerUtil == null) {
            synchronized (MediaPlayerUtil.class) {
                if (mediaPlayerUtil == null) {
                    mediaPlayerUtil = new MediaPlayerUtil();
                }
            }
        }
        return mediaPlayerUtil;
    }

    public void reStart(MusicBean musicBean) {
        try {
            player.reset();
            player.setDataSource(musicBean.getPath());
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setData(MusicBean musicBean) {
        try {
            player.reset();
            player.setDataSource(musicBean.getPath());
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (!player.isPlaying()) {
            player.start();
        }
    }

    public void pause() {
        if (player.isPlaying()) {
            player.pause();
        }
    }

    public void stop() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
        }
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    private StringBuilder getTextDuration(int duration) {
        int total = duration / 1000;
        int hour = total / 60 / 60;
        int min = total / 60 % 60;
        int s = total % 60;

        StringBuilder sb = new StringBuilder();
        if (hour != 0) {
            if (hour > 9) {
                sb.append(hour);
                sb.append(":");
            } else {
                sb.append("0").append(hour).append(":");
            }
        }

        if (min > 9) {
            sb.append(min).append(":");
        } else {
            sb.append("0").append(min).append(":");
        }

        if (s > 9) {
            sb.append(s);
        } else {
            sb.append("0").append(s);
        }

        return sb;
    }

    public StringBuilder getCurrentDuration() {
        return getTextDuration(player.getDuration() - player.getCurrentPosition());
    }

    public void setCompletionListener(final OnMusicCompletionListener completionListener) {
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (completionListener != null) {
                    completionListener.onMusicCompletion();
                }
            }
        });
    }
}
