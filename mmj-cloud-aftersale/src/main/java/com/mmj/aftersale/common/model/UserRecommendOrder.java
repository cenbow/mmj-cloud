package com.mmj.aftersale.common.model;

/**
 * 给订单使用
 */
public class UserRecommendOrder {
    private String orderNo;

    private int status; // 1: 待评价  2:已评价待分享

    private String goodSku;

    private Integer recommendId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public Integer getRecommendId() {
        return recommendId;
    }

    public void setRecommendId(Integer recommendId) {
        this.recommendId = recommendId;
    }
}
