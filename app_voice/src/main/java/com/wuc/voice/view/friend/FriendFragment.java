package com.wuc.voice.view.friend;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuc.voice.R;

import androidx.fragment.app.Fragment;

/**
 * @author: wuchao
 * @date: 2019-09-17 15:17
 * @desciption: 首页朋友Fragment
 */
public class FriendFragment extends Fragment {


    public static FriendFragment newInstance() {
        FriendFragment fragment = new FriendFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_layout, container, false);
    }

}
