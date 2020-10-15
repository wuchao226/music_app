package com.wuc.lib_audio.app.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.lib_audio.mediaplayer.core.AudioController;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.ft_audio.AudioService;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:12
 * @desciption: AudioService实现类
 */
@Route(path = RouterPath.Audio.PATH_AUDIO_SERVICE)
public class AudioServiceImpl implements AudioService {

    @Override
    public void pauseAudio() {
        AudioController.getInstance().pause();
    }

    @Override
    public void resumeAudio() {
        AudioController.getInstance().resume();
    }

    @Override
    public void init(Context context) {

    }
}
