package com.mmj.common.model.third.recharge;

import lombok.Data;

/**
 * @description: 充值详情查询请求
 * @auther: KK
 * @date: 2019/8/31
 */
@Data
public class RechargeDetailsVo {
    /**
     * 业务订单号
     */
    private String outerId;
    /**
     * 渠道
     */
    private String channel;
}
