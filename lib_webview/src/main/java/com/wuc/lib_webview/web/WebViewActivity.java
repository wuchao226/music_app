package com.wuc.lib_webview.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_webview.web.route.WebRouteKeys;
import com.wuc.lib_webview.web.route.WebRouter;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

import androidx.annotation.Nullable;

/**
 * @author: wuchao
 * @date: 2019/2/1 15:56
 * @desciption:
 */
@Route(path = RouterPath.Web.PATH_WEB)
public abstract class WebViewActivity extends BaseActivity implements IWebViewInitializer {

    protected String mUrl;
    protected String mTitle;
    private ReferenceQueue<WebView> mWebViewQueue = new ReferenceQueue<>();
    private WebView mWebView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(WebRouteKeys.WEB_URL);
        mTitle = intent.getStringExtra(WebRouteKeys.WEB_TITLE);
        initWebView();
    }

    @SuppressLint("AddJavascriptInterface")
    protected void initWebView() {
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView = null;
        } else {
            final IWebViewInitializer initializer = setInitializer();
            if (initializer != null) {
                WeakReference<WebView> weakReference = new WeakReference<>(new WebView(this), mWebViewQueue);
                mWebView = weakReference.get();
                mWebView = initializer.initWebView(mWebView);
                mWebView.setWebViewClient(initializer.initWebViewClient());
                mWebView.setWebChromeClient(initializer.initWebChromeClient());
                mWebView.addJavascriptInterface(WebInterface.create(this), "jsContact");
            } else {
                throw new NullPointerException("Web Initializer is null");
            }
        }
    }

    protected abstract IWebViewInitializer setInitializer();

    protected void initData() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(WebRouteKeys.WEB_URL);
        mTitle = intent.getStringExtra(WebRouteKeys.WEB_TITLE);
        if (getUrl() != null) {
            //用原生方式模拟Web跳转并进行页面加载
            WebRouter.getInstance().loadPage(this, getUrl());
        }
    }

    public String getUrl() {
        if (mUrl == null) {
            throw new NullPointerException("url is null");
        }
        Log.d("WebViewActivity", "Web url-->" + mUrl);
        return mUrl;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mWebView != null) {
            mWebView.onPause();
            mWebView.pauseTimers();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) {
            mWebView.onResume();
            mWebView.resumeTimers();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            //移除WebView所有的View对象
            mWebView.removeAllViews();
            //销毁此WebView的内部状态
            mWebView.destroy();
            mWebView = null;
        }
    }

    public WebView getWebView() {
        if (mWebView == null) {
            throw new NullPointerException("Web is null");
        }
        return mWebView;
    }

}
