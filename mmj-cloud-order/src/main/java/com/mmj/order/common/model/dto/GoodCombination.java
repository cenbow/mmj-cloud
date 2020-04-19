package com.mmj.order.common.model.dto;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class GoodCombination extends BaseModel {


    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组合ID")
    private Integer combinId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品SKU")
    private String goodSku;

    @ApiModelProperty(value = "子商品ID")
    private Integer subGoodId;

    @ApiModelProperty(value = "子商品销售ID")
    private Integer subSaleId;

    @ApiModelProperty(value = "子商品SKU")
    private String subGoodSku;

    @ApiModelProperty(value = "子商品库存")
    private Integer subGoodNum;

    @ApiModelProperty(value = "包裹数")
    private Integer packageNum;



}
