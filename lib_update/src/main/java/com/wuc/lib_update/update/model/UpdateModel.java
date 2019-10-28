package com.wuc.lib_update.update.model;

import java.io.Serializable;

/**
 * @author: wuchao
 * @date: 2019-10-25 15:52
 * @desciption:
 */
public class UpdateModel implements Serializable {

    private static final long serialVersionUID = -5161684897150460361L;

    public int ecode;
    public String emsg;
    public UpdateInfo data;
}
