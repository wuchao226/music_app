package com.wuc.voice.view.home

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import com.wuc.lib_common_ui.base.BaseActivity
import com.wuc.lib_common_ui.pager_indictor.ScaleTransitionPagerTitleView
import com.wuc.voice.R
import com.wuc.voice.model.CHANNEL
import kotlinx.android.synthetic.main.activity_home.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

/**
 * @author:     wuchao
 * @date:       2019-09-17 18:09
 * @desciption: 首页
 */
class HomeActivity : BaseActivity(), View.OnClickListener {

    //指定首页要出现的卡片
    private val CHANNELS = arrayOf(CHANNEL.My, CHANNEL.DISCOVERY, CHANNEL.FRIEND)

    private var mAdapter: HomePagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initView()
    }

    private fun initView() {
        toggle_view.setOnClickListener(this)
        search_view.setOnClickListener(this)
        //初始化adapter
        mAdapter = HomePagerAdapter(supportFragmentManager, CHANNELS)
        view_pager.adapter = mAdapter
        //初始化指示器
        initMagicIndicator()
    }

    /**
     * 初始化指示器
     */
    private fun initMagicIndicator() {
        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
                val simplePagerTitleView = ScaleTransitionPagerTitleView(context)
                simplePagerTitleView.run {
                    text = CHANNELS[index].key
                    textSize = 19f
                    typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                    normalColor = Color.parseColor("#999999")
                    selectedColor = Color.parseColor("#333333")
                    setOnClickListener { view_pager.currentItem = index }
                }
                return simplePagerTitleView
            }

            override fun getCount(): Int {
                return CHANNELS.size
            }

            override fun getIndicator(context: Context?): IPagerIndicator? {
                return null
            }

            override fun getTitleWeight(context: Context?, index: Int): Float {
                return 1.0f
            }
        }
        magic_indicator.navigator = commonNavigator
        ViewPagerHelper.bind(magic_indicator, view_pager)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.toggle_view -> {
            }
            R.id.search_view -> {
            }
        }
    }
}
