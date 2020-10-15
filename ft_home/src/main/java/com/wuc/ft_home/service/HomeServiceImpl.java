package com.wuc.ft_home.service;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.wuc.ft_home.view.home.HomeActivity;
import com.wuc.lib_base.ft_home.HomeService;
import com.wuc.lib_base.router.RouterPath;

/**
 * @author : wuchao5
 * @date : 2020/10/15 14:54
 * @desciption : 首页模块对外接口实现类
 */
@Route(path = RouterPath.Home.PATH_HOME)
public class HomeServiceImpl implements HomeService {
  @Override public void startHomeActivity(Context context) {
    HomeActivity.start(context);
  }

  @Override public void init(Context context) {

  }
}
