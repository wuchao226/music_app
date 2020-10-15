package com.wuc.lib_base.ft_audio;

import com.alibaba.android.arouter.facade.template.IProvider;

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
}
