package com.mmj.third.jushuitan.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 普通商品查询
 * @auther: KK
 * @date: 2019/8/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("聚水潭普通商品查询返回参数")
public class SkuQueryDto {
    @ApiModelProperty(value = "商品编码")
    private String sku;
    @ApiModelProperty(value = "款式编码")
    private String iId;
    @ApiModelProperty(value = "商品名称")
    private String name;
    @ApiModelProperty(value = "销售价")
    private BigDecimal salePrice;
    @ApiModelProperty(value = "成本价")
    private BigDecimal costPrice;
    @ApiModelProperty(value = "颜色规格")
    private String propertiesValue;
    @ApiModelProperty(value = "图片地址")
    private String pic;
    @ApiModelProperty(value = "国标码")
    private String skuCode;
    @ApiModelProperty(value = "辅助码")
    private String skuCodes;
    @ApiModelProperty(value = "是否启用，0：备用，1：启用，-1：禁用")
    private Integer enabled;
    @ApiModelProperty(value = "重量")
    private BigDecimal weight;
    @ApiModelProperty(value = "市场价")
    private BigDecimal marketPrice;
    @ApiModelProperty(value = "修改时间")
    private String modified;
    @ApiModelProperty(value = "商品简称")
    private String shortName;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "创建时间")
    private String created;
    @ApiModelProperty(value = "分类")
    private String category;
    @ApiModelProperty(value = "虚拟分类")
    private String vcName;
}
