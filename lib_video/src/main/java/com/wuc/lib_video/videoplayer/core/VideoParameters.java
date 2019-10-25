package com.wuc.lib_video.videoplayer.core;

import com.wuc.lib_video.videoplayer.constant.VideoConstant.AutoPlaySetting;

/**
 * @author: wuchao
 * @date: 2019-10-23 17:25
 * @desciption: 全局参数配置, 都用静态来保证统一性
 */
public class VideoParameters {
    /**
     * 用来记录可自动播放的条件，默认可以自动播放
     */
    private static AutoPlaySetting currentSetting = AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI;

    public static AutoPlaySetting getCurrentSetting() {
        return currentSetting;
    }

    public static void setCurrentSetting(AutoPlaySetting setting) {
        currentSetting = setting;
    }
}
