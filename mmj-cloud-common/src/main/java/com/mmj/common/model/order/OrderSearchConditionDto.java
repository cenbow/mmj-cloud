package com.mmj.common.model.order;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

//订单搜索条件
@Data
@ToString
public class OrderSearchConditionDto {
    private Integer orderType;
    private Integer orderStatus;
    private Long createTimeStart;
    private Long createTimeEnd;
    private String orderNo;
    private String name;
    private String telNumber;
    private Integer virtualGood;
    private Boolean uploadErp;
    private Long userId;
    private String channel;
    private String source;
    private Integer currentPage;
    private Integer pageSize;
}
