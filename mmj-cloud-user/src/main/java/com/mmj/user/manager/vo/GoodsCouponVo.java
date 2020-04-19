package com.mmj.user.manager.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 商品优惠券请求
 * @auther: KK
 * @date: 2019/7/11
 */
@Data
@ApiModel(value = "商品优惠券请求", description = "商品优惠券请求")
public class GoodsCouponVo {
    @ApiModelProperty(value = "商品ID")
    @NotNull
    private Integer goodId;

    @ApiModelProperty(value = "商品分类编码")
    @NotNull
    private String goodClass;

}
