package com.mmj.third.jushuitan.model.request;

import lombok.Data;

/**
 * @description: 聚水潭系统请求参数
 * @auther: KK
 * @date: 2019/6/5
 */
@Data
public class JushuitanRequest {
    /**
     * 合作方编号
     */
    private String partnerId;

    /**
     * 接入密钥
     */
    private String partnerKey;
    /**
     * 授权码
     */
    private String token;
    /**
     * 接口名称
     */
    private String method;
    /**
     * 请求时间，时间戳格式(Unix 纪元到当前时间的秒数),API服务端允许客户端请求最大时间误差为10分钟。
     */
    private Long ts;
    /**
     * 签名
     */
    private String sign;
}
