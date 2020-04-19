package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 发送客服消息的回调接口
 */
public interface CustomCallBack {

    /**
     * 发送成功以后的回调方法
     * @param message
     */
    void success(JSONObject message, String appid);

    /**
     * 发送完成以后的调用
     */
    void complete(String appid);
}
