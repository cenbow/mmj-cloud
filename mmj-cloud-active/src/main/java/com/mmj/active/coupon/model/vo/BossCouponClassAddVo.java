package com.mmj.active.coupon.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: boss新增优惠券关联商品分类信息
 * @auther: KK
 * @date: 2019/6/27
 */
@Data
@ApiModel(value = "优惠券商品分类关联", description = "优惠券商品分类关联")
public class BossCouponClassAddVo {
    @ApiModelProperty(value = "商品分类编码")
    private String goodClass;
    @ApiModelProperty(value = "分类名称")
    private String className;
}
