package com.mmj.good.model;

import java.util.List;

public class GoodNum {
    private Integer goodId;

    private Integer goodNumTotal;

    private Integer saleNumTotal;

    private List<SkuNum> skuNums;

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }

    public Integer getGoodNumTotal() {
        return goodNumTotal;
    }

    public void setGoodNumTotal(Integer goodNumTotal) {
        this.goodNumTotal = goodNumTotal;
    }

    public Integer getSaleNumTotal() {
        return saleNumTotal;
    }

    public void setSaleNumTotal(Integer saleNumTotal) {
        this.saleNumTotal = saleNumTotal;
    }

    public List<SkuNum> getSkuNums() {
        return skuNums;
    }

    public void setSkuNums(List<SkuNum> skuNums) {
        this.skuNums = skuNums;
    }
}
