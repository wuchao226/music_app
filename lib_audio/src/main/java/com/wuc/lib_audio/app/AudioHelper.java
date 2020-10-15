package com.wuc.lib_audio.app;

import android.app.Activity;
import android.content.Context;

import com.wuc.lib_audio.mediaplayer.core.AudioController;
import com.wuc.lib_audio.mediaplayer.core.MusicService;
import com.wuc.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.wuc.lib_audio.mediaplayer.model.AudioBean;
import com.wuc.lib_audio.mediaplayer.utils.Utils;
import com.wuc.lib_audio.mediaplayer.view.MusicPlayerActivity;

import com.wuc.lib_base.ft_audio.model.CommonAudioBean;
import java.util.ArrayList;

/**
 * @author: wuchao
 * @date: 2019-10-08 15:29
 * @desciption: 外观模式；唯一与外界通信的帮助类，负责调用组件内的各模块功能
 */
public final class AudioHelper {
    /**
     * SDK全局Context, 供子模块用
     */
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        GreenDaoHelper.initDatabase();
    }

    public static Context getContext() {
        return mContext;
    }

    /**
     * 外部启动MusicService方法
     */
    public static void startMusicService(ArrayList<AudioBean> audioBeans) {
        MusicService.startMusicService(audioBeans);
    }

    public static void addAudio(Activity activity, CommonAudioBean bean) {
        AudioController.getInstance().addAudio(Utils.convertFrom(bean));
        MusicPlayerActivity.start(activity);
    }

    public static void pauseAudio() {
        AudioController.getInstance().pause();
    }

    public static void resumeAudio() {
        AudioController.getInstance().resume();
    }
}
