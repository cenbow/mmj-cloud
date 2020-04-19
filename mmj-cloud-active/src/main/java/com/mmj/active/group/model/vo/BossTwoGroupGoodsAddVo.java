package com.mmj.active.group.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @description: 二人团商品新增
 * @auther: KK
 * @date: 2019/7/22
 */
@Data
public class BossTwoGroupGoodsAddVo {
    @ApiModelProperty(value = "商品ID")
    @NotNull
    private Integer goodId;

    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "市场价")
    private String basePrice;

    @ApiModelProperty(value = "会员价")
    private String memberPrice;

    @NotNull
    @ApiModelProperty(value = "活动价格")
    private String activePrice;

    @NotNull
    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品分类")
    private String goodClass;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @NotNull
    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @NotNull
    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;

//    @ApiModelProperty(value = "商品排序")
//    private Integer goodOrder;
//
//    @ApiModelProperty(value = "是否限购 0不限购 1限购")
//    private Integer goodLimit;
//
//    @ApiModelProperty(value = "限购模式 1每单限购")
//    private Integer limitType;
//
//    @ApiModelProperty(value = "限购数量")
//    private Integer limitNum;
//
//    @ApiModelProperty(value = "团人数")
//    private Integer groupPerson;
}
