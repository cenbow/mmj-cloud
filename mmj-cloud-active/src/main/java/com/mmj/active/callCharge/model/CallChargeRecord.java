package com.mmj.active.callCharge.model;

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
 * 
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_call_charge_record")
@ApiModel(value="CallChargeRecord对象", description="")
public class CallChargeRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "拆解订单号")
    @TableField("DIS_ORDER_NO")
    private String disOrderNo;

    @ApiModelProperty(value = "订单状态 1待支付 2已支付")
    @TableField("ORDER_STATUS")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单金额")
    @TableField("ORDER_AMOUNT")
    private Integer orderAmount;

    @ApiModelProperty(value = "商品金额")
    @TableField("GOODS_AMOUNT")
    private Integer goodsAmount;

    @ApiModelProperty(value = "优惠金额")
    @TableField("DISCOUNTED_PRICE")
    private Integer discountedPrice;

    @ApiModelProperty(value = "充值商品ID")
    @TableField("GOODS_ID")
    private Integer goodsId;

    @ApiModelProperty(value = "充值商品ID")
    @TableField("RECHARGE_ITEM_ID")
    private String rechargeItemId;

    @ApiModelProperty(value = "充值手机号")
    @TableField("RECHARGE_MOBILE")
    private String rechargeMobile;

    @ApiModelProperty(value = "充值金额")
    @TableField("RECHARGE_AMOUNT")
    private Integer rechargeAmount;

    @ApiModelProperty(value = "充值状态 -1待充值 0充值中 1充值成功 2充值超时（待重试） 3充值失败 4充值订单过期")
    @TableField("RECHARGE_STATUS")
    private Integer rechargeStatus;

    @ApiModelProperty(value = "充值失败时平台返回信息")
    @TableField("RECHARGE_ERROR_RESPONSE")
    private String rechargeErrorResponse;

    @ApiModelProperty(value = "是否权益订单")
    @TableField("RIGHT_ORDER")
    private Boolean rightOrder;

    @ApiModelProperty(value = "备注")
    @TableField("REMARKS")
    private String remarks;

    @ApiModelProperty(value = "是否有效 0无效 1有效")
    @TableField("ACTIVE")
    private Boolean active;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATE_BY")
    private Long createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_AT")
    private Date createAt;

    @ApiModelProperty(value = "更新人")
    @TableField("UPDATE_BY")
    private Long updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_AT")
    private Date updateAt;


}
