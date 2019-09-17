package com.wuc.voice.view.video


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wuc.voice.R


/**
 * @author:     wuchao
 * @date:       2019-09-17 23:04
 * @desciption: 首页 视频 Fragment
 */
class VideoFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_video_layout, container, false)
    }


    companion object {
        fun newInstance(): Fragment {
            return VideoFragment()
        }
    }
}
