package com.wuc.voice.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.service.update.UpdateNotificationService;
import com.wuc.voice.receiver.UpdateReceiver;

/**
 * @author: wuchao
 * @date: 2019-10-28 11:33
 * @desciption:
 */
@Route(path = RouterPath.Voice.PATH_VOICE_SERVICE)
public class UpdateNotificationServiceImpl implements UpdateNotificationService {

    @Override
    public String getUpdatePackageName() {
        return UpdateReceiver.class.getPackage().getName() + ".UpdateReceiver";
    }

    @Override
    public void init(Context context) {

    }
}
