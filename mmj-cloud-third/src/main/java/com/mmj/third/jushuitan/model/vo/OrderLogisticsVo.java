package com.mmj.third.jushuitan.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * 订单包裹收件人信息
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderLogisticsVo {


    private Long userId;

    @NotNull
    private String packageNo;


    private String orderNo;

    @NotNull
    private String companyName;  // 快递公司名称

    private String companyCode; // 快递公司编码

    @NotNull
    private String logisticsNo;

    private String logisticsAmount;

    private String country;


    private String province;


    private String city;


    private String arer;


    private String consumerAddr;


    private String consumerName;


    private String consumerMobile;

    private Date sendTime;

    public OrderLogisticsVo() {

    }
}
