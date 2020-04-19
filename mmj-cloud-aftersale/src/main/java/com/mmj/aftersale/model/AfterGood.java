package com.mmj.aftersale.model;

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
 * 售后商品信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_after_good")
@ApiModel(value="AfterGood对象", description="售后商品信息表")
public class AfterGood extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "AFTER_ID", type = IdType.AUTO)
    private Integer afterId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "售后订单号")
    @TableField("AFTER_SALE_NO")
    private String afterSaleNo;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "销售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "规格名称")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "商品数量")
    @TableField("GOOD_NUM")
    private Integer goodNum;

    @ApiModelProperty(value = "原价")
    @TableField("BASE_PRICE")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "单价")
    @TableField("ACTIVE_PRICE")
    private BigDecimal activePrice;

    @ApiModelProperty(value = "活动优惠金额")
    @TableField("DISCOUNT_AMOUNT")
    private BigDecimal discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    @TableField("COUPON_AMOUNT")
    private BigDecimal couponAmount;

    @ApiModelProperty(value = "快递费")
    @TableField("LOGISTICS_AMOUNT")
    private String logisticsAmount;

    @ApiModelProperty(value = "包裹编号")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "返现金额")
    @TableField("REBACK_AMOUNT")
    private BigDecimal rebackAmount;

    @ApiModelProperty(value = "退款金额")
    @TableField("RETURN_AMOUNT")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "退款备注")
    @TableField("RETURN_DESC")
    private String returnDesc;

    @ApiModelProperty(value = "订单金额")
    @TableField("ORDER_AMOUNT")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "备注")
    @TableField("AFTER_DESC")
    private String afterDesc;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
