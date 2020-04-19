package com.mmj.good.feigin.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class InventoryQuery {
    /**
     * 分仓公司编号
     */
    @ApiModelProperty("分仓公司编号")
    private Integer wmsCoId;

    /**
     * 商品编码，最多50
     */
    @ApiModelProperty("商品编码，最多50")
    private List<String> skus;

    @ApiModelProperty("款式编码")
    private String spu;

    @ApiModelProperty("商品编码")
    private String sku;

    @ApiModelProperty("库存数(主仓实际库存+虚拟库存-订单占有数)")
    private Integer stockNum;

    public Integer getWmsCoId() {
        return wmsCoId;
    }

    public void setWmsCoId(Integer wmsCoId) {
        this.wmsCoId = wmsCoId;
    }

    public List<String> getSkus() {
        return skus;
    }

    public void setSkus(List<String> skus) {
        this.skus = skus;
    }

    public String getSpu() {
        return spu;
    }

    public void setSpu(String spu) {
        this.spu = spu;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStockNum() {
        return stockNum;
    }

    public void setStockNum(Integer stockNum) {
        this.stockNum = stockNum;
    }
}
