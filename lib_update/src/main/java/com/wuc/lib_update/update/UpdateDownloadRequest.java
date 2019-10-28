package com.wuc.lib_update.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:49
 * @desciption: 负责处理文件的下载和线程间的通信
 */
public class UpdateDownloadRequest implements Runnable {

    /**
     * 开始下载的位置
     */
    private int startPos = 0;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 文件保存路径
     */
    private String localFilePath;
    /**
     * 事件回调
     */
    private UpdateDownloadListener mDownloadListener;
    private DownloadResponseHandler mDownloadHandler;
    /**
     * 下载标志位
     */
    private boolean isDownloading = false;
    /**
     * 文件长度
     */
    private long mContentLength;

    public UpdateDownloadRequest(String downloadUrl, String localFilePath,
                                 UpdateDownloadListener downloadListener) {
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        mDownloadListener = downloadListener;
        mDownloadHandler = new DownloadResponseHandler();
        isDownloading = true;
    }

    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            if (mDownloadHandler != null) {
                mDownloadHandler.sendFailureMessage(FailureCode.IO);
            }
        } catch (InterruptedException e) {
            if (mDownloadHandler != null) {
                mDownloadHandler.sendFailureMessage(FailureCode.Interrupted);
            }
        }
    }

    /**
     * 建立连接
     */
    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Range", "bytes=" + startPos + "-");
                connection.setRequestProperty("Connection", "Keep-Alive");
                //阻塞当前线程
                connection.connect();
                mContentLength = connection.getContentLength();
                if (!Thread.currentThread().isInterrupted()) {
                    if (mDownloadHandler != null) {
                        //完成文件下载,取得与远程文件的流
                        mDownloadHandler.sendResponseMessage(connection.getInputStream());
                    }
                }
            } catch (IOException e) {
                if (!Thread.currentThread().isInterrupted()) {
                    throw e;
                }

            }
        }
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void stopDownloading() {
        isDownloading = false;
    }

    /**
     * 下载过程中所有可能出现的异常情况
     */
    public enum FailureCode {
        //
        UnknownHost,
        Socket,
        SocketTimeout,
        ConnectTimeout,
        IO,
        HttpResponse,
        JSON,
        Interrupted
    }

    /**
     * 下载文件，并发送消息和回调接口
     */
    public class DownloadResponseHandler {

        protected static final int SUCCESS_MESSAGE = 0;
        protected static final int FAILURE_MESSAGE = 1;
        protected static final int START_MESSAGE = 2;
        protected static final int FINISH_MESSAGE = 3;
        protected static final int NETWORK_OFF = 4;
        protected static final int PROGRESS_CHANGED = 5;
        protected static final int PAUSED_MESSAGE = 6;

        private Handler mHandler;
        private int mCompleteSize = 0;
        private int progress = 0;

        public DownloadResponseHandler() {
            if (Looper.myLooper() != null) {
                mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        handleSelfMessage(msg);
                    }
                };
            }
        }

        private void handleSelfMessage(Message message) {
            Object[] response;
            switch (message.what) {
                default:
                    break;
                case FAILURE_MESSAGE:
                    response = (Object[]) message.obj;
                    handleFailureMessage((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGED:
                    response = (Object[]) message.obj;
                    handleProgressChangedMessage((Integer) response[0]);
                    break;
                case PAUSED_MESSAGE:
                    handlePausedMessage();
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }
        }

        /**
         * 失败消息的处理逻辑
         */
        protected void handleFailureMessage(FailureCode failureCode) {
            onFailure(failureCode);
        }

        /**
         * 进度改变消息的处理逻辑
         */
        protected void handleProgressChangedMessage(int progress) {
            mDownloadListener.onProgressChanged(progress, "");
        }

        /**
         * 暂停消息的处理逻辑
         */
        protected void handlePausedMessage() {
            mDownloadListener.onPaused(progress, mCompleteSize, "");
        }

        /**
         * 外部接口完成的回调
         */
        public void onFinish() {
            mDownloadListener.onFinished(mCompleteSize, "");
        }

        /**
         * 外部接口失败的回调
         */
        public void onFailure(FailureCode failureCode) {
            mDownloadListener.onFailure();
        }

        /**
         * 发送暂停消息
         */
        private void sendPausedMessage() {
            sendMessage(obtainMessage(PAUSED_MESSAGE, null));
        }

        /**
         * 发送失败消息
         */
        protected void sendFailureMessage(FailureCode failureCode) {
            sendMessage(obtainMessage(FAILURE_MESSAGE, new Object[]{failureCode}));
        }

        private void sendMessage(Message message) {
            if (mHandler != null) {
                mHandler.sendMessage(message);
            } else {
                handleSelfMessage(message);
            }
        }

        private Message obtainMessage(int responseMessage, Object response) {
            Message msg;
            if (mHandler != null) {
                msg = mHandler.obtainMessage(responseMessage, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMessage;
                msg.obj = response;
            }
            return msg;
        }

        /**
         * 文件下载方法，发送各种类型的事件
         */
        public void sendResponseMessage(InputStream inputStream) {
            //文件读写流
            RandomAccessFile randomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                randomAccessFile.seek(startPos);
                boolean isPaused = false;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, length);
                        mCompleteSize += length;
                        if ((startPos + mCompleteSize) < (mContentLength + startPos)) {
                            progress = (int) (Float.parseFloat(getToPointFloatStr(
                                    (float) (startPos + mCompleteSize) / (mContentLength + startPos))) * 100);
                            //限制notification更新频率
                            if (limit % 30 == 0 || progress == 100) {
                                //在子线程中读取流数据，后转发到主线程中去。
                                sendProgressChangedMessage(progress);
                            }
                        }
                        limit++;
                    } else {
                        isPaused = true;
                        sendPausedMessage();
                        break;
                    }
                }
                stopDownloading();
                if (!isPaused) {
                    sendFinishMessage();
                }
            } catch (IOException e) {
                sendPausedMessage();
                stopDownloading();
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    stopDownloading();
                    e.printStackTrace();
                }

            }
        }

        /**
         * 数字格式化
         */
        private String getToPointFloatStr(float value) {
            DecimalFormat format = new DecimalFormat("0.00");
            return format.format(value);
        }

        /**
         * 发送进度改变消息
         */
        private void sendProgressChangedMessage(int progress) {
            sendMessage(obtainMessage(PROGRESS_CHANGED, new Object[]{progress}));
        }

        /**
         * 发送完成消息
         */
        protected void sendFinishMessage() {
            sendMessage(obtainMessage(FINISH_MESSAGE, null));
        }
    }
}
