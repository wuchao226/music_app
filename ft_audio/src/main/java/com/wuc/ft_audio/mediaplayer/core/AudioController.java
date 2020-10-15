package com.wuc.ft_audio.mediaplayer.core;

import com.wuc.ft_audio.mediaplayer.db.GreenDaoHelper;
import com.wuc.ft_audio.mediaplayer.events.AudioCompleteEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioErrorEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioFavouriteEvent;
import com.wuc.ft_audio.mediaplayer.events.AudioPlayModeEvent;
import com.wuc.ft_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.wuc.ft_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author: wuchao
 * @date: 2019-10-09 14:48
 * @desciption: 控制播放逻辑类，控制播放器的播放模式，播放队列，当前播放歌曲等，
 * 注意添加一些控制方法时，要考虑是否需要增加Event,来更新UI
 */
public class AudioController {

    private AudioPlayer mAudioPlayer;
    /**
     * 播放队列,不能为空,不设置主动抛错
     */
    private ArrayList<AudioBean> mQueue = new ArrayList<>();
    private int mQueueIndex = 0;
    private PlayMode mPlayMode = PlayMode.LOOP;

    private AudioController() {
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();
    }

    public static AudioController getInstance() {
        return AudioController.Holder.INSTANCE;
    }

    public ArrayList<AudioBean> getQueue() {
        return mQueue != null ? mQueue : new ArrayList<AudioBean>();
    }

    /**
     * 设置播放队列
     */
    public void setQueue(ArrayList<AudioBean> queue) {
        setQueue(queue, 0);
    }

    /**
     * 设置播放队列
     */
    public void setQueue(ArrayList<AudioBean> queue, int queueIndex) {
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    /**
     * 队列头添加播放哥曲
     */
    public void addAudio(AudioBean bean) {
        this.addAudio(0, bean);
    }

    /**
     * 添加单一歌曲到指定位置
     */
    public void addAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            //没添加过此id的歌曲，添加且直播番放
            addCustomAudio(index, bean);
            setPlayIndex(index);
        } else {
            AudioBean currentBean = getNowPlaying();
            if (!currentBean.id.equals(bean.id)) {
                //添加过且不是当前播放，播，否则什么也不干
                setPlayIndex(query);
            }
        }
    }

    private int queryAudio(AudioBean bean) {
        return mQueue.indexOf(bean);
    }

    private void addCustomAudio(int index, AudioBean bean) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueue.add(index, bean);
    }

    /**
     * 指定播放的音乐
     */
    public void setPlayIndex(int index) {
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueueIndex = index;
        play();
    }

    /**
     * 对外提供的获取当前歌曲信息
     */
    public AudioBean getNowPlaying() {
        return getPlaying(mQueueIndex);
    }

    /**
     * 加载当前index歌曲
     */
    public void play() {
        AudioBean audioBean = mQueue.get(mQueueIndex);
        load(audioBean);
    }

    private AudioBean getPlaying(int index) {
        if (mQueue != null && !mQueue.isEmpty() && index >= 0 && index < mQueue.size()) {
            return mQueue.get(index);
        } else {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
    }

    private void load(AudioBean audioBean) {
        mAudioPlayer.load(audioBean);
    }

    /**
     * 播放/暂停切换
     */
    public void playOrPause() {
        if (isStartState()) {
            pause();
        } else if (isPauseState()) {
            resume();
        }
    }

    /**
     * 对外提供是否播放中状态
     */
    public boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    public void pause() {
        mAudioPlayer.pause();
    }

    /**
     * 对外提提供是否暂停状态
     */
    public boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    public void resume() {
        mAudioPlayer.resume();
    }

    /**
     * 获取播放器当前状态
     */
    private CustomMediaPlayer.Status getStatus() {
        return mAudioPlayer.getStatus();
    }

    /**
     * 加载previous index歌曲
     */
    public void previous() {
        AudioBean audioBean = getPreviousPlaying();
        load(audioBean);
    }

    /**
     * 上一首音频数据
     */
    private AudioBean getPreviousPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + mQueue.size() - 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
            default:
        }
        return null;
    }

    /**
     * 对外提供获取当前播放时间
     */
    public int getNowPlayTime() {
        return mAudioPlayer.getCurrentPosition();
    }

    /**
     * 对外提供获取总播放时间
     */
    public int getTotalPlayTime() {
        return mAudioPlayer.getDuration();
    }

    public void release() {
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取当前播放方式
     */
    public PlayMode getPlayMode() {
        return mPlayMode;
    }

    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
        //还要对外发送切换事件，更新UI
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }

    public int getQueueIndex() {
        return mQueueIndex;
    }

    /**
     * 插放完毕事件处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioCompleteEvent(
            AudioCompleteEvent event) {
        next();
    }

    /**
     * 加载next index歌曲
     */
    public void next() {
        AudioBean audioBean = getNextPlaying();
        load(audioBean);
    }

    /**
     * 下一首音频数据
     */
    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                return getPlaying(mQueueIndex);
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                return getPlaying(mQueueIndex);
            case REPEAT:
                return getPlaying(mQueueIndex);
            default:
        }
        return null;
    }

    /**
     * 播放出错事件处理
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioErrorEvent(AudioErrorEvent event) {
        next();
    }

    /**
     * 添加/移除到收藏
     */
    public void changeFavourite() {
        if (null != GreenDaoHelper.selectFavourite(getNowPlaying())) {
            //已收藏，移除
            GreenDaoHelper.removeFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(false));
        } else {
            //未收藏，添加收藏
            GreenDaoHelper.addFavourite(getNowPlaying());
            EventBus.getDefault().post(new AudioFavouriteEvent(true));
        }
    }

    /**
     * 播放方式
     */
    public enum PlayMode {
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    private static class Holder {
        private static final AudioController INSTANCE = new AudioController();
    }
}
