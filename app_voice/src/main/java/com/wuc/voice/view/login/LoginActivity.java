package com.wuc.voice.view.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wuc.lib_common_ui.SpreadView;
import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_network.okhttp.listener.DisposeDataListener;
import com.wuc.voice.R;
import com.wuc.voice.api.RequestCenter;
import com.wuc.voice.model.login.LoginEvent;
import com.wuc.voice.model.user.User;
import com.wuc.voice.utils.UserManager;

import org.greenrobot.eventbus.EventBus;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author: wuchao
 * @date: 2019-09-26 16:18
 * @desciption: 登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, DisposeDataListener {

    private SpreadView mSpreadView;
    /**
     * 手机号登陆
     */
    private AppCompatTextView mLoginView;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mSpreadView = (SpreadView) findViewById(R.id.spreadView);
        mLoginView = (AppCompatTextView) findViewById(R.id.login_view);
        mLoginView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.login_view:
                RequestCenter.login(LoginActivity.this);
                break;
        }
    }

    @Override
    public void onSuccess(Object responseObj) {
        //处理正常逻辑
        User user = (User) responseObj;
        UserManager.getInstance().saveUser(user);
        EventBus.getDefault().post(new LoginEvent());
        finish();
    }

    @Override
    public void onFailure(Object reasonObj) {
        //登录失败逻辑
    }
}
