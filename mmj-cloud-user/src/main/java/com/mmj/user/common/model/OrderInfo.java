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

import java.util.Date;

/**
 * <p>
 * 订单信息表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_info")
@ApiModel(value="OrderInfo对象", description="订单信息表")
public class OrderInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "订单ID")
    @TableId(value = "ORDER_ID", type = IdType.AUTO)
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "订单类型 CUT 砍价")
    @TableField("ORDER_TYPE")
    private Integer orderType;

    @ApiModelProperty(value = "关联ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "订单状态")
    @TableField("ORDER_STATUS")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单总金额")
    @TableField("ORDER_AMOUNT")
    private Integer orderAmount;

    @ApiModelProperty(value = "商品金额")
    @TableField("GOOD_AMOUNT")
    private Integer goodAmount;

    @ApiModelProperty(value = "活动优惠金额")
    @TableField("DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    @TableField("COUPON_AMOUNT")
    private Integer couponAmount;

    @ApiModelProperty(value = "快递费")
    @TableField("EXPRESS_AMOUNT")
    private Integer expressAmount;

    @ApiModelProperty(value = "过期时间")
    @TableField("EXPIRT_TIME")
    private Date expirtTime;

    @ApiModelProperty(value = "下单渠道")
    @TableField("ORDER_SOURCE")
    private String orderSource;

    @ApiModelProperty(value = "买家备注")
    @TableField("CONSUMER_DESC")
    private String consumerDesc;

    @ApiModelProperty(value = "买买金兑换金额")
    @TableField("GOLD_PRICE")
    private Integer goldPrice;

    @ApiModelProperty(value = "是否会员订单 0普通订单 1会员订单")
    @TableField("MEMBER_ORDER")
    private Boolean memberOrder;

    @ApiModelProperty(value = "是否有售后")
    @TableField("HAS_AFTER_SALE")
    private Boolean hasAfterSale;

    @ApiModelProperty(value = "使用买买金的数量")
    @TableField("GOLD_NUM")
    private Integer goldNum;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "传递数据")
    @TableField("PASSING_DATA")
    private String passingData;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
