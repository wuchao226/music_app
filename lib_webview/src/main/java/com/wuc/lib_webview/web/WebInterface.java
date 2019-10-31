package com.wuc.lib_webview.web;

import android.webkit.JavascriptInterface;

import com.alibaba.fastjson.JSON;
import com.wuc.lib_webview.web.event.BaseWebEvent;
import com.wuc.lib_webview.web.event.WebEventManager;


/**
 * @author: wuchao
 * @date: 2019/2/1 14:53
 * @desciption: 与JS对象映射关系的类
 */
public class WebInterface {

    private WebViewActivity mActivity;

    private WebInterface(WebViewActivity activity) {
        mActivity = activity;
    }

    static WebInterface create(WebViewActivity activity) {
        return new WebInterface(activity);
    }

    @JavascriptInterface
    public String event(String params) {
        String action = JSON.parseObject(params).getString("action");
        BaseWebEvent event = WebEventManager.getInstance().createEvent(action);
        if (event != null) {
            event.setAction(action);
            event.setContext(mActivity);
            event.setActivity(mActivity);
            event.setUrl(mActivity.getUrl());
            return event.execute(params);
        }
        return null;
    }
}
