package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: boss新增优惠券关联商品信息
 * @auther: KK
 * @date: 2019/6/27
 */
@Data
@ApiModel(value = "优惠券商品关联", description = "优惠券商品关联")
public class BossCouponGoodAddVo {
    @ApiModelProperty(value = "商品ID")
    private Integer goodId;
    @ApiModelProperty(value = "商品名称")
    private String goodName;
    @ApiModelProperty(value = "商品图片")
    private String goodImage;
    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;
}
