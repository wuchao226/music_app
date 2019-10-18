package com.wuc.lib_common_ui.base;

import android.content.pm.PackageManager;
import android.os.Bundle;

import com.wuc.lib_common_ui.base.constant.Constant;
import com.wuc.lib_common_ui.utils.StatusBarUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @author: wuchao
 * @date: 2019-09-17 16:32
 * @desciption:
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.statusBarLightMode(this);
    }

    /**
     * 申请指定权限
     */
    public void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    /**
     * 判断是否有指定的权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant
                    .WRITE_READ_EXTERNAL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doSDCardPermission();
                }
                break;
            case Constant
                    .HARDWEAR_CAMERA_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doCameraPermission();
                }
                break;
            default:
        }
    }

    /**
     * 处理整个业务中SDCard业务
     */
    public void doSDCardPermission() {

    }

    /**
     * 处理整个业务中相机业务
     */
    public void doCameraPermission() {

    }
}
