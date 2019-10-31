package com.wuc.lib_webview.web.client;

import android.graphics.Bitmap;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wuc.lib_webview.web.IPageLoadListener;
import com.wuc.lib_webview.web.WebViewActivity;
import com.wuc.lib_webview.web.route.WebRouter;


/**
 * @author: wuchao
 * @date: 2019/1/31 15:18
 * @desciption: WebViewClient 实现类
 */
public class WebViewClientImpl extends WebViewClient {

    private WebViewActivity mActivity;
    private IPageLoadListener mIPageLoadListener = null;

    public WebViewClientImpl(WebViewActivity activity) {
        mActivity = activity;
    }

    public void setPageLoadListener(IPageLoadListener listener) {
        this.mIPageLoadListener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return WebRouter.getInstance().handleWebView(mActivity, url);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        return WebRouter.getInstance().handleWebView(mActivity, request.getUrl().toString());
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mIPageLoadListener != null) {
            mIPageLoadListener.onLoadStart();
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        syncCookie();
        if (mIPageLoadListener != null) {
            mIPageLoadListener.onLoadEnd();
        }
    }

    /**
     * 获取浏览器Cookie
     */
    private void syncCookie() {

    }

}
