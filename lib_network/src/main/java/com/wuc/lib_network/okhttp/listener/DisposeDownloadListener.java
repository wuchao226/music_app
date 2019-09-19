package com.wuc.lib_network.okhttp.listener;

/**
 * @author: wuchao
 * @date: 2019-09-18 16:28
 * @desciption: 监听下载进度
 */
public interface DisposeDownloadListener extends DisposeDataListener {
    void onProgress(int progress);
}
