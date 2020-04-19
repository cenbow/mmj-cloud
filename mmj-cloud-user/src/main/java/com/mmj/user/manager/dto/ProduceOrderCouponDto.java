package com.mmj.user.manager.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 下单时获取可用优惠券返回
 * @auther: KK
 * @date: 2019/7/29
 */
@Data
@ApiModel(value = "下单时获取可用优惠券返回", description = "下单时获取可用优惠券返回")
public class ProduceOrderCouponDto {
    @ApiModelProperty("有效优惠券")
    private List<UserCouponDto> normals;

    @ApiModelProperty("无效优惠券")
    private List<UserCouponDto> invalids;

}
