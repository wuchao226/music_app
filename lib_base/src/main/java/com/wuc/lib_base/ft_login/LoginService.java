package com.wuc.lib_base.ft_login;

import android.content.Context;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.wuc.lib_base.ft_login.model.user.User;

/**
 * @author : wuchao5
 * @date : 2020/10/12 17:47
 * @desciption : Login模块对外提供的所有功能
 */
public interface LoginService extends IProvider {

  User getUserInfo();

  void removeUser();

  boolean hasLogin();

  void login(Context context);
}
