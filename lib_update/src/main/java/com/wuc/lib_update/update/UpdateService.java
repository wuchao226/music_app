package com.wuc.lib_update.update;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.service.update.UpdateNotificationService;
import com.wuc.lib_update.R;
import com.wuc.lib_update.app.UpdateHelper;
import com.wuc.lib_update.update.utils.NotificationUtils;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static com.wuc.lib_update.app.UpdateHelper.UPDATE_FILE_KEY;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:45
 * @desciption: 应用更新组件入口，用来启动下载器并更新Notification
 */
public class UpdateService extends Service {

    public static final String CHANNEL_ID = "channel_id_update";
    public static final String CHANNEL_NAME = "channel_name_update";
    public static final String APK_URL = "apk_url";
    /**
     * apk下载地址
     */
    private static final String APK_URL_TITLE = "http://www.imooc.com/mobile/mukewang.apk";
    @Autowired(name = RouterPath.Voice.PATH_VOICE_SERVICE)
    protected UpdateNotificationService mNotificationService;
    /**
     * 文件存放路径
     */
    private String filePath;
    /**
     * 文件下载地址
     */
    private String apkUrl;
    private NotificationUtils mNotificationUtils;
    /**
     * 是否发送了广播
     */
    private boolean isFirstSendBroad = false;

    public static void startService(Context context) {
        Intent intent = new Intent(context, UpdateService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ARouter.getInstance().inject(this);
        filePath = Environment.getExternalStorageDirectory() + "/imooc/imooc.apk";

        mNotificationUtils = new NotificationUtils(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apkUrl = APK_URL_TITLE;
        notifyUser(getString(R.string.update_download_start), getString(R.string.update_download_start),
                0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    public void notifyUser(String tickerMsg, String message, int progress) {
        mNotificationUtils.sendNotificationProgress(tickerMsg, message, progress, getContentIntent(progress));
        if (progress >= 100) {
            if (!isFirstSendBroad) {
                sendInstallBroadcast();
            }
            isFirstSendBroad = true;
        }
    }

    /**
     * 开始下载
     */
    private void startDownload() {
        UpdateManager.getInstance().startDownload(apkUrl, filePath, new UpdateDownloadListener() {

            @Override
            public void onStarted() {

            }

            @Override
            public void onPrepared(long contentLength, String downloadUrl) {
            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                notifyUser(getString(R.string.update_download_processing),
                        getString(R.string.update_download_processing), progress);
            }

            @Override
            public void onPaused(int progress, int completeSize, String downloadUrl) {
                notifyUser(getString(R.string.update_download_failed),
                        getString(R.string.update_download_failed_msg), 0);
                deleteApkFile();
                stopSelf();// 停掉服务自身
            }

            @Override
            public void onFinished(int completeSize, String downloadUrl) {
                notifyUser(getString(R.string.update_download_finish),
                        getString(R.string.update_download_finish), 100);
                stopSelf();// 停掉服务自身
            }

            @Override
            public void onFailure() {
                notifyUser(getString(R.string.update_download_failed),
                        getString(R.string.update_download_failed_msg), 0);
                deleteApkFile();
                stopSelf();// 停掉服务自身
            }
        });
    }

    private PendingIntent getContentIntent(int progress) {
        PendingIntent pendingIntent = null;
        if (progress > 0 && progress < 100) {
            // intent为null,表示点击通知时不跳转
            pendingIntent = PendingIntent.getActivity(this, 1,
                    new Intent(), PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intent = new Intent(UpdateHelper.UPDATE_ACTION);
            intent.putExtra(UPDATE_FILE_KEY, filePath);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ComponentName componentName = new ComponentName(this.getPackageName(),
                        mNotificationService.getUpdatePackageName());
                intent.setComponent(componentName);
                pendingIntent = PendingIntent.getBroadcast(this, 1,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
        return pendingIntent;
    }

    /**
     * 发送安装APP的广播
     */
    private void sendInstallBroadcast() {
        Intent intent = new Intent(UpdateHelper.UPDATE_ACTION);
        intent.putExtra(UPDATE_FILE_KEY, filePath);
        //发送本地广播通知更新APP安装
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 删除无用apk文件
     */
    private boolean deleteApkFile() {
        File apkFile = new File(filePath);
        if (apkFile.exists() && apkFile.isFile()) {
            return apkFile.delete();
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
