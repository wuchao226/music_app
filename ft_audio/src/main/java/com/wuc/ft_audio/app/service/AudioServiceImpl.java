package com.wuc.ft_audio.app.service;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.ft_audio.app.AudioHelper;
import com.wuc.ft_audio.mediaplayer.core.AudioController;
import com.wuc.lib_base.ft_audio.model.CommonAudioBean;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.ft_audio.service.AudioService;
import java.util.ArrayList;

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

  @Override public void addAudio(Activity activity, CommonAudioBean audioBean) {
    AudioHelper.addAudio(activity, audioBean);
  }

  @Override public void startMusicService(ArrayList<CommonAudioBean> audioBeans) {
    AudioHelper.startMusicService(audioBeans);
  }

  @Override
  public void init(Context context) {

  }
}
