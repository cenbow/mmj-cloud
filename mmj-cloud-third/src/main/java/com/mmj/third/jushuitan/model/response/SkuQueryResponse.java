package com.mmj.third.jushuitan.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 普通商品查询返回
 * @auther: KK
 * @date: 2019/8/13
 */
@Data
public class SkuQueryResponse extends JushuitanResponse {
    @JsonProperty("page_size")
    @JSONField(name = "page_size")
    private Integer pageSize;
    @JsonProperty("page_index")
    @JSONField(name = "page_index")
    private Integer pageIndex;
    @JsonProperty("has_next")
    @JSONField(name = "has_next")
    private Boolean hasNext;
    private List<Data> datas;

    @lombok.Data
    public static class Data {
        @JsonProperty("sku_id")
        @JSONField(name = "sku_id")
        private String skuId;
        @JsonProperty("i_id")
        @JSONField(name = "i_id")
        private String iId;
        private String name;
        @JsonProperty("sale_price")
        @JSONField(name = "sale_price")
        private BigDecimal salePrice;
        @JsonProperty("cost_price")
        @JSONField(name = "cost_price")
        private BigDecimal costPrice;
        @JsonProperty("properties_value")
        @JSONField(name = "properties_value")
        private String propertiesValue;
        private String pic;
        @JsonProperty("sku_code")
        @JSONField(name = "sku_code")
        private String skuCode;
        @JsonProperty("sku_codes")
        @JSONField(name = "sku_codes")
        private String skuCodes;
        private Integer enabled;
        private BigDecimal weight;
        @JsonProperty("market_price")
        @JSONField(name = "market_price")
        private BigDecimal marketPrice;
        private String modified;
        @JsonProperty("short_name")
        @JSONField(name = "short_name")
        private String shortName;
        @JsonProperty("supplier_id")
        @JSONField(name = "supplier_id")
        private String supplierId;
        @JsonProperty("supplier_name")
        @JSONField(name = "supplier_name")
        private String supplierName;
        @JsonProperty("supplier_sku_id")
        @JSONField(name = "supplier_sku_id")
        private String supplierSkuId;
        @JsonProperty("supplier_i_id")
        @JSONField(name = "supplier_i_id")
        private String supplierIId;
        private String remark;
        private String created;
        private String category;
        @JsonProperty("vc_name")
        @JSONField(name = "vc_name")
        private String vcName;
    }
}
