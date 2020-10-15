package com.wuc.lib_base.ft_home;

import android.content.Context;
import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * @author : wuchao5
 * @date : 2020/10/15 14:52
 * @desciption : 首页模块对外暴露接口
 */
public interface HomeService extends IProvider {
  void startHomeActivity(Context context);
}
