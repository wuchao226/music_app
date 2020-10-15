package com.wuc.lib_base.ft_audio.service;

import android.app.Activity;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.wuc.lib_base.ft_audio.model.CommonAudioBean;
import java.util.ArrayList;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:09
 * @desciption: 音频基本础对外接口
 */
public interface AudioService extends IProvider {
  /**
   * 暂停音频
   */
  void pauseAudio();

  /**
   * 恢复播放音频
   */
  void resumeAudio();

  void addAudio(Activity activity, CommonAudioBean audioBean);

  void startMusicService(ArrayList<CommonAudioBean> audioBeans);
}
