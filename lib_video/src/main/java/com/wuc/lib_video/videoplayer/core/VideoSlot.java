package com.wuc.lib_video.videoplayer.core;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.service.audio.AudioService;
import com.wuc.lib_video.videoplayer.constant.VideoConstant;
import com.wuc.lib_video.videoplayer.core.view.CustomVideoView;
import com.wuc.lib_video.videoplayer.core.view.VideoFullDialog;
import com.wuc.lib_video.videoplayer.module.VideoValue;
import com.wuc.lib_video.videoplayer.utils.Utils;

/**
 * @author: wuchao
 * @date: 2019-10-23 22:09
 * @desciption: 视频业务逻辑层
 */
public class VideoSlot implements CustomVideoView.VideoPlayerListener {


    @Autowired(name = RouterPath.Audio.PATH_AUDIO_SERVICE)
    protected AudioService mAudioService;

    private Context mContext;
    private CustomVideoView mVideoView;
    /**
     * 要添加到的父容器
     */
    private ViewGroup mParentView;
    private VideoValue mVideoValue;
    /**
     * 与Context层的事件回调
     */
    private SlotListener mSlotListener;
    /**
     * 是否可自动暂停标志位
     */
    private boolean canPause = false;
    /**
     * 防止将要滑入滑出时播放器的状态改变
     */
    private int lastArea = 0;

    public VideoSlot(VideoValue value, SlotListener slotListener) {
        this(value, slotListener, null);
    }

    public VideoSlot(VideoValue value, SlotListener slotListener,
                     CustomVideoView.FrameImageLoadListener frameLoadListener) {
        ARouter.getInstance().inject(this);
        mVideoValue = value;
        mSlotListener = slotListener;
        mParentView = slotListener.getVideoParent();
        mContext = mParentView.getContext();
        initVideoView(frameLoadListener);
    }

