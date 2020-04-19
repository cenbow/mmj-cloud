package com.mmj.notice.model;

import java.util.List;

/**
 * 微信菜单实体扩展类
 */
public class WxMenuEx extends WxMenu {

    private List<WxMenuKey> wxMenuKeys; //菜单栏点击配置

    public List<WxMenuKey> getWxMenuKeys() {
        return wxMenuKeys;
    }

    public void setWxMenuKeys(List<WxMenuKey> wxMenuKeys) {
        this.wxMenuKeys = wxMenuKeys;
    }
}
