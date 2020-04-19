package com.mmj.user.shopCart.model.vo;

import javax.validation.constraints.NotNull;

/**
 * @Description: 新增购物车商品
 * @Auther: zhangyicao
 * @Date: 2019-06-03
 */
public class ShopCartsAddVo {
    /**
     * 商品id
     */
    @NotNull
    private Integer goodId;
    /**
     * 商品规格
     */
    @NotNull
    private String goodSku;

    /**
     * 销售id
     */
    @NotNull
    private Integer saleId;

    /**
     * 商品标题
     */
    @NotNull
    private String goodName;

    /**
     * 商品图片
     */
    @NotNull
    private String goodImages;

    /**
     * 商品规格数据
     */
    @NotNull
    private String modelName;

    /**
     * 商品类型
     */
    @NotNull
    private String goodType;

    /**
     * 购买数量
     */
    @NotNull
    private Integer goodNum;

    /**
     * 单价
     */
    @NotNull
    private String goodPrice;

    /**
     * 原价
     */
    @NotNull
    private String basePrice;

    /**
     * 会员价
     */
    @NotNull
    private String memberPrice;

    /**
     * 是否会员专属
     */
    @NotNull
    private Boolean memberFlag;

    /**
     * 是否组合商品
     */
    @NotNull
    private Boolean combinaFlag;

    /**
     * 是否虚拟商品
     */
    @NotNull
    private Boolean virtualFlag;

    @NotNull
    private Boolean selectFlag;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setSaleId(Integer saleId) {
        this.saleId = saleId;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getGoodImages() {
        return goodImages;
    }

    public void setGoodImages(String goodImages) {
        this.goodImages = goodImages;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public String getGoodPrice() {
        return goodPrice;
    }

    public void setGoodPrice(String goodPrice) {
        this.goodPrice = goodPrice;
    }

    public String getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(String basePrice) {
        this.basePrice = basePrice;
    }

    public Boolean getSelectFlag() {
        return selectFlag;
    }

    public void setSelectFlag(Boolean selectFlag) {
        this.selectFlag = selectFlag;
    }

    public String getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(String memberPrice) {
        this.memberPrice = memberPrice;
    }

    public Boolean getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Boolean memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Boolean getCombinaFlag() {
        return combinaFlag;
    }

    public void setCombinaFlag(Boolean combinaFlag) {
        this.combinaFlag = combinaFlag;
    }

    public Boolean getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Boolean virtualFlag) {
        this.virtualFlag = virtualFlag;
    }
}
