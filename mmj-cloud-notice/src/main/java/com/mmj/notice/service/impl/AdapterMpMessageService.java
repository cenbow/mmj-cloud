package com.mmj.notice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.mmj.notice.service.MpMessageCommonService;

import java.io.PrintWriter;
import java.util.Map;

public class AdapterMpMessageService implements MpMessageCommonService {
    /**
     * 菜单栏点击事件
     *
     * @param map         事件信息
     * @param userInfo    用户信息
     */
    @Override
    public void click(Map<String, String> map, JSONObject userInfo) {

    }

    /**
     * 取消关注事件
     *
     * @param map         事件信息
     * @param userInfo    用户信息
     */
    @Override
    public void unsubscribe(Map<String, String> map, JSONObject userInfo) {

    }

    /**
     * 关注事件
     *
     * @param map         事件信息
     * @param userInfo    用户信息
     */
    @Override
    public void subscribe(Map<String, String> map, JSONObject userInfo) {

    }

    /**
     * 用户发送文本消息事件
     *
     * @param map         事件信息
     * @param userInfo    用户信息
     */
    @Override
    public void text(Map<String, String> map, JSONObject userInfo) {

    }

    /**
     * 扫描二维码事件
     *
     * @param map         事件信息
     * @param userInfo    用户信息
     */
    @Override
    public void scan(Map<String, String> map, JSONObject userInfo) {

    }
}
