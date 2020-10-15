package com.wuc.ft_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import com.wuc.ft_audio.app.AudioHelper;
import com.wuc.ft_audio.mediaplayer.events.AudioCompleteEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioErrorEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioLoadEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioPauseEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioReleaseEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioStartEvent;
import com.wuc.ft_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * @author: wuchao
 * @date: 2019-10-08 16:12
 * @desciption: 音乐播放器核心类，对外提供，加载，播放，暂停等一系列底层方法，并向外界发送各种播放器事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, AudioFocusManager.AudioFocusListener {

    private static final String TAG = "AudioPlayer";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;
    /**
     * 真正负责播放的核心MediaPlayer子类
     */
    private CustomMediaPlayer mMediaPlayer;
    /**
     * WifiLock的锁，能够阻止wifi进入睡眠状态，使wifi一直处于活跃状态
     */
    private WifiManager.WifiLock mWifiLock;
    /**
     * 音频焦点监听器
     */

    private AudioFocusManager mAudioFocusManager;
    /**
     * 是否失去音频焦点而暂停
     */
    private boolean isPausedByFocusLossTransient;
    /**
     * 播放进度更新handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    break;
                default:
            }
        }
    };

    /**
     * 完成唯一的mediaplayer初始化
     */
    public AudioPlayer() {
        init();
    }

    /**
     * 初始化播放器相关对象
     */
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        // 使用唤醒锁
        mMediaPlayer.setWakeMode(AudioHelper.getContext(), PowerManager.PARTIAL_WAKE_LOCK);
        //设置流媒体的类型
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // 初始化wifi锁
        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        // 初始化音频焦点管理器
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    /**
     * 对外提供的加载音频的方法
     */
    public void load(AudioBean audioBean) {
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(audioBean.mUrl);
            mMediaPlayer.prepareAsync();
            //发送加载音频事件，UI类型处理事件
            EventBus.getDefault().post(new AudioLoadEvent(audioBean));
        } catch (IOException e) {
            e.printStackTrace();
            //对外发送error事件
            EventBus.getDefault().post(new AudioErrorEvent());
        }
    }

    /**
     * 对外暴露pause方法
     */
    public void pause() {
        //音频处于播放状态
        if (getStatus() == CustomMediaPlayer.Status.STARTED) {
            mMediaPlayer.pause();
            //关闭WiFi锁
            if (mWifiLock.isHeld()) {
                mWifiLock.release();
            }
            //取消音频焦点
            if (mAudioFocusManager != null) {
                mAudioFocusManager.abandonAudioFocus();
            }
            //停止发送进度消息
            //mHandler.removeCallbacksAndMessages(null);
            //发送暂停事件,UI类型事件
            EventBus.getDefault().post(new AudioPauseEvent());
        }
    }

    /**
     * 获取播放器状态
     */
    public CustomMediaPlayer.Status getStatus() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getState();
        } else {
            return CustomMediaPlayer.Status.STOPPED;
        }
    }

    /**
     * 销毁唯一mediaplayer实例，清空播放器资源，只有在退出app时使用
     */
    public void release() {
        if (mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.release();
        mMediaPlayer = null;
        // 取消音频焦点
        if (mAudioFocusManager != null) {
            mAudioFocusManager.abandonAudioFocus();
        }
        // 关闭wifi锁
        if (mWifiLock.isHeld()) {
            mWifiLock.release();
        }
        mWifiLock = null;
        mAudioFocusManager = null;
        mHandler.removeCallbacksAndMessages(null);
        //发送销毁播放器事件,清除通知等
        EventBus.getDefault().post(new AudioReleaseEvent());
    }

    /**
     * 网络流媒体的缓冲
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int percent) {
        //缓存进度回调
    }

    /**
     * 网络流媒体播放结束
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        //发送播放完成事件,逻辑类型事件
        EventBus.getDefault().post(new AudioCompleteEvent());
    }

    /**
     * 设置错误信息监
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        //发送当次播放实败事件,逻辑类型事件
        EventBus.getDefault().post(new AudioErrorEvent());
        //true：自行处理异常，不需要播放器处理
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //准备完毕，进入播放状态
        start();
    }

    /**
     * prepare以后自动调用start方法,外部不能调用
     */
    private void start() {
        // 获取音频焦点,保证我们的播放器顺利播放
        if (!mAudioFocusManager.requestAudioFocus()) {
            Log.e(TAG, "获取音频焦点失败");
        }
        mMediaPlayer.start();
        // 启用wifi锁
        mWifiLock.acquire();
        //更新进度
        mHandler.sendEmptyMessage(TIME_MSG);
        //发送start事件，UI类型处理事件
        EventBus.getDefault().post(new AudioStartEvent());
    }

    /**
     * 获得焦点回调处理
     */
    @Override
    public void audioFocusGrant() {
        //重新获得焦点
        setVolume(1.0f, 1.0f);
        if (isPausedByFocusLossTransient) {
            resume();
        }
        isPausedByFocusLossTransient = false;
    }

    /**
     * 永久失去焦点回调处理
     */
    @Override
    public void audioFocusLoss() {
        //永久失去焦点，暂停
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    /**
     * 短暂失去焦点回调处理
     */
    @Override
    public void audioFocusLossTransient() {
        //短暂失去焦点，暂停
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
        isPausedByFocusLossTransient = true;
    }

    /**
     * 瞬间失去焦点回调
     */
    @Override
    public void audioFocusLossDuck() {
        //瞬间失去焦点,
        setVolume(0.5f, 0.5f);
    }

    /**
     * 设置音量
     *
     * @param leftVol  左声道
     * @param rightVol 右声道
     */
    private void setVolume(float leftVol, float rightVol) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(leftVol, rightVol);
        }
    }

    /**
     * 对外提供的恢复播放方法
     */
    public void resume() {
        if (getStatus() == CustomMediaPlayer.Status.PAUSED) {
            start();
        }
    }

    /**
     * 获取当前音乐总时长,更新进度用
     */
    public int getDuration() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 对外提供获取当前播放时间
     */
    public int getCurrentPosition() {
        if (getStatus() == CustomMediaPlayer.Status.STARTED
                || getStatus() == CustomMediaPlayer.Status.PAUSED) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }
}
