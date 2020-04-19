package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 砍价商品列表
 * @auther: KK
 * @date: 2019/6/14
 */
@Data
public class CutGoodListDto {
    @ApiModelProperty(value = "砍价ID")
    private Integer cutId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @ApiModelProperty(value = "活动价")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "底价")
    private BigDecimal baseUnitPrice;

    @ApiModelProperty(value = "销量")
    private Integer saleNum;
}
