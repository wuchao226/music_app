package com.wuc.lib_webview.web.event;

import android.content.Context;
import android.webkit.WebView;

import com.wuc.lib_webview.web.WebViewActivity;


/**
 * @author: wuchao
 * @date: 2019/2/1 15:21
 * @desciption: js 互调处理基类事件
 */
public abstract class BaseWebEvent implements IWebEvent {

    private Context mContext;
    private String mAction;
    private WebViewActivity mActivity;
    private String mUrl;
    private WebView mWebView;

    public Context getContext() {
        return mContext;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public String getAction() {
        return mAction == null ? "" : mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public WebViewActivity getActivity() {
        return mActivity;
    }

    public void setActivity(WebViewActivity activity) {
        mActivity = activity;
    }

    public String getUrl() {
        return mUrl == null ? "" : mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }
}
