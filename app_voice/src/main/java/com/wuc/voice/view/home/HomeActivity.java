package com.wuc.voice.view.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import com.wuc.lib_common_ui.base.BaseActivity;
import com.wuc.lib_common_ui.pager_indicator.ScaleTransitionPagerTitleView;
import com.wuc.lib_image_loader.ImageLoaderManager;
import com.wuc.voice.R;
import com.wuc.voice.model.CHANNEL;
import com.wuc.voice.model.login.LoginEvent;
import com.wuc.voice.utils.UserManager;
import com.wuc.voice.view.home.adapter.HomePagerAdapter;
import com.wuc.voice.view.login.LoginActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;


/**
 * @author: wuchao
 * @date: 2019-09-17 10:39
 * @desciption: 首页Activity
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 指定首页要出现的卡片
     */
    private static final CHANNEL[] CHANNELS = new CHANNEL[]{CHANNEL.MY, CHANNEL.DISCOVERY, CHANNEL.FRIEND};
    private AppCompatImageView mToggleView;
    private AppCompatImageView mSearchView;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private LinearLayoutCompat mUnLogginLayout;
    private AppCompatImageView mAvatarView;

    private HomePagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_home);
        initView();
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggleView = findViewById(R.id.toggle_view);
        mToggleView.setOnClickListener(this);
        mSearchView = findViewById(R.id.search_view);
        mSearchView.setOnClickListener(this);

        //初始化adapter
        mViewPager = findViewById(R.id.view_pager);
        mAdapter = new HomePagerAdapter(getSupportFragmentManager(), CHANNELS);
        mViewPager.setAdapter(mAdapter);
        //初始化指示器
        initMagicIndicator();

        mUnLogginLayout = findViewById(R.id.un_login_layout);
        mUnLogginLayout.setOnClickListener(this);
        mAvatarView = findViewById(R.id.avatar_view);

    }

    /**
     * 初始化指示器
     */
    private void initMagicIndicator() {
        MagicIndicator mMagicIndicator = findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return CHANNELS == null ? 0 : CHANNELS.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(CHANNELS[index].getKey());
                simplePagerTitleView.setTextSize(19);
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#333333"));
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mViewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }

            @Override
            public float getTitleWeight(Context context, int index) {
                return 1.0f;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
    }

    /**
     * 处理登陆事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginEvent(LoginEvent event) {
        mUnLogginLayout.setVisibility(View.GONE);
        mAvatarView.setVisibility(View.VISIBLE);
        ImageLoaderManager.getInstance().displayImageForCircle(mAvatarView,
                UserManager.getInstance().getUser().data.photoUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_view:
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.search_view:
                break;
            case R.id.un_login_layout:
                if (!UserManager.getInstance().hasLogined()) {
                    LoginActivity.start(this);
                } else {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
                break;
            default:
                break;
        }
    }
}
