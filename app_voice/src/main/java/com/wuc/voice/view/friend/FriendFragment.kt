package com.wuc.voice.view.friend


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wuc.voice.R

/**
 * @author:     wuchao
 * @date:       2019-09-17 22:59
 * @desciption: 首页朋友Fragment
 */
class FriendFragment : Fragment() {

    fun newInstance(): Fragment {
        return FriendFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_friend_layout, container, false)
    }


}
