package com.mmj.active.threeSaleTenner.model;

public class ThreeSaleOrder {
    private String orderNo;

    private Integer orderStatus;  // 1:待付款  2：取消付款 3：已支付  4:已分享

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }
}
