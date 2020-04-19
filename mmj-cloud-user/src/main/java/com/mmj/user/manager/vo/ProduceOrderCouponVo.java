package com.mmj.user.manager.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @description: 下单时获取可用优惠券
 * @auther: KK
 * @date: 2019/7/29
 */
@Data
@ApiModel(value = "下单时获取可用优惠券", description = "下单时获取可用优惠券")
public class ProduceOrderCouponVo {
    @ApiModelProperty(value = "订单类型")
    @NotNull
    private Integer orderType;

    @ApiModelProperty(value = "商品信息")
    @Size(min = 1)
    private List<Goods> goods;

    @Data
    public static class Goods {
        @ApiModelProperty(value = "款式ID")
        @NotNull
        private Integer goodId;

        @ApiModelProperty(value = "商品ID")
        @NotNull
        private Integer saleId;

        @ApiModelProperty(value = "商品单价")
        @NotNull
        private String unitPrice;

        @ApiModelProperty(value = "会员单价")
        @NotNull
        private String memberPrice;

        @ApiModelProperty(value = "购买数量")
        @NotNull
        private Integer goodNum;

        @ApiModelProperty(value = "商品分类编码")
        private String goodClass;
    }
}
