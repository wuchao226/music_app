package com.wuc.voice.model.user;


import com.wuc.voice.model.BaseModel;

/**
 * @author: wuchao
 * @date: 2019-09-19 18:16
 * @desciption: 用户真正实体数据
 */
public class UserContent extends BaseModel {
    /**
     * 用户唯一标识符
     */
    public String userId;
    public String photoUrl;
    public String name;
    public String tick;
    public String mobile;
    public String platform;
}
