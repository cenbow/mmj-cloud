package com.mmj.user.member.model.Vo;

import javax.validation.constraints.NotNull;

public class PayIsBuyGiveVo {

    @NotNull
    private Double payAmount;

    @NotNull
    private Long userid;

    private String orderNo;

    @NotNull
    private Integer orderType;

    @NotNull
    private Double goodsAmount;

    public Double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(Double payAmount) {
        this.payAmount = payAmount;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
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

    public Double getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(Double goodsAmount) {
        this.goodsAmount = goodsAmount;
    }
}
