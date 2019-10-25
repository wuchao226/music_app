package com.wuc.lib_video.videoplayer.core.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wuc.lib_video.R;
import com.wuc.lib_video.videoplayer.core.VideoSlot;
import com.wuc.lib_video.videoplayer.module.VideoValue;
import com.wuc.lib_video.videoplayer.utils.Utils;

/**
 * @author: wuchao
 * @date: 2019-10-24 16:55
 * @desciption: 全屏显示视频
 */
public class VideoFullDialog extends Dialog implements CustomVideoView.VideoPlayerListener {

    private CustomVideoView mVideoView;
    private RelativeLayout mRootView;
    private ViewGroup mParentView;
    private ImageView mBackButton;
    private VideoValue mVideoValue;
    private FullToSmallListener mFullToSmallListener;
    private boolean isFirst = true;
    /**
     * 动画要执行的平均值
     */
    private int deltaY;
    /**
     * 从小屏到全屏的播放位置
     */
    private int mPosition;

    private VideoSlot.SlotListener mSlotListener;
    private Bundle mStartBundle;
    /**
     * 用于Dialog出入场动画
     */
    private Bundle mEndBundle;

    public VideoFullDialog(Context context, CustomVideoView videoView, VideoValue videoValue, int position) {
        super(context, R.style.dialog_full_screen);
        mVideoValue = videoValue;
        mPosition = position;
        mVideoView = videoView;
    }

    public void setViewBundle(Bundle bundle) {
        mStartBundle = bundle;
    }

    public void setListener(FullToSmallListener listener) {
        this.mFullToSmallListener = listener;
    }

    public void setSlotListener(VideoSlot.SlotListener slotListener) {
        mSlotListener = slotListener;
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
        runExitAnimator();
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
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    /**
     * 视频加载失败的事件监听
     */
    @Override
    public void onVideoLoadFailed() {

    }

    /**
     * 视频播放完成的事件监听
     */
    @Override
    public void onVideoLoadComplete() {
        dismiss();
        if (mFullToSmallListener != null) {
            mFullToSmallListener.playComplete();
        }
    }

    @Override
    public void dismiss() {
        mParentView.removeView(mVideoView);
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_video_layout);
        initVideoView();
    }

    private void initVideoView() {
        mParentView = (RelativeLayout) findViewById(R.id.content_layout);
        mBackButton = findViewById(R.id.player_close_btn);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBackBtn();
            }
        });
        mRootView = findViewById(R.id.root_view);
        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickVideo();
            }
        });
        mVideoView.setVideoPlayerListener(this);
        mVideoView.mute(false);
        //将 VideoView 添加到新的 viewtree中
        mParentView.addView(mVideoView);
        mParentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mParentView.getViewTreeObserver().removeOnPreDrawListener(this);
                prepareScene();
                runEnterAnimation();
                return true;
            }
        });
    }

    /**
     * 准备动画所需数据
     */
    private void prepareScene() {
        mEndBundle = Utils.getViewProperty(mVideoView);
        //将desationview移到originalview位置处
        deltaY = (mStartBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP)
                - mEndBundle.getInt(Utils.PROPNAME_SCREENLOCATION_TOP));
        mVideoView.setTranslationY(deltaY);
    }

    /**
     * 准备入场动画
     */
    private void runEnterAnimation() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(0)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mRootView.setVisibility(View.VISIBLE);
                    }
                });
    }

    /**
     * 准备出场动画
     */
    private void runExitAnimator() {
        mVideoView.animate()
                .setDuration(200)
                .setInterpolator(new LinearInterpolator())
                .translationY(deltaY)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dismiss();
                        if (mFullToSmallListener != null) {
                            mFullToSmallListener.getCurrentPlayPosition(mVideoView.getCurrentPosition());
                        }
                    }
                }).start();
    }

    @Override
    public void onBackPressed() {
        onClickBackBtn();
        //禁止掉返回键本身的关闭功能,转为自己的关闭效果
        //super.onBackPressed();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //super.onWindowFocusChanged(hasFocus);
        //防止第一次，有些手机仍显示全屏按钮
        mVideoView.isShowFullBtn(false);
        if (!hasFocus) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.pauseForFullScreen();
        } else {
            //为了适配某些手机不执行seekandresume中的播放方法
            if (isFirst) {
                mVideoView.seekAndResume(mPosition);
            } else {
                mVideoView.resume();
            }
        }
        isFirst = false;
    }


    public interface FullToSmallListener {
        /**
         * 当前播放位置（全屏播放中点击关闭按钮或者back健时回调）
         *
         * @param position position
         */
        void getCurrentPlayPosition(int position);

        /**
         * 全屏播放结束时回调
         */
        void playComplete();
    }


}
