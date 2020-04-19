package com.mmj.active.group.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 接力购商品列表
 */
@Data
public class BossRelayGroupListDto {
    @ApiModelProperty(value = "市场价")
    private String basePrice;

    @ApiModelProperty(value = "会员价")
    private String memberPrice;

    @ApiModelProperty(value = "活动价格")
    private String activePrice;

    @ApiModelProperty(value = "店铺价格")
    private String shopPrice;

    @ApiModelProperty(value = "库存")
    private Integer goodNum;

    @ApiModelProperty(value = "关联ID")
    private Integer mapperyId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "卖点")
    private String sellingPoint;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品名称")
    private String goodName;

    @ApiModelProperty(value = "商品分类")
    private String goodClass;

    @ApiModelProperty(value = "商品图片")
    private String goodImage;

    @ApiModelProperty(value = "商品排序")
    private Integer goodOrder;

    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;

    @ApiModelProperty(value = "0:停用 1:启用")
    private Integer goodStatus;

    @ApiModelProperty(value = "接力人数")
    private Integer groupPerson;

    @ApiModelProperty(value = "接力模式: 1每单限购 如果是接力购 接力购模式：0:正常模式，1:老带新，2:所有人")
    private Integer limitType;

    @ApiModelProperty(value = "是否关注公众号")
    private String arg3;



}
