package com.mmj.order.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 订单支付信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_payment")
@ApiModel(value="OrderPayment对象", description="订单支付信息表")
public class OrderPayment extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "支付ID")
    @TableId(value = "PAY_ID", type = IdType.AUTO)
    private Integer payId;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "支付类型")
    @TableField("PAY_TYPE")
    private String payType;

    @ApiModelProperty(value = "支付金额")
    @TableField("PAY_AMOUNT")
    private Integer payAmount;

    @ApiModelProperty(value = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("PAY_TIME")
    private Date payTime;

    @ApiModelProperty(value = "支付状态")
    @TableField("PAY_STATUS")
    private Integer payStatus;

    @ApiModelProperty(value = "支付流水")
    @TableField("PAY_NO")
    private String payNo;

    @ApiModelProperty(value = "支付备注")
    @TableField("PAY_DESC")
    private String payDesc;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
