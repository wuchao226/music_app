package com.wuc.ft_home.view.home.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.wuc.ft_home.model.CHANNEL;
import com.wuc.ft_home.view.discovery.DiscoveryFragment;
import com.wuc.ft_home.view.friend.FriendFragment;
import com.wuc.ft_home.view.mine.MineFragment;

/**
 * @author: wuchao
 * @date: 2019-09-17 14:45
 * @desciption: 首页 ViewPager 的 adapter
 */
public class HomePagerAdapter extends FragmentPagerAdapter {
  private CHANNEL[] mList;

  public HomePagerAdapter(@NonNull FragmentManager fm, CHANNEL[] datas) {
    super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    this.mList = datas;
  }

  /**
   * 这种方式，避免一次性创建所有的fragment
   */
  @NonNull
  @Override
  public Fragment getItem(int position) {
    int type = mList[position].getValue();
    switch (type) {
      case CHANNEL.MINE_ID:
        return MineFragment.newInstance();
      case CHANNEL.DISCOVERY_ID:
        return DiscoveryFragment.newInstance();
      case CHANNEL.FRIEND_ID:
        return FriendFragment.newInstance();
      default:
    }
    return MineFragment.newInstance();
  }

  @Override
  public int getCount() {
    return mList == null ? 0 : mList.length;
  }
}
