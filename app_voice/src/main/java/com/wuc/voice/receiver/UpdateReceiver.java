package com.wuc.voice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wuc.lib_update.app.UpdateHelper;
import com.wuc.voice.utils.Utils;

/**
 * @author: wuchao
 * @date: 2019-10-28 13:46
 * @desciption: update 更新组件广播接收器
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("install2", "home");
        //启动安装页面
        context.startActivity(
                Utils.getInstallApkIntent(context, intent.getStringExtra(UpdateHelper.UPDATE_FILE_KEY)));

    }
}
