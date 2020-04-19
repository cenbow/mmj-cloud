package com.mmj.user.manager.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 优惠券用户
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@ApiModel(value = "使用优惠券", description = "使用优惠券")
public class UseUserCouponVo {
    @ApiModelProperty(name = "用户ID，可为空（当为空时，默认获取当前用户）")
    private Long userId;
    @NotNull
    @ApiModelProperty(name = "订单号")
    private String orderNo;
    @ApiModelProperty(name = "用户优惠券编码")
    private Integer couponCode;
    @ApiModelProperty(name = "使用状态 true使用优惠券 false恢复优惠券")
    private Boolean useStatus = true;
}
