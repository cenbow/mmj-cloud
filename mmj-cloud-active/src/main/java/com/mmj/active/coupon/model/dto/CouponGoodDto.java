package com.mmj.active.coupon.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 优惠券商品信息
 * @auther: KK
 * @date: 2019/8/26
 */
@Data
public class CouponGoodDto {
    @ApiModelProperty(value = "映射ID")
    private Integer mapperId;

    @ApiModelProperty(value = "范围类型 1：可用商品，表示指定商品可以使用；2：不可用商品")
    private String scopeType;

    @ApiModelProperty(value = "优惠券ID")
    private Integer couponId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "店铺价")
    private String shopAmount;

}
