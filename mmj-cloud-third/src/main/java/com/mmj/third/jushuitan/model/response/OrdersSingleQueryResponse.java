package com.mmj.third.jushuitan.model.response;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 订单查询返回
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdersSingleQueryResponse extends JushuitanResponse {
    @JsonProperty("has_next")
    @JSONField(name = "has_next")
    private Boolean hasNext;
    @JsonProperty("page_index")
    @JSONField(name = "page_index")
    private Integer pageIndex;
    @JsonProperty("page_size")
    @JSONField(name = "page_size")
    private Integer pageSize;
    private List<OrdersBean> orders;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OrdersBean {
        /**
         * is_cod : false
         * l_id : @3905983745120
         * pays : [{"is_order_pay":false,"buyer_account":"yike7777@163.com","amount":"79.79","pay_date":"2018-03-30 10:02:58","outer_pay_id":"7448338439423713","pay_id":"584849","payment":"支付宝"}]
         * co_id : 10050067
         * shop_id : 10116896
         * send_date : 2018-04-11 19:49:56
         * pay_date : 2018-03-28 20:01:24
         * freight : 0.0
         * receiver_address : 天洋城4代小区9号楼2单元1801请放（蜂巢）.
         * wms_co_id : 0
         * logistics_company : 韵达快递
         * receiver_district : 燕郊经济技术开发区
         * free_amount : 25.0
         * shop_name : 美丽夏夏2013
         * question_type : 预售
         * outer_pay_id : 2018032821001001150573345614
         * so_id : 129249176100421337
         * type : 普通订单
         * order_from : WAP,WAP
         * status : Split
         * pay_amount : 326.7
         * shop_buyer_id : 莎丁小丸子
         * shop_status : TRADE_FINISHED
         * receiver_mobile : 18910172290
         * order_date : 2018-03-28 20:01:16
         * question_desc : 退款成功，转到预售异常
         * receiver_city : 廊坊市
         * items : [{"is_gift":false,"sku_id":"SY275TX00900S","name":"美美的夏夏：2018夏装新品 时尚少女心 宽松亲肤圆领套头卫衣t恤","refund_status":"none","refund_id":"0","price":"85.9","outer_oi_id":"129249176101421337","item_status":"Sent","i_id":"SY275","properties_value":"T恤-紫-预定15个工作日发;S","oi_id":908767,"amount":"85.9","shop_sku_id":"3601864341657","raw_so_id":"129249176100421337","qty":1,"is_presale":true,"base_price":"95.45"},{"is_gift":false,"sku_id":"SY275TX00100S","name":"美美的夏夏：2018夏装新品 时尚少女心 宽松亲肤圆领套头卫衣t恤","refund_status":"success","refund_id":"7448338439423713","price":"85.9","outer_oi_id":"129249176102421337","item_status":"Cancelled","i_id":"SY275","properties_value":"T恤-白-预定10个工作日发;S","oi_id":908768,"amount":"85.9","shop_sku_id":"3601864341658","raw_so_id":"129249176100421337","qty":1,"is_presale":true,"base_price":"95.45"},{"is_gift":false,"sku_id":"CS03800500S","name":"美美的夏夏：小V领显锁骨喇叭袖衬衫女2018春装新款收腰显瘦上衣","refund_status":"none","refund_id":"0","price":"179.9","outer_oi_id":"129249176103421337","item_status":"Sent","i_id":"CS038","properties_value":"酒红色-预定10个工作日发;S","oi_id":908769,"amount":"179.9","shop_sku_id":"3767662215307","raw_so_id":"129249176100421337","qty":1,"is_presale":true,"base_price":"199.89"}]
         * modified : 2018-05-11 18:40:31
         * receiver_state : 河北省
         * receiver_name : 子内
         * o_id : 609367
         * lc_id : YUNDA
         */

        @JsonProperty("is_cod")
        @JSONField(name = "is_cod")
        private Boolean isCod;
        @JsonProperty("l_id")
        @JSONField(name = "l_id")
        private String lId;
        @JsonProperty("co_id")
        @JSONField(name = "co_id")
        private Integer coId;
        @JsonProperty("shop_id")
        @JSONField(name = "shop_id")
        private Integer shopId;
        @JsonProperty("send_date")
        @JSONField(name = "send_date")
        private String sendDate;
        @JsonProperty("pay_date")
        @JSONField(name = "pay_date")
        private String payDate;
        private String freight;
        @JsonProperty("receiver_address")
        @JSONField(name = "receiver_address")
        private String receiverAddress;
        @JsonProperty("wms_co_id")
        @JSONField(name = "wms_co_id")
        private String wmsCoId;
        @JsonProperty("logistics_company")
        @JSONField(name = "logistics_company")
        private String logisticsCompany;
        @JsonProperty("receiver_district")
        @JSONField(name = "receiver_district")
        private String receiverDistrict;
        @JsonProperty("free_amount")
        @JSONField(name = "free_amount")
        private String freeAmount;
        @JsonProperty("shop_name")
        @JSONField(name = "shop_name")
        private String shopName;
        @JsonProperty("question_type")
        @JSONField(name = "question_type")
        private String questionType;
        @JsonProperty("outer_pay_id")
        @JSONField(name = "outer_pay_id")
        private String outerPayId;
        @JsonProperty("so_id")
        @JSONField(name = "so_id")
        private String soId;
        private String type;
        @JsonProperty("order_from")
        @JSONField(name = "order_from")
        private String orderFrom;
        private String status;
        @JsonProperty("pay_amount")
        @JSONField(name = "pay_amount")
        private String payAmount;
        @JsonProperty("shop_buyer_id")
        @JSONField(name = "shop_buyer_id")
        private String shopBuyerId;
        @JsonProperty("shop_status")
        @JSONField(name = "shop_status")
        private String shopStatus;
        @JsonProperty("receiver_mobile")
        @JSONField(name = "receiver_mobile")
        private String receiverMobile;
        @JsonProperty("order_date")
        @JSONField(name = "order_date")
        private String orderDate;
        @JsonProperty("question_desc")
        @JSONField(name = "question_desc")
        private String questionDesc;
        @JsonProperty("receiver_city")
        @JSONField(name = "receiver_city")
        private String receiverCity;
        private String modified;
        @JsonProperty("receiver_state")
        @JSONField(name = "receiver_state")
        private String receiverState;
        @JsonProperty("receiver_name")
        @JSONField(name = "receiver_name")
        private String receiverName;
        @JsonProperty("o_id")
        @JSONField(name = "o_id")
        private Integer oId;
        @JsonProperty("lc_id")
        @JSONField(name = "lc_id")
        private String lcId;
        private List<PaysBean> pays;
        private List<ItemsBean> items;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class PaysBean {
            /**
             * is_order_pay : false
             * buyer_account : yike7777@163.com
             * amount : 79.79
             * pay_date : 2018-03-30 10:02:58
             * outer_pay_id : 7448338439423713
             * pay_id : 584849
             * payment : 支付宝
             */

            @JsonProperty("is_order_pay")
            @JSONField(name = "is_order_pay")
            private Boolean isOrderPay;
            @JsonProperty("buyer_account")
            @JSONField(name = "buyer_account")
            private String buyerAccount;
            private String amount;
            @JsonProperty("pay_date")
            @JSONField(name = "pay_date")
            private String payDate;
            @JsonProperty("outer_pay_id")
            @JSONField(name = "outer_pay_id")
            private String outerPayId;
            @JsonProperty("pay_id")
            @JSONField(name = "pay_id")
            private String payId;
            private String payment;
        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ItemsBean {
            /**
             * is_gift : false
             * sku_id : SY275TX00900S
             * name : 美美的夏夏：2018夏装新品 时尚少女心 宽松亲肤圆领套头卫衣t恤
             * refund_status : none
             * refund_id : 0
             * price : 85.9
             * outer_oi_id : 129249176101421337
             * item_status : Sent
             * i_id : SY275
             * properties_value : T恤-紫-预定15个工作日发;S
             * oi_id : 908767
             * amount : 85.9
             * shop_sku_id : 3601864341657
             * raw_so_id : 129249176100421337
             * qty : 1
             * is_presale : true
             * base_price : 95.45
             */

            @JsonProperty("is_gift")
            @JSONField(name = "is_gift")
            private Boolean isGift;
            @JsonProperty("sku_id")
            @JSONField(name = "sku_id")
            private String skuId;
            private String name;
            @JsonProperty("refund_status")
            @JSONField(name = "refund_status")
            private String refundStatus;
            @JsonProperty("refund_id")
            @JSONField(name = "refund_id")
            private String refundId;
            private String price;
            @JsonProperty("outer_oi_id")
            @JSONField(name = "outer_oi_id")
            private String outerOiId;
            @JsonProperty("item_status")
            @JSONField(name = "item_status")
            private String itemStatus;
            @JsonProperty("i_id")
            @JSONField(name = "i_id")
            private String iId;
            @JsonProperty("properties_value")
            @JSONField(name = "properties_value")
            private String propertiesValue;
            @JsonProperty("oi_id")
            @JSONField(name = "oi_id")
            private Integer oiId;
            private String amount;
            @JsonProperty("shop_sku_id")
            @JSONField(name = "shop_sku_id")
            private String shopSkuId;
            @JsonProperty("raw_so_id")
            @JSONField(name = "raw_so_id")
            private String rawSoId;
            private Integer qty;
            @JsonProperty("is_presale")
            @JSONField(name = "is_presale")
            private Boolean isPresale;
            @JsonProperty("base_price")
            @JSONField(name = "base_price")
            private String basePrice;
        }
    }
}
