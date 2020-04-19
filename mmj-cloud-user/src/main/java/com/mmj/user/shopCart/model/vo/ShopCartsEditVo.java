package com.mmj.user.shopCart.model.vo;

import javax.validation.constraints.NotNull;

/**
 * @Description: 编辑购物车信息
 * @Auther: zhangyicao
 * @Date: 2019-06-03
 */
public class ShopCartsEditVo  {
    /**
     * 商品规格id
     */
    @NotNull
    private String goodSkuId;

    /**
     * 商品类型
     */
    private String goodType;

    /**
     * 新商品规格id
     */
    private String newGoodSkuId;

    /**
     * 新商品图片
     */
    private String newGoodImage;

    /**
     * 新商品规格数据
     */
    private String newGoodSkuData;

    /**
     * 新购买数量
     */
    @NotNull
    private Integer newGoodNum;

    /**
     * 新单价
     */
    private String newUnitPrice;

    /**
     * 新原价
     */
    private String newOriginalPrice;

    /**
     * 新会员价
     */
    private String newMemberPrice;

    /**
     * 是否会员专属商品
     */
    private Boolean memberFlag;

    public String getGoodSkuId() {
        return goodSkuId;
    }

    public void setGoodSkuId(String goodSkuId) {
        this.goodSkuId = goodSkuId;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public String getNewGoodSkuId() {
        return newGoodSkuId;
    }

    public void setNewGoodSkuId(String newGoodSkuId) {
        this.newGoodSkuId = newGoodSkuId;
    }

    public String getNewGoodImage() {
        return newGoodImage;
    }

    public void setNewGoodImage(String newGoodImage) {
        this.newGoodImage = newGoodImage;
    }

    public String getNewGoodSkuData() {
        return newGoodSkuData;
    }

    public void setNewGoodSkuData(String newGoodSkuData) {
        this.newGoodSkuData = newGoodSkuData;
    }

    public Integer getNewGoodNum() {
        return newGoodNum;
    }

    public void setNewGoodNum(Integer newGoodNum) {
        this.newGoodNum = newGoodNum;
    }

    public String getNewUnitPrice() {
        return newUnitPrice;
    }

    public void setNewUnitPrice(String newUnitPrice) {
        this.newUnitPrice = newUnitPrice;
    }

    public String getNewOriginalPrice() {
        return newOriginalPrice;
    }

    public void setNewOriginalPrice(String newOriginalPrice) {
        this.newOriginalPrice = newOriginalPrice;
    }

    public String getNewMemberPrice() {
        return newMemberPrice;
    }

    public void setNewMemberPrice(String newMemberPrice) {
        this.newMemberPrice = newMemberPrice;
    }

    public Boolean getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Boolean memberFlag) {
        this.memberFlag = memberFlag;
    }
}
