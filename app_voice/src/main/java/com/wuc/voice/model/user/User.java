package com.wuc.voice.model.user;


import java.io.Serializable;

/**
 * 用户数据协议
 */
public class User implements Serializable {
  public int ecode;
  public String emsg;
  public UserContent data;
}
