package com.mmj.good.model;

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

/**
 * <p>
 * 组合商品表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_combination")
@ApiModel(value="GoodCombination对象", description="组合商品表")
public class GoodCombination extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "组合ID")
    @TableId(value = "COMBIN_ID", type = IdType.AUTO)
    private Integer combinId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "子商品ID")
    @TableField("SUB_GOOD_ID")
    private Integer subGoodId;

    @ApiModelProperty(value = "子商品销售ID")
    @TableField("SUB_SALE_ID")
    private Integer subSaleId;

    @ApiModelProperty(value = "子商品SKU")
    @TableField("SUB_GOOD_SKU")
    private String subGoodSku;

    @ApiModelProperty(value = "子商品库存")
    @TableField("SUB_GOOD_NUM")
    private Integer subGoodNum;

    @ApiModelProperty(value = "包裹数")
    @TableField("PACKAGE_NUM")
    private Integer packageNum;


}
