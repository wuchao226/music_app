package com.wuc.lib_base.ft_home.impl;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.wuc.lib_base.ft_home.HomeService;
import com.wuc.lib_base.router.RouterPath;

/**
 * @author : wuchao5
 * @date : 2020/10/15 14:53
 * @desciption : 对HomeService的包装，使接口使用更方便
 */
public class HomeImpl {

  private HomeImpl() {
    ARouter.getInstance().inject(this);
  }

  @Autowired(name = RouterPath.Home.PATH_HOME)
  protected HomeService mHomeService;

  private volatile static HomeImpl mHomeImpl = null;

  public static HomeImpl getInstance() {
    if (mHomeImpl == null) {
      synchronized (HomeImpl.class) {
        if (mHomeImpl == null) {
          mHomeImpl = new HomeImpl();
        }
      }
    }
    return mHomeImpl;
  }

  public void startHomActivity(Context context) {
    mHomeService.startHomeActivity(context);
  }
}
