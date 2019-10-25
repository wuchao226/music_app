package com.wuc.voice.model.discory;


import com.wuc.voice.model.BaseModel;

import java.util.ArrayList;

/**
 * @author: vision
 * @function:
 * @date: 19/6/2
 */
public class RecommandHeadValue extends BaseModel {

    public ArrayList<String> ads;
    public ArrayList<RecommandMiddleValue> middle;
    public ArrayList<RecommandFooterValue> footer;

}
