package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 批量判断用户是否已经领取优惠券
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "批量判断用户是否已经领取优惠券", description = "批量判断用户是否已经领取优惠券")
public class UserCouponReceiveDto {
    @ApiModelProperty(name = "优惠券ID")
    private Integer couponId;
    @ApiModelProperty(name = "是否已领取优惠券 true是 false否")
    private Boolean receiveStatus;
}
