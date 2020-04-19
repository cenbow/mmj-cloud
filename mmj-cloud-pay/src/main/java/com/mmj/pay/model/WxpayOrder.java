package com.mmj.pay.model;


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
 * 微信支付订单表
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wxpay_order")
@ApiModel(value="WxpayOrder对象", description="微信支付订单表")
public class WxpayOrder extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "PAY_ID", type = IdType.AUTO)
    private Integer payId;

    @ApiModelProperty(value = "公众账号ID")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "商户号")
    @TableField("MCH_ID")
    private String mchId;

    @ApiModelProperty(value = "设备号")
    @TableField("DEVICE_INFO")
    private String deviceInfo;

    @ApiModelProperty(value = "商品描述")
    @TableField("GOOD_DESC")
    private String goodDesc;

    @ApiModelProperty(value = "商品详情")
    @TableField("GOOD_DETAIL")
    private String goodDetail;

    @ApiModelProperty(value = "附加数据")
    @TableField("PAY_ATTACH")
    private String payAttach;

    @ApiModelProperty(value = "商户订单号")
    @TableField("OUT_TRADE_NO")
    private String outTradeNo;

    @ApiModelProperty(value = "微信生成的订单号")
    @TableField("TRANSACTION_ID")
    private String transactionId;

    @ApiModelProperty(value = "标价币种")
    @TableField("FEE_TYPE")
    private String feeType;

    @ApiModelProperty(value = "标价金额")
    @TableField("TOTAL_FEE")
    private Integer totalFee;

    @ApiModelProperty(value = "终端IP")
    @TableField("SPBILL_CREATE_IP")
    private String spbillCreateIp;

    @ApiModelProperty(value = "交易起始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "交易结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "订单优惠标记")
    @TableField("GOOD_TAG")
    private String goodTag;

    @ApiModelProperty(value = "通知地址")
    @TableField("NOTIFY_URL")
    private String notifyUrl;

    @ApiModelProperty(value = "交易类型")
    @TableField("TRADE_TYPE")
    private String tradeType;

    @ApiModelProperty(value = "交易状态(order:下单状态;pay:已支付)")
    @TableField("TRADE_STATUS")
    private String tradeStatus;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private String goodId;

    @ApiModelProperty(value = "指定支付方式")
    @TableField("LIMIT_PAY")
    private String limitPay;

    @ApiModelProperty(value = "用户标识")
    @TableField("OPEN_ID")
    private String openId;

    @ApiModelProperty(value = "场景信息")
    @TableField("SCENE_INFO")
    private String sceneInfo;

    @ApiModelProperty(value = "支付会话ID")
    @TableField("PREPAY_ID")
    private String prepayId;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
