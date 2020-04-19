package com.mmj.user.shopCart.model;

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
 * 购物车表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_shopcart")
@ApiModel(value="UserShopCart对象", description="购物车表")
public class UserShopCart extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "CART_ID", type = IdType.AUTO)
    private Integer cartId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "销售表ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGES")
    private String goodImages;

    @ApiModelProperty(value = "商品数量")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "规格ID")
    @TableField("MODEL_ID")
    private Integer modelId;

    @ApiModelProperty(value = "规格名称")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "是否删除")
    @TableField("DELETE_FLAG")
    private Boolean deleteFlag;

    @ApiModelProperty(value = "单价")
    @TableField("GOOD_PRICE")
    private BigDecimal goodPrice;

    @ApiModelProperty(value = "原价")
    @TableField("BASE_PRICE")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "是否选中")
    @TableField("SELECT_FLAG")
    private Boolean selectFlag;

    @ApiModelProperty(value = "商品类型")
    @TableField("GOOD_TYPE")
    private String goodType;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private BigDecimal memberPrice;

    @ApiModelProperty(value = "是否会员专享商品")
    @TableField("MEMBER_FLAG")
    private Boolean memberFlag;

    @ApiModelProperty(value = "是否组合商品")
    @TableField("COMBINA_FLAG")
    private Boolean combinaFlag;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_FLAG")
    private Boolean virtualFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIEFY_ID")
    private Long modiefyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
