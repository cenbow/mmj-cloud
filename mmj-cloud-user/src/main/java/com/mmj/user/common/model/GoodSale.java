package com.mmj.user.common.model;

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
@TableName("t_good_sale")
@ApiModel(value="GoodSale对象", description="商品销售信息表")
public class GoodSale extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "销售ID")
    @TableId(value = "SALE_ID", type = IdType.AUTO)
    private Integer saleId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "SKU编码")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "普通单价")
    @TableField("BASE_PRICE")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "店铺价格")
    @TableField("SHOP_PRICE")
    private BigDecimal shopPrice;

    @ApiModelProperty(value = "拼团价")
    @TableField("TUAN_PRICE")
    private BigDecimal tuanPrice;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private BigDecimal memberPrice;

    @ApiModelProperty(value = "销量")
    @TableField("SALE_NUM")
    private Integer saleNum;

    @ApiModelProperty(value = "库存")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
