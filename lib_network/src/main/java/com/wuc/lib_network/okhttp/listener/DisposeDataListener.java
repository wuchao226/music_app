package com.wuc.lib_network.okhttp.listener;

/**
 * @author: wuchao
 * @date: 2019-09-18 15:24
 * @desciption: 业务逻辑层真正处理的地方，包括java层异常和业务层异常
 */
public interface DisposeDataListener {
    /**
     * 请求成功回调事件处理
     */
    void onSuccess(Object responseObj);

    /**
     * 请求失败回调事件处理
     */
    void onFailure(Object reasonObj);
}
