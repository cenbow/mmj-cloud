package com.mmj.pay.sdk.weixin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 买买家支付的配置
 */
@Component
public class MmjPayWxPayConfig extends WXPayConfig {

    private String appID;

    @Value("${app.pay.weixin.mchid}")
    private String mchID;

    @Value("${app.pay.weixin.key}")
    private String key;

    public MmjPayWxPayConfig(){
        this.appID = appID;
    }

    private InputStream inputStream;

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    @Override
    public String getMchID() {
        return mchID;
    }

    public void setMchID(String mchId) {
        this.mchID = mchId;
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public InputStream getCertStream() {
        return inputStream;
    }

    @Override
    public IWXPayDomain getWXPayDomain() {
        return new MmjIWXPayDomain();
    }
}
