package com.wuc.ft_login.service;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.ft_login.LoginActivity;
import com.wuc.ft_login.manager.UserManager;
import com.wuc.lib_base.router.RouterPath;
import com.wuc.lib_base.service.ft_login.LoginService;
import com.wuc.lib_base.service.ft_login.model.user.User;

/**
 * @author : wuchao5
 * @date : 2020/10/12 19:27
 * @desciption : 登陆模块对外接口功能实现
 */
@Route(path = RouterPath.Login.PATH_LOGIN)
public class LoginServiceImpl implements LoginService {
  @Override
  public User getUserInfo() {
    return UserManager.getInstance().getUser();
  }

  @Override
  public void removeUser() {
    UserManager.getInstance().removeUser();
  }

  @Override
  public boolean hasLogin() {
    return UserManager.getInstance().hasLogined();
  }

  @Override
  public void login(Context context) {
    LoginActivity.start(context);
  }

  @Override public void init(Context context) {

  }
}
