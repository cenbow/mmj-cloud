package com.mmj.third.jushuitan.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @description: 普通商品查询
 * @auther: KK
 * @date: 2019/8/13
 */
@Data
public class SkuQueryRequest {
    /**
     * 第几页，从1 开始
     */
    @JsonProperty("page_index")
    @JSONField(name = "page_index")
    private Integer pageIndex = 1;
    /**
     * 默认 30，最大不超过 50
     */
    @JsonProperty("page_size")
    @JSONField(name = "page_size")
    private Integer pageSize = 50;
    /**
     * 商品编码,多个用逗号分隔，最多50
     */
    @JsonProperty("sku_ids")
    @JSONField(name = "sku_ids")
    private String skuIds;
    /**
     * 修改起始时间，和结束时间必须同时存在，时间间隔不能超过七天
     */
    @JsonProperty("modified_begin")
    @JSONField(name = "modified_begin")
    private String modifiedBegin;
    /**
     * 修改起始时间，和结束时间必须同时存在，时间间隔不能超过七天
     */
    @JsonProperty("modified_end")
    @JSONField(name = "modified_end")
    private String modifiedEnd;
}
