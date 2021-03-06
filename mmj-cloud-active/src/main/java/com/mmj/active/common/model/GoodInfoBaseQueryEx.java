package com.mmj.active.common.model;

import java.util.List;

public class GoodInfoBaseQueryEx extends GoodInfo{

    private int goodNum; //商品库存

    private String className; //商品分类名称

    private int saleNum; //商品销量

    private String label; //标签

    private String image; //商品图片

    private List<GoodSaleEx> goodSaleExes; //商品sku信息

    private List<Integer> goodIds; //商品id - 查询条件

    private List<String> classCodes; //商品分类 - 查询条件

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<GoodSaleEx> getGoodSaleExes() {
        return goodSaleExes;
    }

    public void setGoodSaleExes(List<GoodSaleEx> goodSaleExes) {
        this.goodSaleExes = goodSaleExes;
    }

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public List<String> getClassCodes() {
        return classCodes;
    }

    public void setClassCodes(List<String> classCodes) {
        this.classCodes = classCodes;
    }
}
