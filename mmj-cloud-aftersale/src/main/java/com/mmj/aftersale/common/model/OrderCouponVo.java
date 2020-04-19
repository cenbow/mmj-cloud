package com.mmj.aftersale.common.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 订单号关联的优惠券
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@ApiModel(value = "订单号关联的优惠券", description = "订单号关联的优惠券")
public class OrderCouponVo {
    @ApiModelProperty(name = "用户ID，可为空（当为空时，默认获取当前用户）")
    private Long userId;
    @NotNull
    @ApiModelProperty(name = "订单号")
    private String orderNo;
    @ApiModelProperty(name = "使用状态 true已使用的优惠券 false未使用的优惠券(已恢复)")
    private Boolean useStatus = true;
}
