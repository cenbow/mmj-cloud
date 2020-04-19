package com.mmj.order.common.model;

import com.mmj.common.model.BaseModel;

public class ActiveGood extends BaseModel {

    private static final long serialVersionUID = 1545691354670254085L;
    private Long mapperyId;

    // 活动类型  1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖 11 分类商品  12 拼团  13 免费送  14 转盘商品 15 虚拟商品 16 店铺商品
    private Integer activeType;

    private Integer businessId;

    private Integer goodId;

    private String sellingPoint;

    private Integer basePrice;

    private Integer activePrice;

    private Integer memberPrice;

    private Integer saleId;

    private String goodName;

    private String shortName;

    private String goodClass;

    private String modelName;

    private Integer modelId;

    private String modelValue;

    private String goodImage;

    private Integer goodOrder;

    private String goodSku;

    private Integer activeStore;

    private Integer activeVirtual;

    private String goodSpu;

    private Integer goodLimit;

    private Integer limitType;

    private Integer limitNum;

    private Integer groupPerson;

    private Integer memberFlag;

    private Integer virtualFlag;

    private Integer combinaFlag;

    private String goodStatus;

    private Integer kingNum;

    private String arg1;

    private String arg2;

    private String arg3;

    private String arg4;

    private String arg5;

    private Double baseAmount;

    private Double activeAmount;

    private Double memberAmount;

    private Integer saleNum;

    public Long getMapperyId() {
        return mapperyId;
    }

    public void setMapperyId(Long mapperyId) {
        this.mapperyId = mapperyId;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
        this.baseAmount = basePrice == null ? null : Double.valueOf(basePrice) / 100.00;
    }

    public Integer getActivePrice() {
        return activePrice;
    }

    public void setActivePrice(Integer activePrice) {
        this.activePrice = activePrice;
        this.activeAmount = activePrice == null ? null : Double.valueOf(activePrice) / 100.00;
    }

    public Integer getKingNum() {
        return kingNum;
    }

    public void setKingNum(Integer kingNum) {
        this.kingNum = kingNum;
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;
        this.memberAmount = memberPrice == null ? null : Double.valueOf(memberPrice) / 100.00;
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

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getModelValue() {
        return modelValue;
    }

    public void setModelValue(String modelValue) {
        this.modelValue = modelValue;
    }

    public String getGoodImage() {
        return goodImage;
    }

    public void setGoodImage(String goodImage) {
        this.goodImage = goodImage;
    }

    public Integer getGoodOrder() {
        return goodOrder;
    }

    public void setGoodOrder(Integer goodOrder) {
        this.goodOrder = goodOrder;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public Integer getActiveStore() {
        return activeStore;
    }

    public void setActiveStore(Integer activeStore) {
        this.activeStore = activeStore;
    }

    public Integer getActiveVirtual() {
        return activeVirtual;
    }

    public void setActiveVirtual(Integer activeVirtual) {
        this.activeVirtual = activeVirtual;
    }

    public String getGoodSpu() {
        return goodSpu;
    }

    public void setGoodSpu(String goodSpu) {
        this.goodSpu = goodSpu;
    }

    public Integer getGoodLimit() {
        return goodLimit;
    }

    public void setGoodLimit(Integer goodLimit) {
        this.goodLimit = goodLimit;
    }

    public Integer getLimitType() {
        return limitType;
    }

    public void setLimitType(Integer limitType) {
        this.limitType = limitType;
    }

    public Integer getLimitNum() {
        return limitNum;
    }

    public void setLimitNum(Integer limitNum) {
        this.limitNum = limitNum;
    }

    public Integer getGroupPerson() {
        return groupPerson;
    }

    public void setGroupPerson(Integer groupPerson) {
        this.groupPerson = groupPerson;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Integer getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Integer memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getCombinaFlag() {
        return combinaFlag;
    }

    public void setCombinaFlag(Integer combinaFlag) {
        this.combinaFlag = combinaFlag;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public String getArg1() {
        return arg1;
    }

    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    public String getArg3() {
        return arg3;
    }

    public void setArg3(String arg3) {
        this.arg3 = arg3;
    }

    public String getArg4() {
        return arg4;
    }

    public void setArg4(String arg4) {
        this.arg4 = arg4;
    }

    public String getArg5() {
        return arg5;
    }

    public void setArg5(String arg5) {
        this.arg5 = arg5;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
        this.basePrice = baseAmount == null ? null : (int)(baseAmount * 100);
    }

    public Double getActiveAmount() {
        return activeAmount;
    }

    public void setActiveAmount(Double activeAmount) {
        this.activeAmount = activeAmount;
        this.activePrice = activeAmount == null ? null : (int)(activeAmount * 100);
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;
        this.memberPrice = memberAmount == null ? null : (int)(memberAmount * 100);
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }
}
