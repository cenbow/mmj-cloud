package com.mmj.pay.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * VIEW
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="ViewWxpayOrder对象", description="VIEW")
public class ViewWxpayOrder extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableField("PAY_ID")
    private Integer payId;

    @TableField("APP_ID")
    private String appId;

    @TableField("MCH_ID")
    private String mchId;

    @TableField("DEVICE_INFO")
    private String deviceInfo;

    @TableField("GOOD_DESC")
    private String goodDesc;

    @TableField("GOOD_DETAIL")
    private String goodDetail;

    @TableField("PAY_ATTACH")
    private String payAttach;

    @TableField("OUT_TRADE_NO")
    private String outTradeNo;

    @TableField("TRANSACTION_ID")
    private String transactionId;

    @TableField("FEE_TYPE")
    private String feeType;

    @TableField("TOTAL_FEE")
    private Integer totalFee;

    @TableField("SPBILL_CREATE_IP")
    private String spbillCreateIp;

    @TableField("START_TIME")
    private Date startTime;

    @TableField("END_TIME")
    private Date endTime;

    @TableField("GOOD_TAG")
    private String goodTag;

    @TableField("NOTIFY_URL")
    private String notifyUrl;

    @TableField("TRADE_TYPE")
    private String tradeType;

    @TableField("TRADE_STATUS")
    private String tradeStatus;

    @TableField("GOOD_ID")
    private String goodId;

    @TableField("LIMIT_PAY")
    private String limitPay;

    @TableField("OPEN_ID")
    private String openId;

    @TableField("SCENE_INFO")
    private String sceneInfo;

    @TableField("PREPAY_ID")
    private String prepayId;

    @TableField("CREATER_ID")
    private Long createrId;

    @TableField("CREATER_TIME")
    private Date createrTime;


}
