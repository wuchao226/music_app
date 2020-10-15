package com.wuc.ft_home.model.friend;

import com.wuc.lib_base.BaseModel;
import com.wuc.lib_base.ft_audio.model.CommonAudioBean;
import java.util.ArrayList;

/**
 * @文件描述：朋友实体
 */
public class FriendBodyValue extends BaseModel {

  public int type;
  public String avatr;
  public String name;
  public String fans;
  public String text;
  public ArrayList<String> pics;
  public String videoUrl;
  public String zan;
  public String msg;
  public CommonAudioBean audioBean;
}
