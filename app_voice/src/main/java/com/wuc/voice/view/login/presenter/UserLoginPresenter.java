package com.wuc.voice.view.login.presenter;

import com.google.gson.Gson;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;
import com.wuc.voice.api.MockData;
import com.wuc.voice.api.RequestCenter;
import com.wuc.voice.model.login.LoginEvent;
import com.wuc.voice.model.user.User;
import com.wuc.voice.utils.UserManager;
import com.wuc.voice.view.login.IUserLoginPresenter;
import com.wuc.voice.view.login.IUserLoginView;
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
