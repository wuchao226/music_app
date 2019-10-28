package com.wuc.lib_base.service.update;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * @author: wuchao
 * @date: 2019-10-28 11:32
 * @desciption:
 */
public interface UpdateNotificationService extends IProvider {
    /**
     * 获取UpdateReceiver 的包名
     * @return String
     */
    String getUpdatePackageName();
}
