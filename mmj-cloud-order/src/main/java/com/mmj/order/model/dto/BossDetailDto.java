package com.mmj.order.model.dto;


import com.mmj.order.model.OrderLogistics;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class BossDetailDto {

    private String orderNo;

    private Integer orderType;

    private String orderTypeDesc;

    private Integer orderStauts;

    private String orderStatusDesc;

    private String orderAmount;

    private String goodAmount;

    private String couponAmount;

    private String discountAmount;

    private String freight;

    private String freightRemarks;

    private String resultAmount;  // 返现金额

    private String createDate;

    private String expireDate;

    private Integer afterSaleStatus;   //  售后状态

    private String afterSaleStatusDesc;  // 售后状态描述

    private  boolean jstStatus; // 聚水潭状态

    private boolean memberOrder;

    private String CreaterId;   // 下单用户id

    // 成团信息
    private GroupDto GroupInfo;

    // 支付信息
    private OrderPayinfoDto orderPayinfo;

    // 寄回快递信息
    private ShippingDto shipping;


    // 收货人信息
    private OrderLogisticsDto orderLogistics;

    // 包裹信息
    private List<OrderPackageDto> packages;

}
