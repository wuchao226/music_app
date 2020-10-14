package com.wuc.lib_base.service.ft_login.service;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.launcher.ARouter;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.service.audio.AudioService;
import com.wuc.lib_base.service.ft_login.LoginService;
import com.wuc.lib_base.service.ft_login.model.user.User;

/**
 * @author : wuchao5
 * @date : 2020/10/12 19:30
 * @desciption : 对LoginService包装，业务方直接调用，无需再自己初始化service类
 */
public class LoginImpl {

  @Autowired(name = RouterPath.Login.PATH_LOGIN)
  protected LoginService mLoginService;

  private LoginImpl() {
    //初始化LoginService
    ARouter.getInstance().inject(this);
  }

  private static volatile LoginImpl mLoginImpl;

  public static LoginImpl getInstance() {
    if (mLoginImpl == null) {
      synchronized (LoginImpl.class) {
        if (mLoginImpl == null) {
          mLoginImpl = new LoginImpl();
          return mLoginImpl;
        }
      }
    }
    return mLoginImpl;
  }

  public void login(Context context) {
    mLoginService.login(context);
  }

  public boolean hasLogin() {
    return mLoginService.hasLogin();
  }

  public void removeUser() {
    mLoginService.removeUser();
  }

  public User getUserInfo() {
    return mLoginService.getUserInfo();
  }
}
