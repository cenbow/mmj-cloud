package com.mmj.good.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

public class ExportGoodsProperty extends BaseRowModel {

    @ExcelProperty("商品ID")
    private Integer goodId;
    @ExcelProperty("分类")
    private String goodClass;
    @ExcelProperty("商品名称")
    private String goodName;
    @ExcelProperty("商品卖点")
    private String sellingPoint;
    @ExcelProperty("商品简称")
    private String shortName;
    @ExcelProperty("款式编码")
    private String goodSpu;
    @ExcelProperty("是否自动展示(1是,0否)")
    private Integer autoShow;
    @ExcelProperty("是否虚拟商品(1是,0否)")
    private Integer virtualFlag;
    @ExcelProperty("是否会员专享(1是,0否)")
    private Integer memberFlag;
    @ExcelProperty("是否组合商品(1是,0否)")
    private Integer combinaFlag;
    @ExcelProperty("上架时间")
    private String upTime;
    @ExcelProperty("在售时间(小时)")
    private Integer saleDays;
    @ExcelProperty("状态(-1：删除 0：暂不发布 1：立即上架)")
    private String goodStatus;

    @ExcelProperty("商品编码")
    private String goodSku;
    @ExcelProperty("规格名")
    private String modelName;
    @ExcelProperty("规则值")
    private String modelValue;
    @ExcelProperty("普通价")
    private Integer basePrice;
    @ExcelProperty("店铺价")
    private Integer shopPrice;
    @ExcelProperty("会员价")
    private Integer memberPrice;
    @ExcelProperty("库存")
    private Integer goodNum;
    @ExcelProperty("销量")
    private Integer saleNum;
    @ExcelProperty("仓库")

    private String warehouseName;
    @ExcelProperty("商品图片")
    private String image;
    @ExcelProperty("卖点图")
    private String sellingPointImage;
    @ExcelProperty("分享图")
    private String wechat;
    @ExcelProperty("分享图H5")
    private String h5;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public String getGoodClass() {
        return goodClass;
    }

    public void setGoodClass(String goodClass) {
        this.goodClass = goodClass;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getSellingPoint() {
        return sellingPoint;
    }

    public void setSellingPoint(String sellingPoint) {
        this.sellingPoint = sellingPoint;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getGoodSpu() {
        return goodSpu;
    }

    public void setGoodSpu(String goodSpu) {
        this.goodSpu = goodSpu;
    }

    public Integer getAutoShow() {
        return autoShow;
    }

    public void setAutoShow(Integer autoShow) {
        this.autoShow = autoShow;
    }

    public Integer getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(Integer virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public Integer getMemberFlag() {
        return memberFlag;
    }

    public void setMemberFlag(Integer memberFlag) {
        this.memberFlag = memberFlag;
    }

    public Integer getCombinaFlag() {
        return combinaFlag;
    }

    public void setCombinaFlag(Integer combinaFlag) {
        this.combinaFlag = combinaFlag;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public Integer getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(Integer saleDays) {
        this.saleDays = saleDays;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public String getGoodSku() {
        return goodSku;
    }

    public void setGoodSku(String goodSku) {
        this.goodSku = goodSku;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelValue() {
        return modelValue;
    }

    public void setModelValue(String modelValue) {
        this.modelValue = modelValue;
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

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public Integer getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(Integer saleNum) {
        this.saleNum = saleNum;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSellingPointImage() {
        return sellingPointImage;
    }

    public void setSellingPointImage(String sellingPointImage) {
        this.sellingPointImage = sellingPointImage;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getH5() {
        return h5;
    }

    public void setH5(String h5) {
        this.h5 = h5;
    }
}
