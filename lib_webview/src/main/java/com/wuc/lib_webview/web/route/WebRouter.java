package com.wuc.lib_webview.web.route;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.URLUtil;
import android.webkit.WebView;

import com.wuc.lib_webview.web.WebViewActivity;
import com.wuc.lib_webview.web.WebViewActivityImpl;

import androidx.core.content.ContextCompat;

/**
 * @author: wuchao
 * @date: 2019/2/1 17:04
 * @desciption: js 处理
 */
public class WebRouter {

    private WebRouter() {
    }

    public static WebRouter getInstance() {
        return Holder.INSTANCE;
    }

    public boolean handleWebView(Context context, String url) {
        if (url.contains("tel:")) {
            callPhone(context, url);
            return true;
        }
        WebViewActivityImpl.open(context, url, "");
        return true;
    }

    /**
     * 拨打电话
     */
    private void callPhone(Context context, String url) {
        final Intent intent = new Intent(Intent.ACTION_DIAL);
        final Uri data = Uri.parse(url);
        intent.setData(data);
        ContextCompat.startActivity(context, intent, null);
    }

    public final void loadPage(WebViewActivity activity, String url) {
        loadPage(activity.getWebView(), url);
    }

    private void loadPage(WebView webView, String url) {
        if (URLUtil.isNetworkUrl(url) || URLUtil.isAssetUrl(url)) {
            loadWebPage(webView, url);
        } else {
            loadLocalPage(webView, url);
        }
    }

    private void loadWebPage(WebView webView, String url) {
        if (webView != null) {
            webView.loadUrl(url);
        } else {
            throw new NullPointerException("Web is null!");
        }
    }

    private void loadLocalPage(WebView webView, String url) {
        loadWebPage(webView, "file:///android_asset/" + url);
    }

    private static class Holder {
        private static final WebRouter INSTANCE = new WebRouter();
    }
}
