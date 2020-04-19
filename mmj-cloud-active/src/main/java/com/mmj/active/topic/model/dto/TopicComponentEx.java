package com.mmj.active.topic.model.dto;

import com.mmj.active.topic.model.TopicComponent;

public class TopicComponentEx extends TopicComponent {

    private String couponName1;  //优惠券一对应的名称

    private double couponMoney1;  //优惠金额

    private int  couponType1;  //优惠类型 1:减少的具体金额;2:折扣

    private String businessRemark1; //运营备注

    private int status1; //领取状态 1:未领取;2:已领取;3:已领完

    private String couponName2;  //优惠券二对应的名称

    private double couponMoney2;  //优惠金额

    private int  couponType2;  //优惠类型 1:减少的具体金额;2:折扣

    private String businessRemark2; //运营备注

    private int status2; //领取状态 1:未领取;2:已领取;3:已领完

    private String couponName3;  //优惠券三对应的名称

    private double couponMoney3;  //优惠金额

    private int  couponType3;  //优惠类型 1:减少的具体金额;2:折扣

    private String businessRemark3; //运营备注

    private int status3; //领取状态 1:未领取;2:已领取;3:已领完

    private Integer goodsbaseid1; //跳转1的商品id

    private int stocknum1;//跳转1商品的库存

    private boolean shelves1; //跳转1商品是否是上架状态

    private Integer goodsbaseid2; //跳转2的商品id

    private int stocknum2;//跳转2商品的库存

    private boolean shelves2; //跳转2商品是否是上架状态

    private Integer goodsbaseid3; //跳转3的商品id

    private int stocknum3;//跳转3商品的库存

    private boolean shelves3; //跳转3商品是否是上架状态

    public String getCouponName1() {
        return couponName1;
    }

    public void setCouponName1(String couponName1) {
        this.couponName1 = couponName1;
    }

    public double getCouponMoney1() {
        return couponMoney1;
    }

    public void setCouponMoney1(double couponMoney1) {
        this.couponMoney1 = couponMoney1;
    }

    public int getCouponType1() {
        return couponType1;
    }

    public void setCouponType1(int couponType1) {
        this.couponType1 = couponType1;
    }

    public String getBusinessRemark1() {
        return businessRemark1;
    }

    public void setBusinessRemark1(String businessRemark1) {
        this.businessRemark1 = businessRemark1;
    }

    public int getStatus1() {
        return status1;
    }

    public void setStatus1(int status1) {
        this.status1 = status1;
    }

    public String getCouponName2() {
        return couponName2;
    }

    public void setCouponName2(String couponName2) {
        this.couponName2 = couponName2;
    }

    public double getCouponMoney2() {
        return couponMoney2;
    }

    public void setCouponMoney2(double couponMoney2) {
        this.couponMoney2 = couponMoney2;
    }

    public int getCouponType2() {
        return couponType2;
    }

    public void setCouponType2(int couponType2) {
        this.couponType2 = couponType2;
    }

    public String getBusinessRemark2() {
        return businessRemark2;
    }

    public void setBusinessRemark2(String businessRemark2) {
        this.businessRemark2 = businessRemark2;
    }

    public int getStatus2() {
        return status2;
    }

    public void setStatus2(int status2) {
        this.status2 = status2;
    }

    public String getCouponName3() {
        return couponName3;
    }

    public void setCouponName3(String couponName3) {
        this.couponName3 = couponName3;
    }

    public double getCouponMoney3() {
        return couponMoney3;
    }

    public void setCouponMoney3(double couponMoney3) {
        this.couponMoney3 = couponMoney3;
    }

    public int getCouponType3() {
        return couponType3;
    }

    public void setCouponType3(int couponType3) {
        this.couponType3 = couponType3;
    }

    public String getBusinessRemark3() {
        return businessRemark3;
    }

    public void setBusinessRemark3(String businessRemark3) {
        this.businessRemark3 = businessRemark3;
    }

    public int getStatus3() {
        return status3;
    }

    public void setStatus3(int status3) {
        this.status3 = status3;
    }

    public Integer getGoodsbaseid1() {
        return goodsbaseid1;
    }

    public void setGoodsbaseid1(Integer goodsbaseid1) {
        this.goodsbaseid1 = goodsbaseid1;
    }

    public int getStocknum1() {
        return stocknum1;
    }

    public void setStocknum1(int stocknum1) {
        this.stocknum1 = stocknum1;
    }

    public boolean isShelves1() {
        return shelves1;
    }

    public void setShelves1(boolean shelves1) {
        this.shelves1 = shelves1;
    }

    public Integer getGoodsbaseid2() {
        return goodsbaseid2;
    }

    public void setGoodsbaseid2(Integer goodsbaseid2) {
        this.goodsbaseid2 = goodsbaseid2;
    }

    public int getStocknum2() {
        return stocknum2;
    }

    public void setStocknum2(int stocknum2) {
        this.stocknum2 = stocknum2;
    }

    public boolean isShelves2() {
        return shelves2;
    }

    public void setShelves2(boolean shelves2) {
        this.shelves2 = shelves2;
    }

    public Integer getGoodsbaseid3() {
        return goodsbaseid3;
    }

    public void setGoodsbaseid3(Integer goodsbaseid3) {
        this.goodsbaseid3 = goodsbaseid3;
    }

    public int getStocknum3() {
        return stocknum3;
    }

    public void setStocknum3(int stocknum3) {
        this.stocknum3 = stocknum3;
    }

    public boolean isShelves3() {
        return shelves3;
    }

    public void setShelves3(boolean shelves3) {
        this.shelves3 = shelves3;
    }
}
