package com.wuc.voice.view.discovery


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wuc.voice.R


/**
 * @author:     wuchao
 * @date:       2019-09-17 22:57
 * @desciption: 首页发现Fragment
 */
class DiscoveryFragment : Fragment() {

    fun newInstance(): Fragment {
        return DiscoveryFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_discovery_layout, container, false)
    }


}
