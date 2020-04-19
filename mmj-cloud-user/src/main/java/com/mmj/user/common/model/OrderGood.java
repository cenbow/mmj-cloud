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
 * 订单商品表
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_good")
@ApiModel(value="OrderGood对象", description="订单商品表")
public class OrderGood extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "关联ID")
    @TableId(value = "OG_ID", type = IdType.AUTO)
    private Integer ogId;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品分类")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "分类名称（拼接）")
    @TableField("CLASS_NAME")
    private String className;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品数量")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "商品金额")
    @TableField("GOOD_AMOUNT")
    private BigDecimal goodAmount;

    @ApiModelProperty(value = "规格ID")
    @TableField("MODEL_ID")
    private String modelId;

    @ApiModelProperty(value = "规格名称（拼接）")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_FLAG")
    private Integer virtualFlag;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "活动优惠金额")
    @TableField("DISCOUNT_AMOUNT")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    @TableField("COUPON_AMOUNT")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


    @ApiModelProperty(value = "包裹号")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "商品单价")
    @TableField("GOOD_PRICE")
    private Integer goodPrice;

    @ApiModelProperty(value = "虚拟商品类型 1:优惠券,2:买买金,3:话费")
    @TableField("VIRTUAL_TYPE")
    private Integer virtualType;

    @ApiModelProperty(value = "快照")
    @TableField("SNAPSHOT_DATA")
    private String snapshotData;

    @ApiModelProperty(value = "买买金兑换金额")
    @TableField("GOLD_PRICE")
    private Integer goldPrice;

    @ApiModelProperty(value = "会员价")
    @TableField("MEMBER_PRICE")
    private Integer memberPrice;

    @ApiModelProperty(value = "快递费用")
    @TableField("LOGISTICS_AMOUNT")
    private Integer logisticsAmount;


}
