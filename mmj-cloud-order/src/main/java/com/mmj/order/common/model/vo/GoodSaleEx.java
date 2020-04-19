package com.mmj.order.common.model.vo;

import java.util.List;

public class GoodSaleEx extends GoodSale {

    private List<Integer> goodIds; //商品id - 查询条件

    private List<Integer> saleIds; //销售id - 查询条件

    private List<String> goodSkus; //sku - 查询条件

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public List<Integer> getSaleIds() {
        return saleIds;
    }

    public void setSaleIds(List<Integer> saleIds) {
        this.saleIds = saleIds;
    }

    public List<String> getGoodSkus() {
        return goodSkus;
    }

    public void setGoodSkus(List<String> goodSkus) {
        this.goodSkus = goodSkus;
    }
}
