package com.mmj.aftersale.model.dto;

/**
 * 优惠券
 * 2 * @Author: pengwenhao
 * 3 * @Date: 2018/12/25 19:48
 * 4
 */
public class CouponDto {

    private String couponCode;

    private String couponMoney;

    private String couponName;

    public CouponDto() {
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getCouponMoney() {
        return couponMoney;
    }

    public void setCouponMoney(String couponMoney) {
        this.couponMoney = couponMoney;
    }

    public String getCouponName() {
        return couponName;
    }

    public void setCouponName(String couponName) {
        this.couponName = couponName;
    }
}
