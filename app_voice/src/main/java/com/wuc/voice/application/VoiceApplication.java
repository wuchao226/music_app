package com.wuc.voice.application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.wuc.ft_audio.app.AudioHelper;
import com.wuc.lib_share.share.ShareManager;
import com.wuc.lib_update.app.UpdateHelper;

import androidx.multidex.MultiDexApplication;

/**
 * @author: wuchao
 * @date: 2019-10-09 18:01
 * @desciption:
 */
public class VoiceApplication extends MultiDexApplication {

    private static VoiceApplication mApplication = null;

    public static VoiceApplication getInstance() {
        return mApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        //音频SDK初始化
        AudioHelper.init(this);
        //分享组件初始化
        ShareManager.initSDK(this);
        //更新组件下载
        UpdateHelper.init(this);
        //ARouter初始化
        ARouter.init(this);
    }
}
