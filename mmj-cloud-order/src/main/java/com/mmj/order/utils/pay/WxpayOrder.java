package com.mmj.order.utils.pay;


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
@ApiModel(value = "WxpayOrder对象", description = "微信支付订单表")
public class WxpayOrder extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Integer payId;

    @ApiModelProperty(value = "公众账号ID")
    private String appId;

    @ApiModelProperty(value = "商户号")
    private String mchId;

    @ApiModelProperty(value = "设备号")
    private String deviceInfo;

    @ApiModelProperty(value = "商品描述")
    private String goodDesc;

    @ApiModelProperty(value = "商品详情")
    private String goodDetail;

    @ApiModelProperty(value = "附加数据")
    private String payAttach;

    @ApiModelProperty(value = "商户订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "微信生成的订单号")
    private String transactionId;

    @ApiModelProperty(value = "标价币种")
    private String feeType;

    @ApiModelProperty(value = "标价金额")
    private Integer totalFee;

    @ApiModelProperty(value = "终端IP")
    private String spbillCreateIp;

    @ApiModelProperty(value = "交易起始时间")
    private Date startTime;

    @ApiModelProperty(value = "交易结束时间")
    private Date endTime;

    @ApiModelProperty(value = "订单优惠标记")
    private String goodTag;

    @ApiModelProperty(value = "通知地址")
    private String notifyUrl;

    @ApiModelProperty(value = "交易类型")
    private String tradeType;

    @ApiModelProperty(value = "交易状态(order:下单状态;pay:已支付)")
    private String tradeStatus;

    @ApiModelProperty(value = "商品ID")
    private String goodId;

    @ApiModelProperty(value = "指定支付方式")
    private String limitPay;

    @ApiModelProperty(value = "用户标识")
    private String openId;

    @ApiModelProperty(value = "场景信息")
    private String sceneInfo;

    @ApiModelProperty(value = "支付会话ID")
    private String prepayId;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;


}
