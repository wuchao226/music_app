package com.wuc.lib_common_ui.base;

import android.os.Bundle;

import com.wuc.lib_common_ui.utils.StatusBarUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
}
