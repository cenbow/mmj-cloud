package com.mmj.active.homeManagement.model;

public class WebAlertEX extends WebAlert {
    private Integer couponStatus;

    private String couponDesc;

    public Integer getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(Integer couponStatus) {
        this.couponStatus = couponStatus;
    }

    public String getCouponDesc() {
        return couponDesc;
    }

    public void setCouponDesc(String couponDesc) {
        this.couponDesc = couponDesc;
    }
}
