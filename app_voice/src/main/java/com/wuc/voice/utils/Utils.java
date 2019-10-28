package com.wuc.voice.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.wuc.lib_update.update.UpdateManager;
import com.wuc.voice.view.InstallPermissionActivity;

import java.io.File;

import androidx.core.content.FileProvider;

public class Utils {

    public static Intent getInstallApkIntent(final Context context, final String filePath) {
        // 通过Intent安装APK文件
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //兼容7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //增加读写权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //兼容8.0
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                if (!hasInstallPermission) {
                    InstallPermissionActivity.sListener = new UpdateManager.InstallPermissionListener() {
                        @Override
                        public void permissionSuccess() {
                            installApk(context, filePath);
                        }

                        @Override
                        public void permissionFail() {
                            Toast.makeText(context, "授权失败，无法安装应用", Toast.LENGTH_LONG).show();
                        }
                    };
                    Intent intent8 = new Intent(context, InstallPermissionActivity.class);
                    intent8.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    return intent8;
                }
            }
        }
        intent.setDataAndType(getPathUri(context, filePath), "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 8.0 权限获取后的安装
     */
    private static void installApk(Context context, String filePath) {
        File apkFile = new File(filePath);
        // 通过Intent安装APK文件
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        installIntent.setDataAndType(getPathUri(context, filePath), "application/vnd.android.package-archive");
        context.startActivity(installIntent);
    }

    /**
     * 获取文件对应uri
     */
    public static Uri getPathUri(Context context, String filePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider",
                    new File(filePath));
        } else {
            uri = Uri.fromFile(new File(filePath));
        }
        return uri;
    }
}
