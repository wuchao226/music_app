package com.wuc.lib_network.okhttp.listener;

import java.util.ArrayList;

/**
 * @author: wuchao
 * @date: 2019-09-18 16:28
 * @desciption: 当需要专门处理Cookie时创建此回调接口
 */
public interface DisposeHandleCookieListener extends DisposeDataListener {
    public void onCookie(ArrayList<String> cookieStrLists);
}
