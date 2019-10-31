package com.wuc.lib_webview.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wuc.lib_webview.web.chromeclient.WebChromeClientImpl;
import com.wuc.lib_webview.web.client.WebViewClientImpl;
import com.wuc.lib_webview.web.route.WebRouteKeys;
import com.wuc.lib_webview.web.route.WebRouter;

import androidx.annotation.Nullable;

/**
 * @author: wuchao
 * @date: 2019-10-31 14:01
 * @desciption:
 */
public class WebViewActivityImpl extends WebViewActivity {

    private IPageLoadListener mIPageLoadListener = null;

    public static void open(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivityImpl.class);
        intent.putExtra(WebRouteKeys.WEB_URL, url);
        intent.putExtra(WebRouteKeys.WEB_TITLE, title);
        context.startActivity(intent);
    }

    public void setPageLoadListener(IPageLoadListener listener) {
        mIPageLoadListener = listener;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getWebView());
        if (getUrl() != null) {
            //用原生方式模拟Web跳转并进行页面加载
            WebRouter.getInstance().loadPage(this, getUrl());
        }
    }

    @Override
    protected IWebViewInitializer setInitializer() {
        return this;
    }

    @Override
    public WebView initWebView(WebView webView) {
        return new WebViewInitializer().createWebView(webView);
    }

    @Override
    public WebViewClient initWebViewClient() {
        final WebViewClientImpl client = new WebViewClientImpl(this);
        client.setPageLoadListener(mIPageLoadListener);
        return client;
    }

    @Override
    public WebChromeClient initWebChromeClient() {
        return new WebChromeClientImpl();
    }
}
