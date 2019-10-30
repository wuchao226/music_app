package com.wuc.lib_base.router;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:22
 * @desciption: 模块路由 路径定义
 */
public class RouterPath {

    public class QrCode {
        public static final String PATH_CAPTURE_ZXING_ACTIVITY = "/qrcode/capture_zxing_activity";
        public static final String PATH_CAPTURE_ZBAR_ACTIVITY = "/qrcode/capture_zbar_activity";
    }

    public class WebView {
        public static final String PATH_WEB_ACTIVITY = "/webview/web_activity";
    }

    /**
     * 音频模块
     */
    public class Audio {
        /**
         * 音频服务router
         */
        public static final String PATH_AUDIO_SERVICE = "/audio/audio_service";
        /**
         * 播放音乐
         */
        public static final String PATH_MUSIC_ACTIVITY = "/audio/music_activity";
    }

    /**
     * 首页更新服务模块
     */
    public class Voice {
        /**
         * 首页更新服务router
         */
        public static final String PATH_VOICE_SERVICE = "/voice/voice_service";
    }
}
