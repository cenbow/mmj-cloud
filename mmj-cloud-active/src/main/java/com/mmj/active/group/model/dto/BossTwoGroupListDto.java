package com.mmj.active.group.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @description: 二人团商品
 * @auther: KK
 * @date: 2019/7/23
 */
@Data
public class BossTwoGroupListDto {
    @ApiModelProperty(value = "市场价")
    private String basePrice;

    @ApiModelProperty(value = "会员价")
    private String memberPrice;

    @ApiModelProperty(value = "活动价格")
    private String activePrice;

    @ApiModelProperty(value = "店铺价格")
    private String shopPrice;

    @ApiModelProperty(value = "库存")
    private Integer activeStore;

    @ApiModelProperty(value = "关联ID")
    private String mapperyId;

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

    @ApiModelProperty(value = "是否限购 0不限购 1限购")
    private Integer goodLimit;

    @ApiModelProperty(value = "限购模式 1每单限购 如果是接力购 接力购模式：0:正常模式，1:老带新，2:所有人")
    private Integer limitType;

    @ApiModelProperty(value = "限购数量")
    private Integer limitNum;
}
