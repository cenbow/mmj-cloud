package com.mmj.active.callCharge.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 话费充值
 * @auther: KK
 * @date: 2019/8/1
 */
@Data
public class RechargeOrderVo {
    private Long userId;
    /**
     * 商品id
     */
    @NotNull
    private Integer goodsId;

    /**
     * 手机号码
     */
    @NotNull
    private String mobile;

    private String source;
    private String channel;
}
