package com.mmj.good.model;

import java.util.List;

public class GoodOrder extends GoodSale {
    private String image;

    private GoodInfo goodInfo;

    private List<GoodModel> goodModels;

    private List<GoodWarehouse> goodWarehouses;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public GoodInfo getGoodInfo() {
        return goodInfo;
    }

    public void setGoodInfo(GoodInfo goodInfo) {
        this.goodInfo = goodInfo;
    }

    public List<GoodModel> getGoodModels() {
        return goodModels;
    }

    public void setGoodModels(List<GoodModel> goodModels) {
        this.goodModels = goodModels;
    }

    public List<GoodWarehouse> getGoodWarehouses() {
        return goodWarehouses;
    }

    public void setGoodWarehouses(List<GoodWarehouse> goodWarehouses) {
        this.goodWarehouses = goodWarehouses;
    }
}
