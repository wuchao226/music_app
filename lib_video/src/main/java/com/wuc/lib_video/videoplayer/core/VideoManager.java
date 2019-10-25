package com.wuc.lib_video.videoplayer.core;

import android.view.ViewGroup;

import com.wuc.lib_video.videoplayer.core.view.CustomVideoView;
import com.wuc.lib_video.videoplayer.module.VideoValue;

/**
 * @author: wuchao
 * @date: 2019-10-23 11:06
 * @desciption: 管理slot, 与外界进行通信
 */
public class VideoManager implements VideoSlot.SlotListener {

    private ViewGroup mParentView;
    private VideoSlot mVideoSlot;
    private VideoValue mVideoValue;
    private VideoManagerInterface mListener;
    private CustomVideoView.FrameImageLoadListener mFrameLoadListener;

    public VideoManager(ViewGroup parentView, VideoValue value, CustomVideoView.FrameImageLoadListener imageLoadListener) {
        mParentView = parentView;
        mVideoValue = value;
        mFrameLoadListener = imageLoadListener;
        load();
    }

    /**
     * init the ad,不调用则不会创建videoview
     */
    private void load() {
        if (mVideoValue != null && mVideoValue.resource != null) {
            mVideoSlot = new VideoSlot(mVideoValue, this, mFrameLoadListener);
        } else {
            //创建空的slot,不响应任何事件
            mVideoSlot = new VideoSlot(null, this, mFrameLoadListener);
            if (mListener != null) {
                mListener.onVideoFailed();
            }
        }
    }

    /**
     * 最终通知应用层广告是否成功监听事件
     *
     * @param listener listener
     */
    public void setListener(VideoManagerInterface listener) {
        mListener = listener;
    }

    /**
     * 根据滑动距离来判断是否可以自动播放, 出现超过50%自动播放，离开超过50%,自动暂停
     */
    public void updateVideoInScrollView() {
        if (mVideoSlot != null) {
            mVideoSlot.updateVideoInScrollView();
        }
    }

    /**
     * release the video
     */
    public void destroy() {
        mVideoSlot.destroy();
    }

    @Override
    public ViewGroup getVideoParent() {
        return mParentView;
    }

    @Override
    public void onVideoLoadSuccess() {
        if (mListener != null) {
            mListener.onVideoSuccess();
        }
    }

    @Override
    public void onVideoLoadFailed() {
        if (mListener != null) {
            mListener.onVideoFailed();
        }
    }

    @Override
    public void onVideoLoadComplete() {
        if (mListener != null) {
            mListener.onVideoComplete();
        }
    }

    @Override
    public void onClickVideo(String url) {
        if (mListener != null) {
            mListener.onClickVideo(url);
        } else {
//            Intent intent = new Intent(mParentView.getContext(), AdBrowserActivity.class);
//            intent.putExtra(AdBrowserActivity.KEY_URL, url);
//            mParentView.getContext().startActivity(intent);
        }
    }
}
