package com.mmj.aftersale.common.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import lombok.Data;
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
public class WxpayRefund extends BaseModel {

    private static final long serialVersionUID = -3501007254669409766L;
    private Long id;

    private String appid;

    private String transactionId;

    private String outTradeNo;

    private String outRefundNo;

    private Integer totalFee;

    private Integer refundFee;

    private String refundDesc;

    private Integer state;

    private String errorDesc;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
