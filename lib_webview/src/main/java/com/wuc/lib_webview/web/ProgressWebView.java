package com.wuc.lib_webview.web;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.wuc.lib_webview.R;


/**
 * @author: wuchao
 * @date: 2019/2/1 09:20
 * @desciption: 带有进度条的 Web
 */
public class ProgressWebView extends WebView {

    private ProgressBar mProgressBar;

    public ProgressWebView(Context context) {
        this(context, null);
    }

    public ProgressWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(R.drawable.progress_bar_states, null);
        } else {
            drawable = context.getResources().getDrawable(R.drawable.progress_bar_states);
        }
        mProgressBar.setProgressDrawable(drawable);
        addView(mProgressBar);
        //setWebChromeClient(new WebChromeClientImpl(mProgressBar));
    }

}
