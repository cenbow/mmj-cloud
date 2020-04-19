package com.mmj.common.model.third.recharge;

import lombok.Data;

/**
 * @description: 话费充值请求
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
public class RechargeVo {
    /**
     * 充值手机号
     */
    private String mobile;
    /**
     * 充值金额
     */
    private String amount;
    /**
     * 业务订单号
     */
    private String outerId;
    /**
     * 回调地址
     */
    private String callBackUrl;
}
