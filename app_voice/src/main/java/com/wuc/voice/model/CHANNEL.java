package com.wuc.voice.model;

/**
 * @author: wuchao
 * @date: 2019-09-17 11:19
 * @desciption: 首页卡片封装（指定首页要出现的卡片）
 */
public enum CHANNEL {
    //我的
    MY("我的", 0x01),
    //发现
    DISCORY("发现", 0x02),
    //朋友
    FRIEND("朋友", 0x03),
    VIDEO("视频", 0x04);

    //所有类型标识
    public static final int MINE_ID = 0x01;
    public static final int DISCORY_ID = 0x02;
    public static final int FRIEND_ID = 0x03;
    public static final int VIDEO_ID = 0x04;

    private final String key;
    private final int value;

    CHANNEL(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key == null ? "" : key;
    }

    public int getValue() {
        return value;
    }
}
