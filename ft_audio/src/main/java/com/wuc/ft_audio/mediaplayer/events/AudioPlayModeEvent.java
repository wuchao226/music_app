package com.wuc.ft_audio.mediaplayer.events;


import com.wuc.ft_audio.mediaplayer.core.AudioController;

/**
 * @author: wuchao
 * @date: 2019-10-09 16:44
 * @desciption: 播放模式切换事件
 */
public class AudioPlayModeEvent {
    public AudioController.PlayMode mPlayMode;

    public AudioPlayModeEvent(AudioController.PlayMode playMode) {
        this.mPlayMode = playMode;
    }
}
