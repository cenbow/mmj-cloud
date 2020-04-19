package com.mmj.pay.model;


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
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * <p>
 * 微信退款表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wxpay_refund")
@ApiModel(value = "WxpayRefund对象", description = "微信退款表")
public class WxpayRefund extends BaseModel {

    private static final long serialVersionUID = -3501007254669409766L;
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户号")
    @TableField("APPID")
    private String appid;

    @ApiModelProperty(value = "微信订单号")
    @TableField("TRANSACTION_ID")
    private String transactionId;

    @ApiModelProperty(value = "商户订单号")
    @TableField("OUT_TRADE_NO")
    private String outTradeNo;

    @ApiModelProperty(value = "商户退款单号")
    @TableField("OUT_REFUND_NO")
    private String outRefundNo;

    @ApiModelProperty(value = "订单金额")
    @TableField("TOTAL_FEE")
    private Integer totalFee;

    @ApiModelProperty(value = "退款金额")
    @TableField("REFUND_FEE")
    private Integer refundFee;

    @ApiModelProperty(value = "退款原因")
    @TableField("REFUND_DESC")
    private String refundDesc;

    @ApiModelProperty(value = "退款状态(0:正常退款;1:退款未完成)")
    @TableField("STATE")
    private Integer state;

    @ApiModelProperty(value = "退款失败时的原因")
    @TableField("ERROR_DESC")
    private String errorDesc;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
