package com.wuc.lib_network.okhttp.request;

import java.io.File;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author: wuchao
 * @date: 2019-09-18 13:29
 * @desciption: 接收请求参数，生成 request 对象；对外提供get/post/文件上传请求
 */
public class CommonRequest {

    /**
     * create the key-value Request
     *
     * @param url    url
     * @param params params
     * @return Request
     */
    public static Request createPostRequest(String url, RequestParams params) {
        return createPostRequest(url, params, null);
    }

    /**
     * 带请求头的Post请求
     *
     * @param url     url
     * @param params  params
     * @param headers headers
     * @return Request
     */
    public static Request createPostRequest(String url, RequestParams params, RequestParams headers) {
        FormBody.Builder mFormBodyBuild = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                mFormBodyBuild.add(entry.getKey(), entry.getValue());
            }
        }
        //添加请求头
        Headers.Builder mHeadersBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeadersBuild.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody mFormBody = mFormBodyBuild.build();
        Headers mHeader = mHeadersBuild.build();
        return new Request.Builder()
                .url(url)
                .post(mFormBody)
                .headers(mHeader)
                .build();
    }

    /**
     * ressemble the params to the url
     *
     * @param url    url
     * @param params params
     * @return Request
     */
    public static Request createGetRequest(String url, RequestParams params) {
        return createGetRequest(url, params, null);
    }

    /**
     * 可以带请求头的Get请求
     *
     * @param url     url
     * @param params  params
     * @param headers headers
     * @return Request
     */
    public static Request createGetRequest(String url, RequestParams params, RequestParams headers) {
        StringBuilder urlBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                urlBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        //添加请求头
        Headers.Builder mHeadersBuild = new Headers.Builder();
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.urlParams.entrySet()) {
                mHeadersBuild.add(entry.getKey(), entry.getValue());
            }
        }
        Headers mHeader = mHeadersBuild.build();
        return new Request.Builder()
                .url(urlBuilder.substring(0, urlBuilder.length() - 1))
                .get()
                .headers(mHeader)
                .build();
    }

    /**
     * 文件上传请求
     */
    private static final MediaType FILE_TYPE = MediaType.parse("application/octet-stream");
    private static final MediaType JSON_TYPE = MediaType.parse("application/json");

    /**
     * 文件上传请求
     *
     * @param url    url
     * @param params params
     * @return Request
     */
    public static Request createMultiPostRequest(String url, RequestParams params) {
        MultipartBody.Builder requestBody = new MultipartBody.Builder();
        requestBody.setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.fileParams.entrySet()) {
                if (entry.getValue() instanceof File) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create((File) entry.getValue(), FILE_TYPE));
                } else if (entry.getValue() instanceof String) {
                    requestBody.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + entry.getKey() + "\""),
                            RequestBody.create((String) entry.getValue(), null));
                }
            }
        }
        return new Request.Builder().url(url).post(requestBody.build()).build();
    }
}
