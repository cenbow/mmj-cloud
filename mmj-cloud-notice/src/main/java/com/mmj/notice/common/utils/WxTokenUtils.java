package com.mmj.notice.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.utils.HttpTools;
import com.mmj.notice.model.WxConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 微信token处理的工具类
 */
@Component
public class WxTokenUtils {

    @Autowired
    HttpTools httpTools;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    /**
     * 根据appid和secret获取最新的token
     * @param appid
     * @param secret
     * @return
     */
    public String reloadToken(String appid, String secret){
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credential");
        map.put("appid", appid);
        map.put("secret", secret);
        JSONObject result = httpTools.doGet(WxConstants.URL_GET_ACCESSTOKEN, map);
        String accessToken = result.getString("access_token");
        redisTemplate.opsForValue().set("access_token_" + appid, accessToken, 7000, TimeUnit.SECONDS);
        return accessToken;
    }
}
