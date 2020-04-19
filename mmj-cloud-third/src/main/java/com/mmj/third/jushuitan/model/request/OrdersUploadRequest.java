package com.mmj.third.jushuitan.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @description: 订单上传
 * 接口说明:
 * <p>
 * 此接口一次最大上传50个订单
 * @auther: KK
 * @date: 2019/6/5
 */
@Data
public class OrdersUploadRequest {
    /**
     * 店铺编号
     */
    @JsonProperty("shop_id")
    @JSONField(name = "shop_id")
    private Integer shopId;
    /**
     * 订单号，最长不超过 50;唯一
     */
    @JsonProperty("so_id")
    @JSONField(name = "so_id")
    private String soId;
    /**
     * 下单时间
     */
    @JsonProperty("order_date")
    @JSONField(name = "order_date", format = "yyyy-MM-dd HH:mm:ss")
    private Date orderDate;
    /**
     * 订单状态,可选： WAIT_BUYER_PAY：等待买家付款， WAIT_SELLER_SEND_GOODS:等待卖家发货,WAIT_BUYER_CONFIRM_GOODS:等待买家确认收货, TRADE_FINISHED:交易成功, TRADE_CLOSED:付款后交易关闭,TRADE_CLOSED_BY_TAOBAO:付款前交易关闭,最长40字符，20个汉字
     */
    @JsonProperty("shop_status")
    @JSONField(name = "shop_status")
    private String shopStatus;
    /**
     * 买家昵称，最长50
     */
    @JsonProperty("shop_buyer_id")
    @JSONField(name = "shop_buyer_id")
    private String shopBuyerId;
    /**
     * 收货省份，最长50
     */
    @JsonProperty("receiver_state")
    @JSONField(name = "receiver_state")
    private String receiverState;
    /**
     * 收货市，最长50
     */
    @JsonProperty("receiver_city")
    @JSONField(name = "receiver_city")
    private String receiverCity;
    /**
     * 区，最长 50
     */
    @JsonProperty("receiver_district")
    @JSONField(name = "receiver_district")
    private String receiverDistrict;
    /**
     * 收货地址，最大50
     */
    @JsonProperty("receiver_address")
    @JSONField(name = "receiver_address")
    private String receiverAddress;
    /**
     * 收件人，最大 50
     */
    @JsonProperty("receiver_name")
    @JSONField(name = "receiver_name")
    private String receiverName;
    /**
     * 固定电话，最大30
     */
    @JsonProperty("receiver_phone")
    @JSONField(name = "receiver_phone")
    private String receiverPhone;

    /**
     * 应付金额，保留两位小数，单位元）
     */
    @JsonProperty("pay_amount")
    @JSONField(name = "pay_amount")
    private BigDecimal payAmount;
    /**
     * 订单来源
     */
    @JsonProperty("order_from")
    @JSONField(name = "order_from")
    private String orderFrom;
    /**
     * 运费，保留两位小数，单位（元）
     */
    private BigDecimal freight;

    /**
     * 买家备注
     */
    @JsonProperty("buyer_message")
    @JSONField(name = "buyer_message")
    private String buyerMessage;
    /**
     * 卖家备注
     */
    private String remark;
    @JsonProperty("shop_modified")
    @JSONField(name = "shop_modified", format = "yyyy-MM-dd HH:mm:ss")
    private Date shopModified;
    private List<Item> items;
    private Pay pay;

    /**
     * 订单商品
     */
    @Data
    public static class Item {
        /**
         * 商家SKU，对应库存管理的 SKU，最大 40
         */
        @JsonProperty("sku_id")
        @JSONField(name = "sku_id")
        private String skuId;
        /**
         * 网站对应的自定义SKU编号，最大30
         */
        @JsonProperty("shop_sku_id")
        @JSONField(name = "shop_sku_id")
        private String shopSkuId;

        /**
         * 图片地址
         */
        private String pic;
        /**
         * 属性；最长不超过100 长度
         */
        @JsonProperty("properties_value")
        @JSONField(name = "properties_value")
        private String propertiesValue;
        /**
         * 应付金额，保留两位小数，单位（元）；备注：可能存在人工改价
         */
        private BigDecimal amount;
        /**
         * 基本价（拍下价格），保留两位小数，单位（元）
         */
        @JsonProperty("base_price")
        @JSONField(name = "base_price")
        private BigDecimal basePrice;
        /**
         * 购买数量
         */
        private Integer qty;
        /**
         * 商品名称;最长不超过 100 长度
         */
        private String name;
        /**
         * 商家系统商品主键,最长不超过 50,保持唯一
         */
        @JsonProperty("outer_oi_id")
        @JSONField(name = "outer_oi_id")
        private String outerOiId;
    }

    @Data
    public static class Pay {
        /**
         * 外部支付单号，最大50
         */
        @JsonProperty("outer_pay_id")
        @JSONField(name = "outer_pay_id")
        private String outerPayId;
        /**
         * 支付日期
         */
        @JsonProperty("pay_date")
        @JSONField(name = "pay_date", format = "yyyy-MM-dd HH:mm:ss")
        private Date payDate;
        /**
         * 支付方式，最大10个汉字或20个字符
         */
        private String payment;
        /**
         * 卖家支付账号，最大 50
         */
        @JsonProperty("seller_account")
        @JSONField(name = "seller_account")
        private String sellerAccount;
        /**
         * 买家支付账号，最大 50
         */
        @JsonProperty("buyer_account")
        @JSONField(name = "buyer_account")
        private String buyerAccount;
        /**
         * 支付金额
         */
        private BigDecimal amount;
    }
}
