package com.wuc.voice.view.mine


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wuc.voice.R

/**
 * @author:     wuchao
 * @date:       2019-09-17 23:02
 * @desciption: 首页 我的Fragment
 */
class MineFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment {
            return MineFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mine_layout, container, false)
    }


}
