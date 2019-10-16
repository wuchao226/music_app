package com.wuc.lib_audio.mediaplayer.core;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * @author: wuchao
 * @date: 2019-09-27 17:12
 * @desciption: 带状态的 MediaPlayer，对外提供状态
 */
public class CustomMediaPlayer extends MediaPlayer implements MediaPlayer.OnCompletionListener {

    private Status mState;
    private OnCompletionListener mOnCompletionListener;

    public CustomMediaPlayer() {
        super();
        mState = Status.IDEAL;
        super.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mState = Status.COMPLETED;
        if (mOnCompletionListener != null) {
            mOnCompletionListener.onCompletion(mediaPlayer);
        }
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, IllegalStateException, SecurityException {
        super.setDataSource(path);
        mState = Status.INITIALIZED;
    }

    @Override
    public void start() throws IllegalStateException {
        super.start();
        mState = Status.STARTED;
    }

    @Override
    public void stop() throws IllegalStateException {
        super.stop();
        mState = Status.STOPPED;
    }

    @Override
    public void pause() throws IllegalStateException {
        super.pause();
        mState = Status.PAUSED;
    }

    @Override
    public void reset() {
        super.reset();
        mState = Status.IDEAL;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        super.setOnCompletionListener(listener);
        mOnCompletionListener = listener;
    }

    public Status getState() {
        return mState;
    }

    public boolean isComplete() {
        return mState == Status.COMPLETED;
    }

    public enum Status {
        //初始状态，即空状态
        IDEAL,
        //初始化状态
        INITIALIZED,
        //开始状态
        STARTED,
        //暂停状态
        PAUSED,
        //停止状态
        STOPPED,
        //完成状态
        COMPLETED
    }
}
