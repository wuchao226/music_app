package com.wuc.ft_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author: wuchao
 * @date: 2019-10-08 14:45
 * @desciption: 音频焦点监听器
 */
public class AudioFocusManager implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = AudioFocusManager.class.getSimpleName();

    private AudioFocusListener mAudioFocusListener;
    private AudioManager mAudioManager;

    public AudioFocusManager(Context context, AudioFocusListener listener) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusListener = listener;
    }

    public boolean requestAudioFocus() {
        return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 重新获得焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusGrant();
                }
                break;
            // 永久丢失焦点，如被其他播放器抢占
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLoss();
                }
                break;
            // 短暂丢失焦点，如来电
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLossTransient();
                }
                break;
            // 瞬间丢失焦点，如通知
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mAudioFocusListener != null) {
                    mAudioFocusListener.audioFocusLossDuck();
                }
                break;
            default:
        }
    }

    /**
     * 音频焦点改变,接口回调，
     */
    public interface AudioFocusListener {
        /**
         * 获得焦点回调处理
         */
        void audioFocusGrant();

        /**
         * 永久失去焦点回调处理
         */
        void audioFocusLoss();

        /**
         * 短暂失去焦点回调处理
         */
        void audioFocusLossTransient();

        /**
         * 瞬间失去焦点回调
         */
        void audioFocusLossDuck();
    }
}
