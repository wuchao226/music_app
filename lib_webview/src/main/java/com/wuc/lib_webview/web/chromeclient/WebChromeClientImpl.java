package com.wuc.lib_webview.web.chromeclient;

import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * @author: wuchao
 * @date: 2019/1/31 17:26
 * @desciption: WebChromeClient 实现类
 */
public class WebChromeClientImpl extends WebChromeClient {

    /*private ProgressBar mProgressBar;

    public WebChromeClientImpl(ProgressBar progressBar) {
        mProgressBar = progressBar;
    }*/

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        /*if (mProgressBar != null) {
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
            } else {
                if (mProgressBar.getVisibility() == View.GONE) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
        }*/
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        return super.onJsAlert(view, url, message, result);
    }
}
