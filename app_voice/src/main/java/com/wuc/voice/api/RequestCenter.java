package com.wuc.voice.api;

import com.wuc.lib_network.okhttp.CommonOkHttpClient;
import com.wuc.lib_network.okhttp.listener.DisposeDataHandle;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;
import com.wuc.lib_network.okhttp.request.CommonRequest;
import com.wuc.lib_network.okhttp.request.RequestParams;
import com.wuc.voice.model.user.User;

/**
 * @author: wuchao
 * @date: 2019-09-19 10:18
 * @desciption: 存放应用中的所有请求
 */
public class RequestCenter {

    /**
     * 根据参数发送所有的post请求
     */
    public static void postRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.post(CommonRequest.createPostRequest(url, params),
                new DisposeDataHandle(listener, clazz));
    }

    /**
     * 用户登陆请求
     */
    public static void login(DisposeDataListener listener) {
        RequestParams params = new RequestParams();
        params.put("mb", "18734924592");
        params.put("pwd", "999999q");
        RequestCenter.getRequest(HttpConstants.LOGIN, params, listener, User.class);
    }

    /**
     * 根据参数发送所有的get请求
     */
    public static void getRequest(String url, RequestParams params, DisposeDataListener listener, Class<?> clazz) {
        CommonOkHttpClient.get(CommonRequest.createGetRequest(url, params),
                new DisposeDataHandle(listener, clazz));
    }

    /**
     * 所有请求相关地址
     */
    static class HttpConstants {
        //private static final String ROOT_URL = "http://imooc.com/api";
        private static final String ROOT_URL = "http://39.97.122.129";
        /**
         * 登陆接口
         */
        public static String LOGIN = ROOT_URL + "/module_voice/login_phone";
        /**
         * 首页请求接口
         */
        private static String HOME_RECOMMAND = ROOT_URL + "/module_voice/home_recommand";
        private static String HOME_RECOMMAND_MORE = ROOT_URL + "/module_voice/home_recommand_more";
        private static String HOME_FRIEND = ROOT_URL + "/module_voice/home_friend";
    }
}
