package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @description: 用户领取优惠券返回
 * @auther: KK
 * @date: 2019/8/5
 */
@Data
@AllArgsConstructor
@ApiModel(value = "用户领取优惠券返回信息", description = "用户领取优惠券返回信息")
public class UserReceiveCouponDto {
    @ApiModelProperty(value = "领取状态 -1出错 0已领取 1领取成功 2已发完")
    private int resultStatus = 0;
    @ApiModelProperty(value = "用户优惠券信息")
    private UserCouponDto userCoupon;

    public UserReceiveCouponDto() {
    }

    public UserReceiveCouponDto(int resultStatus) {
        this(resultStatus, null);
    }

}
