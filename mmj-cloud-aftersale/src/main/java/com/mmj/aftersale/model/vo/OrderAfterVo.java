package com.mmj.aftersale.model.vo;

import javax.validation.constraints.NotNull;

public class OrderAfterVo {


    @NotNull
    private String orderNo;


    private String userId;

    public OrderAfterVo() {


    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
