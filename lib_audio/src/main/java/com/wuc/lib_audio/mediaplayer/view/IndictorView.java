package com.wuc.lib_audio.mediaplayer.view;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.wuc.lib_audio.R;
import com.wuc.lib_audio.mediaplayer.core.AudioController;
import com.wuc.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.wuc.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.wuc.lib_audio.mediaplayer.events.AudioStartEvent;
import com.wuc.lib_audio.mediaplayer.model.AudioBean;
import com.wuc.lib_audio.mediaplayer.view.adapter.MusicPagerAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.ViewPager;

/**
 * @author: wuchao
 * @date: 2019-10-12 16:03
 * @desciption: 音乐播放页面唱针布局
 */
public class IndictorView extends RelativeLayout implements ViewPager.OnPageChangeListener {

    private Context mContext;

    /*
     * view相关
     */
    private AppCompatImageView mImageView;
    private ViewPager mViewPager;
    private MusicPagerAdapter mMusicPagerAdapter;
    /*
     * data
     */
    /**
     * 当前播放歌曲
     */
    private AudioBean mAudioBean;
    /**
     * 播放队列
     */
    private ArrayList<AudioBean> mQueue;


    public IndictorView(Context context) {
        this(context, null);
    }

    public IndictorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndictorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        EventBus.getDefault().register(this);
        initData();
    }

    private void initData() {
        mAudioBean = AudioController.getInstance().getNowPlaying();
        mQueue = AudioController.getInstance().getQueue();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.indictor_view, this);
        mImageView = rootView.findViewById(R.id.tip_view);
        mViewPager = rootView.findViewById(R.id.view_pager);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mMusicPagerAdapter = new MusicPagerAdapter(mContext, mQueue, null);
        mViewPager.setAdapter(mMusicPagerAdapter);
        //要在UI初始化完，否则会多一次listener响应
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //指定要播放的position
        AudioController.getInstance().setPlayIndex(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                showPlayView();
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                showPauseView();
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
            default:
        }
    }

    private void showPlayView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) {
            if (anim.isPaused()) {
                anim.resume();
            } else {
                anim.start();
            }
        }
    }

    private void showPauseView() {
        Animator anim = mMusicPagerAdapter.getAnim(mViewPager.getCurrentItem());
        if (anim != null) {
            anim.pause();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event) {
        //更新viewpager为load状态
        mAudioBean = event.mAudioBean;
        showLoadView(true);
    }

    private void showLoadView(boolean isSmooth) {
        mViewPager.setCurrentItem(mQueue.indexOf(mAudioBean), isSmooth);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event) {
        //更新activity为暂停状态
        showPauseView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event) {
        //更新activity为播放状态
        showPlayView();
    }
}
