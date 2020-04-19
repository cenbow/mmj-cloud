package com.mmj.pay.common.model.dto;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class GoodSale extends BaseModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "SKU编码")
    private String goodSku;

    @ApiModelProperty(value = "普通单价")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "店铺价格")
    private BigDecimal shopPrice;

    @ApiModelProperty(value = "拼团价")
    private BigDecimal tuanPrice;

    @ApiModelProperty(value = "会员价")
    private BigDecimal memberPrice;

    @ApiModelProperty(value = "销量")
    private Integer saleNum;

    @ApiModelProperty(value = "库存")
    private Integer goodNum;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;
}
