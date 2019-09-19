package com.wuc.lib_network.okhttp.response;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wuc.lib_network.okhttp.exception.OkHttpException;
import com.wuc.lib_network.okhttp.listener.DisposeDataHandle;
import com.wuc.lib_network.okhttp.listener.DisposeDownloadListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import okhttp3.Call;
import okhttp3.Response;

/**
 * @author: wuchao
 * @date: 2019-09-18 17:42
 * @desciption: 专门处理文件下载回调
 */
public class CommonFileCallback extends BaseCommonCallback {

    /**
     * 将其它线程的数据转发到UI线程
     */
    private static final int PROGRESS_MESSAGE = 0x01;

    /**
     * 下载监听
     */
    private DisposeDownloadListener mListenerDownload;
    private String mFilePath;
    private int mProgress;

    public CommonFileCallback(DisposeDataHandle handle) {
        super(handle);
        this.mListenerDownload = (DisposeDownloadListener) mListener;
        this.mFilePath = handle.mSource;
        this.mDeliveryHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case PROGRESS_MESSAGE:
                        mListenerDownload.onProgress((int) msg.obj);
                        break;
                    default:
                }
            }
        };
    }


    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        final File file = handleResponse(response);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                if (file != null) {
                    mListenerDownload.onSuccess(file);
                } else {
                    mListenerDownload.onFailure(new OkHttpException(IO_ERROR, EMPTY_MSG));
                }
            }
        });
    }

    /**
     * 此时还在子线程中，不则调用回调接口
     *
     * @param response response
     * @return File
     */
    private File handleResponse(Response response) {
        if (response == null) {
            return null;
        }
        InputStream inputStream = null;
        File file = null;
        FileOutputStream fos = null;
        byte[] buffer = new byte[2048];
        int length;
        int currentLength = 0;
        double sumLength;
        try {
            checkLocalFilePath(mFilePath);
            file = new File(mFilePath);
            fos = new FileOutputStream(file);
            inputStream = response.body().byteStream();
            sumLength = (double) response.body().contentLength();

            while ((length = inputStream.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
                currentLength += length;
                mProgress = (int) (currentLength / sumLength * 100);
                mDeliveryHandler.obtainMessage(PROGRESS_MESSAGE, mProgress).sendToTarget();
            }
            fos.flush();
        } catch (Exception e) {
            file = null;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (inputStream != null) {

                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private void checkLocalFilePath(String localFilePath) {
        File path = new File(localFilePath.substring(0,
                localFilePath.lastIndexOf("/") + 1));
        File file = new File(localFilePath);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
