package com.mmj.active.common.model.vo;

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
@ApiModel(value = "用户优惠券", description = "用户优惠券")
public class UserCouponVo {
    @ApiModelProperty(name = "用户ID，可为空（当为空时，默认获取当前用户）")
    private Long userId;
    @NotNull
    @ApiModelProperty(name = "优惠券ID")
    private Integer couponId;
    @NotNull
    @ApiModelProperty(name = "优惠券来源 SYSTEM：系统发送，TOPIC：专题页领取，BRUSH：刷一刷，INDEX：弹出优惠券领用")
    private String couponSource;
}
