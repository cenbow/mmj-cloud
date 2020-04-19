package com.mmj.active.relayLottery.model.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @Description: 订单商品
 * @Auther: KK
 * @Date: 2018/11/24
 */
public class OrderGoodsDto {
    private Integer goodsId;
    private String goodsSkuId;
    private String goodsTitle;
    private String goodsImage;
    private String goodsSkuData;
    private Integer goodsNum;
    private String unitPrice;
    private String originalPrice;
    @JsonProperty("memberprice")
    @JSONField(name = "memberprice")
    private String memberPrice;
    private String couponUnitPrice;
    private String couponPrice;
    private String goodsAmount;
    private String goodsshortname;
    private boolean virtualGoodsFlag;
    //是否有推荐 0默认 1待评价 2已评价待分享
    private int hasRecommend = 0;

    public OrderGoodsDto(Integer goodsId, String goodsSkuId, String goodsTitle, String goodsImage, String goodsSkuData, Integer goodsNum, String unitPrice, String memberPrice, String originalPrice, String couponUnitPrice, String couponPrice, String goodsAmount, boolean virtualGoodsFlag, int hasRecommend) {
        this.goodsId = goodsId;
        this.goodsSkuId = goodsSkuId;
        this.goodsTitle = goodsTitle;
        this.goodsImage = goodsImage;
        this.goodsSkuData = goodsSkuData;
        this.goodsNum = goodsNum;
        this.unitPrice = unitPrice;
        this.memberPrice = memberPrice;
        this.originalPrice = originalPrice;
        this.couponUnitPrice = couponUnitPrice;
        this.couponPrice = couponPrice;
        this.goodsAmount = goodsAmount;
        this.virtualGoodsFlag = virtualGoodsFlag;
        this.hasRecommend = hasRecommend;
    }

    public OrderGoodsDto(Integer goodsId, String goodsSkuId, String goodsTitle, String goodsImage, String goodsSkuData, Integer goodsNum, String unitPrice, String memberPrice, String originalPrice, String couponUnitPrice, String couponPrice, String goodsAmount, boolean virtualGoodsFlag) {
        this(goodsId, goodsSkuId, goodsTitle, goodsImage, goodsSkuData, goodsNum, unitPrice, memberPrice, originalPrice, couponUnitPrice, couponPrice, goodsAmount, virtualGoodsFlag, 0);
    }

    public OrderGoodsDto(Integer goodsId, String goodsSkuId, String goodsTitle, String goodsImage, String goodsSkuData, Integer goodsNum, String unitPrice, String memberPrice, String originalPrice, String couponUnitPrice, String couponPrice, String goodsAmount) {
        this(goodsId, goodsSkuId, goodsTitle, goodsImage, goodsSkuData, goodsNum, unitPrice, memberPrice, originalPrice, couponUnitPrice, couponPrice, goodsAmount, false, 0);
    }


    public boolean isVirtualGoodsFlag() {
        return virtualGoodsFlag;
    }

    public void setVirtualGoodsFlag(boolean virtualGoodsFlag) {
        this.virtualGoodsFlag = virtualGoodsFlag;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsSkuId() {
        return goodsSkuId;
    }

    public void setGoodsSkuId(String goodsSkuId) {
        this.goodsSkuId = goodsSkuId;
    }

    public String getGoodsTitle() {
        return goodsTitle;
    }

    public void setGoodsTitle(String goodsTitle) {
        this.goodsTitle = goodsTitle;
    }

    public String getGoodsImage() {
        return goodsImage;
    }

    public void setGoodsImage(String goodsImage) {
        this.goodsImage = goodsImage;
    }

    public String getGoodsSkuData() {
        return goodsSkuData;
    }

    public void setGoodsSkuData(String goodsSkuData) {
        this.goodsSkuData = goodsSkuData;
    }

    public Integer getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCouponUnitPrice() {
        return couponUnitPrice;
    }

    public void setCouponUnitPrice(String couponUnitPrice) {
        this.couponUnitPrice = couponUnitPrice;
    }

    public String getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(String couponPrice) {
        this.couponPrice = couponPrice;
    }

    public String getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(String goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public String getGoodsshortname() {
        return goodsshortname;
    }

    public void setGoodsshortname(String goodsshortname) {
        this.goodsshortname = goodsshortname;
    }

    public String getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        this.memberPrice = memberPrice;
    }

    public int getHasRecommend() {
        return hasRecommend;
    }

    public void setHasRecommend(int hasRecommend) {
        this.hasRecommend = hasRecommend;
    }
}
