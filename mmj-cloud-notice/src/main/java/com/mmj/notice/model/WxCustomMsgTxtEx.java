package com.mmj.notice.model;

public class WxCustomMsgTxtEx extends WxCustomMsgTxt {

    public static String dictType = "WX_CUSTOM_MSG"; //微信客服消息得字典匹配规则

    public static enum matchRule{
        half, //半匹配
        full  //全匹配
    }
}
