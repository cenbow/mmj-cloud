package com.mmj.active.callCharge.model.vo;

import lombok.Data;

/**
 * @description: 调用支付信息
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
public class WxpayOrderEx {
    private String outTradeNo;

    private String appId;

    private String goodDesc;

    private String openId;

    private Integer totalFee;  // 分
}
