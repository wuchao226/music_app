package com.wuc.lib_audio.app;

import android.content.Context;

import com.wuc.lib_audio.mediaplayer.core.MusicService;
import com.wuc.lib_audio.mediaplayer.db.GreenDaoHelper;
import com.wuc.lib_audio.mediaplayer.model.AudioBean;

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
}
