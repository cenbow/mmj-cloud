package com.mmj.user.shopCart.model.dto;

import javax.validation.constraints.NotNull;

/**
 * @Description: 购物车实体
 * @Auther: zhangyicao
 * @Date: 2019/06/11
 */
public class ShopCartsDto {
    /**
     * 商品id
     */
    private Integer goodId;

    /**
     * 商品规格id
     */
    private String goodSku;

    /**
     *
     */
    private Integer saleId;

    /**
     * 商品标题
     */
    private String goodName;

    /**
     * 商品图片
     */
    private String goodImages;

    /**
     * 商品规格数据
     */
    private String modelName;

    /**
     * 购买数量
     */
    private Integer goodNum;

    /**
     * 商品类型
     */
    private String goodType;

    /**
     * 单价
     */
    private String goodPrice;

    /**
     * 原价
     */
    private String basePrice;

    /**
     * 库存数
     */
    private Integer stockNum = 0;

    /**
     * 是否选中 0否 1是
     */
    private Boolean selectFlag;

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
     * 是否会员专属
     */
    @NotNull
    private Boolean combinaFlag;

    public ShopCartsDto(Integer goodId,String goodSku){
        this.goodId = goodId;
        this.goodSku = goodSku;
    }

    public ShopCartsDto(Integer goodId, String goodSku, Integer saleId, String goodName, String goodImages, String modelName, Integer goodNum, String goodType, String goodPrice, String basePrice, Integer stockNum,
                        String memberPrice,Boolean memberFlag,Boolean selectFlag) {
        this.goodId = goodId;
        this.goodSku = goodSku;
        this.saleId = saleId;
        this.goodName = goodName;
        this.goodImages = goodImages;
        this.modelName = modelName;
        this.goodNum = goodNum;
        this.goodType = goodType;
        this.goodPrice = goodPrice;
        this.basePrice = basePrice;
        this.stockNum = stockNum;
        this.memberPrice = memberPrice;
        this.memberFlag = memberFlag;
        this.selectFlag = selectFlag;
    }

    public ShopCartsDto(){

    }

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

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public String getGoodType() {
        return goodType;
    }

    public void setGoodType(String goodType) {
        this.goodType = goodType;
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

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
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
}
