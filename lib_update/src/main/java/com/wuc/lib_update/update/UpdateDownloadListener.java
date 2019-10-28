package com.wuc.lib_update.update;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:49
 * @desciption: 下载不同状态接口回调
 */
public interface UpdateDownloadListener {
    /**
     * 下载请求开始回调
     */
    void onStarted();

    /**
     * 请求成功，下载前的准备回调
     *
     * @param contentLength 文件长度
     * @param downloadUrl   下载地址
     */
    void onPrepared(long contentLength, String downloadUrl);

    /**
     * 进度更新回调
     *
     * @param progress    进度
     * @param downloadUrl 下载地址
     */
    void onProgressChanged(int progress, String downloadUrl);

    /**
     * 下载暂停回调
     *
     * @param progress     进度
     * @param completeSize 已下载的文件大小
     * @param downloadUrl  下载地址
     */
    void onPaused(int progress, int completeSize, String downloadUrl);

    /**
     * 下载完成回调
     *
     * @param completeSize 已下载的文件大小
     * @param downloadUrl  下载地址
     */
    void onFinished(int completeSize, String downloadUrl);

    /**
     * 下载失败回调
     */
    void onFailure();
}
