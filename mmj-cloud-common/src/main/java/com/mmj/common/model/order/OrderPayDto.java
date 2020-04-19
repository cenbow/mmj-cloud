package com.mmj.common.model.order;

import lombok.Data;

import java.util.Date;

@Data
public class OrderPayDto {
    //单位:分
    private Integer payAmount;

    //1:微信支付
    private Integer payType;

    private Date payTime;

    private String orderNo;

}
