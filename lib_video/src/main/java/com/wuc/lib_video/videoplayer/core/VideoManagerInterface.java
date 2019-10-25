package com.wuc.lib_video.videoplayer.core;

/**
 * @author: wuchao
 * @date: 2019-10-24 17:46
 * @desciption: 最终通知应用层广告是否成功
 */
public interface VideoManagerInterface {
    /**
     * 视频加载成功的事件监听
     */
    void onVideoSuccess();
    /**
     * 视频加载失败的事件监听
     */
    void onVideoFailed();
    /**
     * 视频播放完成的事件监听
     */
    void onVideoComplete();
    /**
     * 点击视频区域的事件
     *
     * @param url url
     */
    void onClickVideo(String url);
}
