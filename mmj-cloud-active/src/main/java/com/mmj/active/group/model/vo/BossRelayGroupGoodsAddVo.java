package com.mmj.active.group.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BossRelayGroupGoodsAddVo {
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

    @NotNull
    @ApiModelProperty(value = "接力人数")
    private Integer groupPerson;

    @NotNull
    @ApiModelProperty(value = "接力模式: 1每单限购 如果是接力购 接力购模式：0:正常模式，1:老带新，2:所有人")
    private Integer limitType;

    @NotNull
    @ApiModelProperty(value = "接力模式: 是否关注公众号 1：是，0：否")
    private String arg3;





}
