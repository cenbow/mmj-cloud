package com.mmj.order.model.dto;

public class GoodsStockDto {
    private String sku;
    private int stocknum;

    public GoodsStockDto(String sku, int stocknum) {
        this.sku = sku;
        this.stocknum = stocknum;
    }

    public GoodsStockDto() {
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getStocknum() {
        return stocknum;
    }

    public void setStocknum(int stocknum) {
        this.stocknum = stocknum;
    }
}
