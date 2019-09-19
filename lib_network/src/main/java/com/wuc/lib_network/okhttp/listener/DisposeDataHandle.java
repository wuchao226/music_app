package com.wuc.lib_network.okhttp.listener;

/**
 * @author: wuchao
 * @date: 2019-09-18 15:47
 * @desciption: 数据处理
 */
public class DisposeDataHandle {

    public DisposeDataListener mListener = null;
    public Class<?> mClass = null;
    public String mSource = null;

    public DisposeDataHandle(DisposeDataListener listener) {
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz) {
        this.mListener = listener;
        this.mClass = clazz;
    }

    public DisposeDataHandle(DisposeDataListener listener, String source) {
        this.mListener = listener;
        this.mSource = source;
    }
}
