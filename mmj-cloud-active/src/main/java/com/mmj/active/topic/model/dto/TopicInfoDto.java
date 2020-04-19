package com.mmj.active.topic.model.dto;

import com.mmj.active.topic.model.TopicInfo;

import java.util.List;

public class TopicInfoDto extends TopicInfo {

    public String goodIds;

    public String orderType;
    public String filterRule;
    public String orderBy;
    public String goodClass;
    public ActiveSortDto activeSort;

    public List<Coupons> coupons;

    public static class Coupons{
        private Integer couponId;
        private String couponName;

        public Integer getCouponId() {
            return couponId;
        }

        public void setCouponId(Integer couponId) {
            this.couponId = couponId;
        }

        public String getCouponName() {
            return couponName;
        }

        public void setCouponName(String couponName) {
            this.couponName = couponName;
        }

        public Coupons(){

        }
        public Coupons(Integer couponId, String couponName) {
            this.couponId = couponId;
            this.couponName = couponName;
        }
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getFilterRule() {
        return filterRule;
    }

    public void setFilterRule(String filterRule) {
        this.filterRule = filterRule;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(String goodIds) {
        this.goodIds = goodIds;
    }

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
    }

    public ActiveSortDto getActiveSort() {
        return activeSort;
    }

    public void setActiveSort(ActiveSortDto activeSort) {
        this.activeSort = activeSort;
    }

    public List<Coupons> getCoupons() {
        return coupons;
    }

    public void setCoupons(List<Coupons> coupons) {
        this.coupons = coupons;
    }

}
