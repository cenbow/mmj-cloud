package com.mmj.notice.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 微信标签处理
 */
public interface WxTagService {

    /**
     * 给用户打标签
     * @param params
     * @return
     */
    JSONObject doTag(JSONObject params);

    /**
     * 根据公众号id和标签查询对应的标签名称
     * @param appid  公众号id
     * @param tagList  标签集合
     * @return
     */
    List<String> queryTagName(String appid, List<String> tagList);

    /**
     * 根据appid查询公众号标签信息
     * @param appid
     * @return
     */
    JSONObject query(String appid);
}
