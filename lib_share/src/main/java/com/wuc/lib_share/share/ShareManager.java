package com.wuc.lib_share.share;

import android.content.Context;

import com.mob.MobSDK;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * @author: wuchao
 * @date: 2019-10-16 15:33
 * @desciption: 分享功能统一入口，负责发送数据到指定平台,可以优化为一个单例模式
 */
public class ShareManager {
    /**
     * 要分享到的平台
     */
    private Platform mCurrentPlatform;

    private ShareManager() {
    }

    /**
     * 第一个执行的方法,最好在程序入口入执行
     */
    public static void initSDK(Context context) {
        MobSDK.init(context);
    }

    /**
     * 分享数据到不同平台
     */
    public void shareData(ShareData shareData, PlatformActionListener listener) {
        switch (shareData.mPlatformType) {
            case QQ:
                mCurrentPlatform = ShareSDK.getPlatform(QQ.NAME);
                break;
            case QZone:
                mCurrentPlatform = ShareSDK.getPlatform(QZone.NAME);
                break;
            case WeChat:
                mCurrentPlatform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case WechatMoments:
                mCurrentPlatform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
            default:
                break;
        }
        //由应用层去处理回调,分享平台不关心。
        mCurrentPlatform.setPlatformActionListener(listener);
        mCurrentPlatform.share(shareData.mShareParams);
    }

    public static ShareManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final ShareManager INSTANCE = new ShareManager();
    }

    /**
     * 应用程序需要的平台
     */
    public enum PlatformType {
        //qq
        QQ,
        QZone,
        WeChat,
        WechatMoments
    }
}
