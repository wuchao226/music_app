package com.wuc.ft_login.presenter;

import com.google.gson.Gson;
import com.wuc.ft_login.api.MockData;
import com.wuc.ft_login.api.RequestCenter;
import com.wuc.ft_login.inner.IUserLoginPresenter;
import com.wuc.ft_login.inner.IUserLoginView;
import com.wuc.ft_login.manager.UserManager;
import com.wuc.lib_base.ft_login.model.LoginEvent;
import com.wuc.lib_base.ft_login.model.user.User;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;
import org.greenrobot.eventbus.EventBus;

/**
 * @author : wuchao5
 * @date : 2020/10/10 16:00
 * @desciption : 登陆页面对应Presenter
 */
public class UserLoginPresenter implements IUserLoginPresenter, DisposeDataListener {

  private IUserLoginView mIView;

  public UserLoginPresenter(IUserLoginView iView) {
    mIView = iView;
  }

  @Override
  public void login(String username, String password) {
    mIView.showLoadingView();
    RequestCenter.login(this);
  }

  @Override
  public void onSuccess(Object responseObj) {
    mIView.hideLoadingView();
    User user = (User) responseObj;
    UserManager.getInstance().setUser(user);
    //发送登陆Event
    EventBus.getDefault().post(new LoginEvent());
    mIView.finishActivity();
  }

  @Override
  public void onFailure(Object reasonObj) {
    mIView.hideLoadingView();
    onSuccess(new Gson().fromJson(MockData.LOGIN_DATA, User.class));
    mIView.showLoginFailedView();
  }
}
