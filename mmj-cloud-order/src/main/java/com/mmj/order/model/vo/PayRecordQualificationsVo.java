package com.mmj.order.model.vo;

public class PayRecordQualificationsVo {

    private Double orderAmount;

    private Integer userId;

    private String orderNo;

    private Integer orderType;

    private Double goodAmount;

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Double getGoodAmount() {
        return goodAmount;
    }

    public void setGoodAmount(Double goodAmount) {
        this.goodAmount = goodAmount;
    }
}
