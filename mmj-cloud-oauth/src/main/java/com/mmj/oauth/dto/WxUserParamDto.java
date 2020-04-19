package com.mmj.oauth.dto;

import lombok.Data;

@Data
public class WxUserParamDto {

    private String code;

    private String encryptedData;

    private String iv;

    private String channel;

    private String openId;

    private String appid;
    
    private String appType;
    
    /**
     * 第三方渠道的ID，小程序授权时才传
     */
    private String advertiserId;
    
    /**
     * 第三方渠道的用户ID，小程序授权时才传
     */
    private String thridId;
    
}