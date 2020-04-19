package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 微信客服消息处理
 */
public interface WxMessageService {

    /**
     * 校验客服消息配置的地址是否正确
     * @param signParams
     * @return
     */
    boolean checkSignature(Map<String, String[]> signParams);


    /**
     * 将微信接受到的流转化位map消息(公众号)
     * @param inputStream
     * @return
     */
    Map<String, String> transform(InputStream inputStream);

    /**
     * 将微信接受到的流转化位map消息(小程序)
     * @param inputStream
     * @return
     */
    Map<String, String> transformM(InputStream inputStream);

    /**
     * 发送公众号模板消息
     * @param msgJson
     * @return
     */
    JSONObject sendTemplate(JSONObject msgJson);

    /**
     * 发送小程序模板消息
     * @param msgJson
     * @return
     */
    JSONObject sendTemplateM(JSONObject msgJson);

    void sendTemplateM(Map<String, Object> map);

    /**
     * 发送公众号客服消息
     * @param msgJson
     * @return
     */
    JSONObject sendCustom(JSONObject msgJson);

    /**
     * 发送公众号客服消息 带上回调方法
     * @param msgJson
     * @param customCallBack
     * @return
     */
    JSONObject sendCustom(JSONObject msgJson, CustomCallBack customCallBack);


    /**
     * 发送公众号客服消息 发送给appid下的所有人
     * @param msgJson
     * @param appid
     * @return
     */
    JSONObject sendCustom(JSONObject msgJson, String appid);

    /**
     * 发送公众号客服消息 发送给appid下的所有人,带上回调方法
     * @param msgJson
     * @param appid
     * @param customCallBack
     * @return
     */
    JSONObject sendCustomCallBack(JSONObject msgJson, String appid, CustomCallBack customCallBack);

    /**
     * 查询公众号appid下面的这个用户信息
     * @param appid
     * @param openid
     * @return
     */
    JSONObject queryUserInfo(String appid, String openid);


    /**
     * 发送公众号客服消息 发送给appid下的指定标签用户,带上回调方法
     * @param msgJson
     * @param appid
     * @param customCallBack
     * @return
     */
    JSONObject sendCustomCallBack(JSONObject msgJson, String appid, List<String> tagIds, CustomCallBack customCallBack);

    /**
     * 查询一个公众号下面的总人数
     * @param appid
     * @return
     */
    int queryTotal(String appid);

    /**
     * 快速发送短信
     * @param msgJson
     * @return
     */
    Object sendSms(JSONObject msgJson);

    /**
     * 慢速发短信
     * @param msgJson
     * @return
     */
    Object sendSmsl(JSONObject msgJson);



}
