package com.mmj.third.jushuitan.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 物流同步
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
@ApiModel("物流同步")
public class LogisticsUploadRequest {
    /**
     * ERP内部订单号，唯一
     */
    @JsonProperty("o_id")
    @JSONField(name = "o_id")
    @ApiModelProperty("ERP内部订单号，唯一")
    private Long oId;
    /**
     * 订单号，最长不超过 50;平台唯一
     */
    @JsonProperty("so_id")
    @JSONField(name = "so_id")
    @ApiModelProperty("订单号，最长不超过 50;平台唯一")
    private String soId;
    /**
     * 发货日期
     */
    @JsonProperty("send_date")
    @JSONField(name = "send_date")
    @ApiModelProperty("发货日期")
    private String sendDate;
    /**
     * 快递单号
     */
    @JsonProperty("l_id")
    @JSONField(name = "l_id")
    @ApiModelProperty("快递单号")
    private String lId;

    /**
     * 物流公司编码
     */
    @JsonProperty("lc_id")
    @JSONField(name = "lc_id")
    @ApiModelProperty("物流公司编码")
    private String lcId;
    /**
     * 快递公司
     */
    @JsonProperty("logistics_company")
    @JSONField(name = "logistics_company")
    @ApiModelProperty("快递公司")
    private String logisticsCompany;
    /**
     * 商品列表
     */
    @ApiModelProperty("商品列表")
    private List<Item> items;

    @Data
    @ApiModel("物流同步-子信息")
    public static class Item {
        /**
         * 商品编码
         */
        @JsonProperty("sku_id")
        @JSONField(name = "sku_id")
        @ApiModelProperty("商品编码")
        private String skuId;
        /**
         * 数量
         */
        @ApiModelProperty("数量")
        private Integer qty;
        /**
         * 订单号，最长不超过 50;平台唯一;合并订单发货的时候，可以和上层结构不同,标识原始线上单号，为空则和上层一致
         */
        @JsonProperty("so_id")
        @JSONField(name = "so_id")
        @ApiModelProperty("订单号，最长不超过 50;平台唯一;合并订单发货的时候，可以和上层结构不同,标识原始线上单号，为空则和上层一致")
        private String soId;
    }
}
