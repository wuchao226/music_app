package com.wuc.voice.view.discovery;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuc.voice.R;

import androidx.fragment.app.Fragment;

/**
 * @author: wuchao
 * @date: 2019-09-17 15:14
 * @desciption: 首页发现Fragment
 */
public class DiscoveryFragment extends Fragment {


    public static DiscoveryFragment newInstance() {
        DiscoveryFragment fragment = new DiscoveryFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discovery_layout, container, false);
    }

}
