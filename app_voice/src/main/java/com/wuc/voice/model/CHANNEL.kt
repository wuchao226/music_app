package com.wuc.voice.model

/**
 * @author:     wuchao
 * @date:       2019-09-17 22:28
 * @desciption: 首页卡片封装（指定首页要出现的卡片）
 */

//所有类型标识
const val MINE_ID = 0x01
const val DISCOVERY_ID = 0x02
const val FRIEND_ID = 0x03
const val VIDEO_ID = 0x04

enum class CHANNEL(key: String, value: Int) {

    My("我的", MINE_ID),

    DISCOVERY("发现", DISCOVERY_ID),

    FRIEND("朋友", FRIEND_ID),

    VIDEO("视频", VIDEO_ID);

    val key: String = key
    val value: Int = value
}
