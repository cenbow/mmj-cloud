package com.mmj.order.model;

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
 * 订单包裹信息
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_package")
@ApiModel(value="OrderPackage对象", description="订单包裹信息")
public class OrderPackage extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "包裹ID")
    @TableId(value = "PACKAGE_ID", type = IdType.AUTO)
    private Integer packageId;

    @ApiModelProperty(value = "包裹编号")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "订单状态")
    @TableField("ORDER_STATUS")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单类型")
    @TableField("ORDER_TYPE")
    private Integer orderType;

    @ApiModelProperty(value = "订单金额")
    @TableField("ORDER_AMOUNT")
    private Integer orderAmount;

    @ApiModelProperty(value = "商品金额")
    @TableField("GOOD_AMOUNT")
    private Integer goodAmount;

    @ApiModelProperty(value = "快递费")
    @TableField("LOGISTICS_AMOUNT")
    private Integer logisticsAmount;

    @ApiModelProperty(value = "包裹备注")
    @TableField("PACKAGE_DESC")
    private String packageDesc;

    @ApiModelProperty(value = "活动优惠金额")
    @TableField("DISCOUNT_AMOUNT")
    private Integer discountAmount;

    @ApiModelProperty(value = "优惠券优惠金额")
    @TableField("COUPON_AMOUNT")
    private Integer couponAmount;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_GOOD")
    private Integer virtualGood;

    @ApiModelProperty(value = "买买金兑换金额")
    @TableField("GOLD_PRICE")
    private Integer goldPrice;

    @ApiModelProperty(value = "是否会员订单 0普通订单 1会员订单")
    @TableField("MEMBER_ORDER")
    private Boolean memberOrder;

    @ApiModelProperty(value = "是否有上传erp")
    @TableField("UPLOAD_ERP")
    private Boolean uploadErp;

    @ApiModelProperty(value = "删除标识")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
