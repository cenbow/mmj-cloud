package com.mmj.active.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 砍价商品信息返回
 * @auther: KK
 * @date: 2019/6/14
 */
@Data
public class CutGoodDto {
    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "普通单价")
    private BigDecimal baseAmount;

    @ApiModelProperty(value = "店铺价格")
    private BigDecimal shopAmount;

    @ApiModelProperty(value = "拼团价")
    private BigDecimal tuanAmount;

    @ApiModelProperty(value = "会员价")
    private BigDecimal memberAmount;

    @ApiModelProperty(value = "销量")
    private Integer saleNum;

    @ApiModelProperty(value = "库存")
    private Integer warehouseNum; //库存数据

    @ApiModelProperty(value = "商品仓库 逗号分隔")
    private String wareHouseShow; //商品仓库展示名称，多个已逗号分隔,数据来源从goodWarehouses获取
}
