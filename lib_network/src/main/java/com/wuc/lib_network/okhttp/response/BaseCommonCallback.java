package com.wuc.lib_network.okhttp.response;

import android.os.Handler;

import com.wuc.lib_network.okhttp.exception.OkHttpException;
import com.wuc.lib_network.okhttp.listener.DisposeDataHandle;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

/**
 * @author: wuchao
 * @date: 2019-09-18 17:46
 * @desciption: 抽取回调公共数据
 */
public abstract class BaseCommonCallback implements Callback {

    /**
     * 有返回则对于http请求来说是成功的，但还有可能是业务逻辑上的错误
     */
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * the network relative error
     */
    protected final int NETWORK_ERROR = -1;
    /**
     * the JSON relative error
     */
    protected final int JSON_ERROR = -2;
    /**
     * the unknow error
     */
    protected final int OTHER_ERROR = -3;
    /**
     * the IO relative error
     */
    protected final int IO_ERROR = -4;

    /**
     * 将其它线程的数据转发到UI线程
     */
    protected Handler mDeliveryHandler;

    protected DisposeDataListener mListener;

    public BaseCommonCallback(DisposeDataHandle handle) {
        this.mListener = handle.mListener;
        //this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull final IOException ioexception) {
        /*
         * 此时还在非UI线程，因此要转发
         */
        if (mDeliveryHandler != null) {
            mDeliveryHandler.post(new Runnable() {
                @Override
                public void run() {
                    mListener.onFailure(new OkHttpException(NETWORK_ERROR, ioexception));
                }
            });
        }
    }
}
