package com.mmj.notice.model;

import java.util.List;

public class WxCustomMsgEx extends WxCustomMsg {

    private List<WxCustomMsgTxt> wxCustomMsgTxts;

    private String openid;

    public static enum ACCEPTTYPE{
        subscribe,  //关注回复
        delay,       //关注延迟回复
        push,        //主动推送
        keyword,    //关键词回复
        defaultreply      //默认回复
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public List<WxCustomMsgTxt> getWxCustomMsgTxts() {
        return wxCustomMsgTxts;
    }

    public void setWxCustomMsgTxts(List<WxCustomMsgTxt> wxCustomMsgTxts) {
        this.wxCustomMsgTxts = wxCustomMsgTxts;
    }
}
