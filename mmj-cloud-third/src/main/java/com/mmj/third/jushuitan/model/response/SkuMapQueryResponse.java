package com.mmj.third.jushuitan.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 商品映射查询
 * @auther: KK
 * @date: 2019/8/13
 */
@Data
public class SkuMapQueryResponse extends JushuitanResponse {
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
        /**
         * 店铺id
         */
        @JsonProperty("shop_id")
        @JSONField(name = "shop_id")
        private Integer shopId;

        /**
         * 平台
         */
        private String channel;

        /**
         * 款号id
         */
        @JsonProperty("i_id")
        @JSONField(name = "i_id")
        private String iId;

        /**
         * 商品id
         */
        @JsonProperty("sku_id")
        @JSONField(name = "sku_id")
        private String skuId;

        /**
         * 店铺款号id
         */
        @JsonProperty("shop_i_id")
        @JSONField(name = "shop_i_id")
        private String shopIId;

        /**
         * 店铺商品id
         */
        @JsonProperty("shop_sku_id")
        @JSONField(name = "shop_sku_id")
        private String shopSkuId;

        /**
         * 修改时间
         */
        private String modified;

        /**
         * 是否在售
         */
        private Boolean enabled;
    }
}
