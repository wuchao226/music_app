package com.wuc.voice.model.user;


import java.io.Serializable;

/**
 * @author: wuchao
 * @date: 2019-09-19 18:16
 * @desciption: 用户真正实体数据
 */
public class UserContent implements Serializable {
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
