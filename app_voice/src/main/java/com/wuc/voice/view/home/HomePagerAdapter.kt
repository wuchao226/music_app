package com.wuc.voice.view.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wuc.voice.model.*
import com.wuc.voice.view.discovery.DiscoveryFragment
import com.wuc.voice.view.friend.FriendFragment
import com.wuc.voice.view.mine.MineFragment
import com.wuc.voice.view.video.VideoFragment

/**
 * @author:     wuchao
 * @date:       2019-09-17 22:49
 * @desciption: 首页框架adapter
 */
class HomePagerAdapter : FragmentPagerAdapter {

    private var mList: Array<CHANNEL>? = null

    constructor(fm: FragmentManager, list: Array<CHANNEL>) : super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        mList = list
    }

    override fun getItem(position: Int): Fragment {
        val type = mList!![position].value
        when (type) {
            MINE_ID -> return MineFragment()
            DISCOVERY_ID -> return DiscoveryFragment()
            FRIEND_ID -> return FriendFragment()
            VIDEO_ID -> return VideoFragment()
            else -> return MineFragment()
        }
    }

    override fun getCount(): Int {
        return mList!!.size
    }
}