    private void initVideoView(CustomVideoView.FrameImageLoadListener frameLoadListener) {
        mVideoView = new CustomVideoView(mContext, mParentView);
        if (mVideoValue != null) {
            mVideoView.setDataSource(mVideoValue.resource);
            mVideoView.setFrameURI(mVideoValue.thumb);
            mVideoView.setFrameLoadListener(frameLoadListener);
            mVideoView.setVideoPlayerListener(this);
        }
        RelativeLayout paddingView = new RelativeLayout(mContext);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black, null));
        } else {
            paddingView.setBackgroundColor(mContext.getResources().getColor(android.R.color.black));
        }
        paddingView.setLayoutParams(mVideoView.getLayoutParams());
        mParentView.addView(paddingView);
        mParentView.addView(mVideoView);
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        mContext = null;
        mVideoValue = null;
    }

    /**
     * 根据滑动距离来判断是否可以自动播放, 出现超过50%自动播放，离开超过50%,自动暂停
     */
    public void updateVideoInScrollView() {
        int currentArea = Utils.getVisiblePercent(mParentView);
        //小于0表示未出现在屏幕上，不做处理
        if (currentArea < 0) {
            return;
        }
        //刚要滑入和滑出时，异常状态的处理
        if (Math.abs(currentArea - lastArea) >= 100) {
            return;
        }
        if (currentArea < VideoConstant.VIDEO_SCREEN_PERCENT) {
            //进入自动暂停状态
            if (canPause) {
                pauseVideo();
                canPause = false;
            }
            lastArea = 0;
            // 滑动出50%后标记为从头开始播
            mVideoView.setIsComplete(false);
            mVideoView.setIsRealPause(false);
            return;
        }
        if (isRealPause() || isComplete()) {
            //进入手动暂停或者播放结束，播放结束和不满足自动播放条件都作为手动暂停
            pauseVideo();
            canPause = false;
            return;
        }
        //满足自动播放条件或者用户主动点击播放，开始播放
        if (Utils.canAutoPlay(mContext) || isPlaying()) {
            lastArea = currentArea;
            resumeVideo();
            canPause = true;
            mVideoView.setIsRealPause(false);
        } else {
            pauseVideo();
            //不能自动播放则设置为手动暂停效果
            mVideoView.setIsRealPause(true);
        }
    }

    /**
     * 暂停
     */
    public void pauseVideo() {
        if (mVideoView != null) {
            mVideoView.seekAndPause(0);
        }
    }

    /**
     * 播放器是否真正暂停
     */
    private boolean isRealPause() {
        if (mVideoView != null) {
            return mVideoView.isRealPause();
        }
        return false;
    }

    /**
     * 播放器是否播放完成
     */
    private boolean isComplete() {
        if (mVideoView != null) {
            return mVideoView.isComplete();
        }
        return false;
    }

    /**
     * 是否正在播放
     */
    private boolean isPlaying() {
        if (mVideoView != null) {
            return mVideoView.isPlaying();
        }
        return false;
    }

    private void resumeVideo() {
        if (mVideoView != null) {
            mVideoView.resume();
            /*if (isPlaying()) {
                //发自动播放监测
                sendSUSReport(true);
            }*/
        }
    }

    /**
     * 获取当前播放器播放的位置(秒)
     *
     * @return 已经播放了的秒数
     */
    private int getPosition() {
        return mVideoView.getCurrentPosition() / VideoConstant.MILLION_UNIT;
    }

    /**
     * 播放器播放到第几秒
     *
     * @param time 秒
     */
    @Override
    public void onBufferUpdate(int time) {

    }

    /**
     * 跳转到全屏的事件监听,
     */
    @Override
    public void onClickFullScreenBtn() {
        //获取videoview在当前界面的属性
        Bundle bundle = Utils.getViewProperty(mParentView);
        mParentView.removeView(mVideoView);
        VideoFullDialog dialog =
                new VideoFullDialog(mContext, mVideoView, mVideoValue, mVideoView.getCurrentPosition());
        dialog.setListener(new VideoFullDialog.FullToSmallListener() {
            @Override
            public void getCurrentPlayPosition(int position) {
                //全屏回到小屏继续处理
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                //全屏播放完毕回到小屏继续处理
                bigPlayComplete();
            }
        });
        //为Dialog设置播放器数据Bundle对象
        dialog.setViewBundle(bundle);
        dialog.setSlotListener(mSlotListener);
        dialog.show();
        //全屏暂停音乐播放
        mAudioService.pauseAudio();
    }

    /**
     * 全屏回到小屏继续播放
     */
    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        //防止动画导致偏离父容器
        mVideoView.setTranslationY(0);
        mVideoView.isShowFullBtn(true);
        //小屏静音播放
        mVideoView.mute(true);
        mVideoView.setVideoPlayerListener(this);
        mVideoView.seekAndResume(position);
        // 标为可自动暂停
        canPause = true;
        //小屏恢复音乐播放
        mAudioService.resumeAudio();
    }

    /**
     * 全屏播放完毕回到小屏事件
     */
    private void bigPlayComplete() {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        //防止动画导致偏离父容器
        mVideoView.setTranslationY(0);
        mVideoView.isShowFullBtn(true);
        mVideoView.mute(true);
        mVideoView.setVideoPlayerListener(this);
        mVideoView.seekAndPause(0);
        canPause = false;
    }

    /**
     * 点击视频区域的事件
     */
    @Override
    public void onClickVideo() {
        String desationUrl = mVideoValue.clickUrl;
        if (mSlotListener != null) {
            if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                mSlotListener.onClickVideo(desationUrl);
            }
        } else {
            //走默认样式
            /*if (mVideoView.isFrameHidden() && !TextUtils.isEmpty(desationUrl)) {
                Intent intent = new Intent(mContext, AdBrowserActivity.class);
                intent.putExtra(AdBrowserActivity.KEY_URL, mVideoValue.clickUrl);
                mContext.startActivity(intent);
            }*/
        }
    }

    /**
     * 点击返回按钮的事件监听
     */
    @Override
    public void onClickBackBtn() {

    }

    /**
     * 点击播放按钮的事件监听
     */
    @Override
    public void onClickPlay() {

    }

    /**
     * 视频加载成功的事件监听
     */
    @Override
    public void onVideoLoadSuccess() {
        if (mSlotListener != null) {
            mSlotListener.onVideoLoadSuccess();
        }
    }

    /**
     * 视频加载失败的事件监听
     */
    @Override
    public void onVideoLoadFailed() {
        if (mSlotListener != null) {
            mSlotListener.onVideoLoadFailed();
        }
        //加载失败全部回到初始状态
        canPause = false;
    }

    /**
     * 视频播放完成的事件监听
     */
    @Override
    public void onVideoLoadComplete() {
        if (mSlotListener != null) {
            mSlotListener.onVideoLoadComplete();
        }
        mVideoView.setIsRealPause(true);
    }

    /**
     * 传递消息到app层
     */
    public interface SlotListener {
        /**
         * 要添加到的父容器
         *
         * @return ViewGroup
         */
        ViewGroup getVideoParent();

        /**
         * 视频加载成功的事件监听
         */
        void onVideoLoadSuccess();

        /**
         * 视频加载失败的事件监听
         */
        void onVideoLoadFailed();

        /**
         * 视频播放完成的事件监听
         */
        void onVideoLoadComplete();

        /**
         * 点击视频区域的事件
         *
         * @param url url
         */
        void onClickVideo(String url);
    }
}
