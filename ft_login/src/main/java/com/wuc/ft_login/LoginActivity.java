package com.wuc.ft_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.wuc.ft_login.inner.IUserLoginView;
import com.wuc.ft_login.presenter.UserLoginPresenter;
import com.wuc.lib_common_ui.base.BaseActivity;

/**
 * @author: wuchao
 * @date: 2019-09-26 16:18
 * @desciption: 登录
 */
public class LoginActivity extends BaseActivity implements IUserLoginView {

  private UserLoginPresenter mUserLoginPresenter;

  public static void start(Context context) {
    Intent intent = new Intent(context, LoginActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    //初始化P层
    mUserLoginPresenter = new UserLoginPresenter(this);
    findViewById(R.id.login_view).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mUserLoginPresenter.login(getUserName(), getPassword());
      }
    });
  }

  @Override public String getUserName() {
    return "18734924592";
  }

  @Override public String getPassword() {
    return "999999q";
  }

  @Override public void finishActivity() {
    finish();
  }

  @Override public void showLoginFailedView() {

  }

  @Override public void showLoadingView() {

  }

  @Override public void hideLoadingView() {

  }
}
