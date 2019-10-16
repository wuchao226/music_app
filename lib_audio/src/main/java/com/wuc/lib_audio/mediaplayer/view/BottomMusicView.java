package com.wuc.lib_audio.mediaplayer.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.wuc.lib_audio.R;
import com.wuc.lib_audio.mediaplayer.core.AudioController;
import com.wuc.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.wuc.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.wuc.lib_audio.mediaplayer.events.AudioProgressEvent;
import com.wuc.lib_audio.mediaplayer.events.AudioStartEvent;
import com.wuc.lib_audio.mediaplayer.model.AudioBean;
import com.wuc.lib_image_loader.ImageLoaderManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author: wuchao
 * @date: 2019-10-09 22:03
 * @desciption: 播放器底部View
 */
public class BottomMusicView extends RelativeLayout {

    private Context mContext;

    /*
     * View
     */
    private AppCompatImageView mLeftView;
    private AppCompatTextView mTitleView;
    private AppCompatTextView mAlbumView;
    private AppCompatImageView mPlayView;
    private AppCompatImageView mRightView;
    private AudioBean mAudioBean;

    public BottomMusicView(Context context) {
        this(context, null);
    }

    public BottomMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.bottom_view, this);
        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳到音乐播放Activity
                MusicPlayerActivity.start((Activity) mContext);
            }
        });
        mLeftView = rootView.findViewById(R.id.album_view);
        final ObjectAnimator animator = ObjectAnimator.ofFloat(mLeftView, View.ROTATION.getName(), 0f, 360f);
        animator.setDuration(10000);
        animator.setInterpolator(new LinearInterpolator());
        //无限重复动画
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();

        mTitleView = rootView.findViewById(R.id.audio_name_view);
        mAlbumView = rootView.findViewById(R.id.audio_album_view);
        mPlayView = rootView.findViewById(R.id.play_view);
        mPlayView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放暂停事件
                AudioController.getInstance().playOrPause();
                if (AudioController.getInstance().isPauseState()) {
                    animator.pause();
                } else if (AudioController.getInstance().isStartState()) {
                    animator.resume();
                }
            }
        });

        mRightView = rootView.findViewById(R.id.show_list_view);
        mRightView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示音乐列表对话框
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新当前view为load状态
        mAudioBean = event.mAudioBean;
        showLoadView();
    }

    private void showLoadView() {
        //目前loading状态的UI处理与pause逻辑一样，分开为了以后好扩展
        if (mAudioBean != null) {
            ImageLoaderManager.getInstance().displayImageForCircle(mLeftView, mAudioBean.albumPic);
            mTitleView.setText(mAudioBean.name);
            mAlbumView.setText(mAudioBean.album);
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新当前view为暂停状态
        showPauseView();
    }

    private void showPauseView() {
        if (mAudioBean != null) {
            mPlayView.setImageResource(R.mipmap.note_btn_play_white);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新当前view为播放状态
        showPlayView();
    }

    private void showPlayView() {
        if (mAudioBean != null) {
            mPlayView.setImageResource(R.mipmap.note_btn_pause_white);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioProgrssEvent(AudioProgressEvent event) {
        //更新当前view的播放进度
    }
}
