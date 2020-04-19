package com.mmj.active.common.model;

import java.util.List;

public class GoodInfoEx extends GoodInfo {
    private List<Integer> goodIds; //商品id - 查询条件

    private List<Integer> noGoodIds;  //例外商品id

    private List<Integer> topGoodIds;   //置顶商品id

    private String wareHouseShow; //商品仓库展示名称，多个已逗号分隔

    private int goodNum; //商品库存

    private String className; //商品分类名称

    private int saleNum; //商品销量

    private String image; //商品图片

    private Integer basePrice; //普通单价

    private Integer shopPrice; //店铺价格

    private Integer tuanPrice; //拼团价

    private Integer memberPrice;//会员价

    private Double baseAmount;  //普通单价

    private Double shopAmount;  //店铺价格

    private Double tuanAmount;  //拼团价

    private Double memberAmount;    //会员价

    private Integer warehouseNum; //库存数据

    private String classCode; //分类编码

    private Integer showFlag; //是否显示-分类

    private String orderType; //排序sql

    private String fileServer;

    private Integer activeType;//活动类型

    private Integer businessId;//活动ID

    private String labelName;//标签名称

    private List<String> classCodes;

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public List<Integer> getNoGoodIds() {
        return noGoodIds;
    }

    public void setNoGoodIds(List<Integer> noGoodIds) {
        this.noGoodIds = noGoodIds;
    }

    public List<Integer> getTopGoodIds() {
        return topGoodIds;
    }

    public void setTopGoodIds(List<Integer> topGoodIds) {
        this.topGoodIds = topGoodIds;
    }

    public String getWareHouseShow() {
        return wareHouseShow;
    }

    public void setWareHouseShow(String wareHouseShow) {
        this.wareHouseShow = wareHouseShow;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;
    }

    public Integer getTuanPrice() {
        return tuanPrice;
    }

    public void setTuanPrice(Integer tuanPrice) {
        this.tuanPrice = tuanPrice;
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public Double getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(Double shopAmount) {
        this.shopAmount = shopAmount;
    }

    public Double getTuanAmount() {
        return tuanAmount;
    }

    public void setTuanAmount(Double tuanAmount) {
        this.tuanAmount = tuanAmount;
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;
    }

    public Integer getWarehouseNum() {
        return warehouseNum;
    }

    public void setWarehouseNum(Integer warehouseNum) {
        this.warehouseNum = warehouseNum;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
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

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public List<String> getClassCodes() {
        return classCodes;
    }

    public void setClassCodes(List<String> classCodes) {
        this.classCodes = classCodes;
    }
}
