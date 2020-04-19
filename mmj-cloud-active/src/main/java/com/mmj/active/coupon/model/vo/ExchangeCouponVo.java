package com.mmj.active.coupon.model.vo;

import javax.validation.constraints.NotNull;

public class ExchangeCouponVo {

    @NotNull
    private String openId;

    private Long userid;

    private String unionid;

    @NotNull
    private String redeemCode;

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public String getRedeemCode() {
        return redeemCode;
    }

    public void setRedeemCode(String redeemCode) {
        this.redeemCode = redeemCode;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
