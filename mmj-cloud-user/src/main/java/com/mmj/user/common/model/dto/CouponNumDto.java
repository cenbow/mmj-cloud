package com.mmj.user.common.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 优惠券当天发放数量
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "优惠券发放数量", description = "优惠券发放数量")
public class CouponNumDto {
    @ApiModelProperty("优惠券ID")
    private Integer couponId;
    @ApiModelProperty("优惠券发放数量")
    private Integer num;
}
