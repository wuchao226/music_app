package com.wuc.lib_video.videoplayer.constant;

/**
 * @author: wuchao
 * @date: 2019-10-23 14:42
 * @desciption: 用来存放sdk中用到的常量。
 */
public class VideoConstant {

    //素材类型
    public static final String MATERIAL_IMAGE = "image";
    public static final String MATERIAL_HTML = "html";
    //毫秒单位
    public static int MILLION_UNIT = 1000;
    /**
     * 自动播放阈值
     */
    public static int VIDEO_SCREEN_PERCENT = 50;
    public static float VIDEO_HEIGHT_PERCENT = 9 / 16.0f;

    /**
     * 自动播放条件
     */
    public enum AutoPlaySetting {
        //WiFi
        AUTO_PLAY_ONLY_WIFI,
        AUTO_PLAY_3G_4G_WIFI,
        AUTO_PLAY_NEVER
    }
}
