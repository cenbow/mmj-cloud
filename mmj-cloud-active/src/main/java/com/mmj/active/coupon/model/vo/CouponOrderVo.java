package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @description: 订单优惠券
 * @auther: KK
 * @date: 2019/7/11
 */
public class CouponOrderVo {
    @NotNull
    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;
}
