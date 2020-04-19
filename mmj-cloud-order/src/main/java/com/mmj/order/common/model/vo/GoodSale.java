package com.mmj.order.common.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 商品销售信息表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="GoodSale对象", description="商品销售信息表")
public class GoodSale extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "销售ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品ID")
    private Integer goodId;

    @ApiModelProperty(value = "SKU编码")
    @TableField("GOOD_SKU")
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
