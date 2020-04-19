package com.mmj.third.jushuitan.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

/**
 * @description: 聚水潭请求
 * @auther: KK
 * @date: 2019/6/5
 */
@Slf4j
@Component
public class JushuitanHttpClient {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client = new OkHttpClient();

    private JushuitanHttpClient() {
    }

    /**
     * 请求地址
     */
    @Value("${jushuitan.url}")
    private String url;
    /**
     * 合作方编号
     */
    @Value("${jushuitan.partnerId}")
    private String partnerId;
    /**
     * 接入密钥
     */
    @Value("${jushuitan.partnerKey}")
    private String partnerKey;
    /**
     * 授权码
     */
    @Value("${jushuitan.token}")
    private String token;
    /**
     * 请求时间，时间戳格式(Unix 纪元到当前时间的秒数),API服务端允许客户端请求最大时间误差为10分钟。
     */
    private long ts;

    public <T> T execute(String method, Object requestEntity, Class<T> responseEntity) {
        this.ts = Instant.now().getEpochSecond();
        String requestUrl = getRequestUrl(method);
        String body = "{}";
        if (Objects.nonNull(requestEntity)) {
            body = JSONObject.toJSONString(requestEntity);
            log.info("=> requestUrl:{},requestEntity:{}", requestUrl, body);
        }
        RequestBody requestBody = RequestBody.create(JSON, body);
        Request request = new Request.Builder().url(requestUrl).post(requestBody).build();
        try {
            Response response = this.client.newCall(request).execute();
            log.info("=> responseEntity:{}", JSONObject.toJSONString(response));
            if (response.code() == HttpStatus.SC_OK) {
                String bodyString = response.body().string();
                return JSONObject.parseObject(bodyString, responseEntity);
            }
        } catch (IOException e) {
            log.error("=> 请求聚水潭接口错误 url:{},body:{},error:{}", requestUrl, body, e.getMessage());
        }
        throw new IllegalArgumentException("=> 聚水潭请求错误 method:" + method + ",request:" + requestEntity);
    }

    /**
     * 拼装请求地址
     *
     * @param method
     * @return
     */
    private String getRequestUrl(String method) {
        return getRequestUrl(this.url, method);
    }

    /**
     * 拼装请求地址
     *
     * @param url
     * @param method
     * @return
     */
    private String getRequestUrl(String url, String method) {
        return String.format("%s?partnerid=%s&token=%s&method=%s&ts=%s&sign=%s", url, this.partnerId, this.token, method, this.ts, this.checkSign(method));
    }

    /**
     * 获取签名
     *
     * @param method
     * @return
     */
    private String checkSign(String method) {
        return DigestUtils.md5Hex(String.format("%s%stoken%sts%s%s", method, this.partnerId, this.token, this.ts, this.partnerKey));
    }
}
