package com.wuc.voice.view.login.inner;

/**
 * @author : wuchao5
 * @date : 2020/10/10 16:01
 * @desciption : UI层对外提供的方法
 */
public interface IUserLoginView {

  String getUserName();

  String getPassword();

  void finishActivity();

  void showLoginFailedView();

  void showLoadingView();

  void hideLoadingView();
}
