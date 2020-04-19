package com.mmj.third.jushuitan.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 库存查询返回
 * @auther: KK
 * @date: 2019/6/5
 */
@Data
public class InventoryQueryResponse extends JushuitanResponse {
    @JsonProperty("page_size")
    @JSONField(name = "page_size")
    private Integer pageSize;
    @JsonProperty("page_index")
    @JSONField(name = "page_index")
    private Integer pageIndex;
    @JsonProperty("has_next")
    @JSONField(name = "has_next")
    private Boolean hasNext;
    private List<Inventory> inventorys;

    @Data
    public static class Inventory{
        @JsonProperty("sku_id")
        @JSONField(name = "sku_id")
        private String skuId;
        @JsonProperty("i_id")
        @JSONField(name = "i_id")
        private String iId;
        private Integer qty;
        @JsonProperty("order_lock")
        @JSONField(name = "order_lock")
        private Integer orderLock;
        @JsonProperty("virtual_qty")
        @JSONField(name = "virtual_qty")
        private Integer virtualQty;
        @JsonProperty("purchase_qty")
        @JSONField(name = "purchase_qty")
        private Integer purchaseQty;
        private String modified;
    }
}
