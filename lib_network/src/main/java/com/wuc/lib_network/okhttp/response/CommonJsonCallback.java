package com.wuc.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.wuc.lib_network.okhttp.exception.OkHttpException;
import com.wuc.lib_network.okhttp.listener.DisposeDataHandle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author: wuchao
 * @date: 2019-09-18 16:29
 * @desciption: 专门处理JSON的回调
 */
public class CommonJsonCallback extends BaseCommonCallback {

    private Class<?> mClass;

    public CommonJsonCallback(DisposeDataHandle handle) {
        super(handle);
        //this.mListener = handle.mListener;
        this.mClass = handle.mClass;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final String result = response.body().string();
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handleResponse(result);
            }
        });
    }

    private void handleResponse(Object responseObj) {
        if (responseObj == null || "".equals(responseObj.toString().trim())) {
            mListener.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        try {
            JSONObject result = new JSONObject(responseObj.toString());
            if (mClass == null) {
                mListener.onSuccess(result);
            } else {
                Object obj = new Gson().fromJson(responseObj.toString(), mClass);
                if (obj != null) {
                    mListener.onSuccess(obj);
                } else {
                    mListener.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                }
            }
        } catch (Exception e) {
            mListener.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
            e.printStackTrace();
        }
    }
}
