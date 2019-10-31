package com.wuc.lib_webview.web.event;


import android.util.Log;

/**
 * @author: wuchao
 * @date: 2019/2/1 15:30
 * @desciption: 默认 event
 */
public class UndefineEvent extends BaseWebEvent {
    @Override
    public String execute(String params) {
        Log.e("UndefineEvent", params);
        return null;
    }
}
