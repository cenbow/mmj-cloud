package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;

import java.io.PrintWriter;
import java.util.Map;

/**
 * 微信公众号接受到消息
 */
public interface MpMessageCommonService {

    /**
     * 菜单栏点击事件
     * @param map               事件信息
     * @param userInfo         用户信息
     */
    void click(Map<String, String> map, JSONObject userInfo);

    /**
     * 取消关注事件
     * @param map              事件信息
     * @param userInfo        用户信息
     */
    void unsubscribe(Map<String, String> map, JSONObject userInfo);

    /**
     * 关注事件
     * @param map              事件信息
     * @param userInfo        用户信息
     */
    void subscribe(Map<String, String> map, JSONObject userInfo);


    /**
     * 用户发送文本消息事件
     * @param map              事件信息
     * @param userInfo        用户信息
     */
    void text(Map<String, String> map, JSONObject userInfo);

    /**
     * 扫描二维码事件
     * @param map              事件信息
     * @param userInfo        用户信息
     */
    void scan(Map<String, String> map, JSONObject userInfo);
}
