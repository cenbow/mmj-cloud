package com.mmj.user.recommend.model.vo;

/**
 * 给订单使用
 */
public class UserRecommendOrder {
    private String orderNo;

    private Integer status; // 1: 待评价  2:已评价待分享

    private String goodSku;

    private Integer recommendId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
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
