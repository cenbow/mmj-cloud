package com.mmj.common.model.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

//订单搜索结果
@Data
public class OrderSearchResultDto {

    private String orderNo;
    private String packageNo;
    private Boolean uploadErp;
    private Integer orderType;
    private Integer orderStatus;
    private String orderStatusDesc;
    private Date orderDate;
    private Date payDate;
    private String orderAmount;
    private Integer payAmount;
    private List<OrdersMQDto.Goods> goods;
    private String userId;
    private Integer virtualGood;
    private String channel;
    private String source;
}
