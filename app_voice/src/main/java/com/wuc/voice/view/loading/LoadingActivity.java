package com.wuc.voice.view.loading;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.wuc.lib_base.ft_home.impl.HomeImpl;
import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_common_ui.base.constant.Constant;
import com.wuc.lib_pullalive.AliveJobService;
import com.wuc.voice.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author: wuchao
 * @date: 2019-10-18 11:00
 * @desciption:
 */
public class LoadingActivity extends BaseActivity {

  private Handler mHandler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(@NonNull Message msg) {
      super.handleMessage(msg);
      HomeImpl.getInstance().startHomActivity(LoadingActivity.this);
      finish();
    }
  };

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    avoidLauncherAgain();
    setContentView(R.layout.activity_loading);
    pullAliveService();
    if (hasPermission(Constant.WRITE_READ_EXTERNAL_PERMISSION)) {
      doSDCardPermission();
    } else {
      requestPermission(Constant.WRITE_READ_EXTERNAL_CODE, Constant.WRITE_READ_EXTERNAL_PERMISSION);
    }
  }

  /**
   * 避免从桌面启动程序后，会重新实例化入口类的activity
   */
  private void avoidLauncherAgain() {
    // 判断当前activity是不是所在任务栈的根
    if (!this.isTaskRoot()) {
      Intent intent = getIntent();
      if (intent != null) {
        String action = intent.getAction();
        if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
          finish();
        }
      }
    }
  }

  private void pullAliveService() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      AliveJobService.start(this);
    }
  }

  @Override
  public void doSDCardPermission() {
    super.doSDCardPermission();
    mHandler.sendEmptyMessageDelayed(0, 3000);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
  }
}
