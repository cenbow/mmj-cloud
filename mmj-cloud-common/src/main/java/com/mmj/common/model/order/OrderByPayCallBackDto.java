package com.mmj.common.model.order;

import lombok.Data;

import java.util.List;

@Data
public class OrderByPayCallBackDto {


    private String orderNo;

    private Integer orderStatus;

    private Integer orderType;

    private Integer businessId;  // 活动订单关联ID

    private Integer orderAmount;

    private Integer goodAmount;

    private String orderSource;

    private boolean memberOrder; // 是否会员订单

    private Integer goldPrice;  //  买买金兑换金额

    private Integer goldNum; // 买买金

    private String passingData;

    private Long userId;

    private OrderPayDto orderPayDto;   // 支付

    private List<OrderPackageDto> packages;  // 包裹

    // 收货人信息
    private OrderLogisticsDto orderLogistics;

}
