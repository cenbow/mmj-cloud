package com.mmj.good.model;

import java.util.List;

public class GoodSaleEx extends GoodSale {

    private List<Integer> goodIds; //商品id - 查询条件

    private List<Integer> saleIds; //销售id - 查询条件

    private List<String> goodSkus; //sku - 查询条件

    private Integer warehouseNum; //库存数据

    private String wareHouseShow; //商品仓库展示名称，多个已逗号分隔,数据来源从goodWarehouses获取

    private String goodStatus; //商品状态 -1：删除 0：暂不发布 1：立即上架 2：自动上架 3：上架失败

    private List<GoodModelEx> goodModelExes;

    private List<GoodWarehouse> goodWarehouses;//sku仓库

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

    public Integer getWarehouseNum() {
        return warehouseNum;
    }

    public void setWarehouseNum(Integer warehouseNum) {
        this.warehouseNum = warehouseNum;
    }

    public String getWareHouseShow() {
        return wareHouseShow;
    }

    public void setWareHouseShow(String wareHouseShow) {
        this.wareHouseShow = wareHouseShow;
    }

    public String getGoodStatus() {
        return goodStatus;
    }

    public void setGoodStatus(String goodStatus) {
        this.goodStatus = goodStatus;
    }

    public List<GoodModelEx> getGoodModelExes() {
        return goodModelExes;
    }

    public void setGoodModelExes(List<GoodModelEx> goodModelExes) {
        this.goodModelExes = goodModelExes;
    }

    public List<GoodWarehouse> getGoodWarehouses() {
        return goodWarehouses;
    }

    public void setGoodWarehouses(List<GoodWarehouse> goodWarehouses) {
        this.goodWarehouses = goodWarehouses;
    }
}
