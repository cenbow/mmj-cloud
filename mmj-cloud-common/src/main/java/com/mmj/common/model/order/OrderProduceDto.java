package com.mmj.common.model.order;

import lombok.Data;

/**
 * @description: 订单生成
 * @auther: KK
 * @date: 2019/9/3
 */
@Data
public class OrderProduceDto {
    private String orderNo;
    private Integer orderType;
    private Integer orderStatus;
    private Integer orderAmount;
    private Integer goodAmount;
    private Integer discountAmount;
    private String orderSource;
    private String orderChannel;
    private String appId;
    private String openId;
    private Integer businessId;
    private String passingData;
    private Boolean memberOrder;
    private Long createrId;
    //是否虚拟商品 0否 1是
    private Integer virtualGood;
    private Goods goods;
    private Consignee consignee;
    private Payment payment;

    @Data
    public static class Goods {
        private Integer goodId;
        private String goodSpu;
        private Integer saleId;
        private String goodSku;
        private String classCode;
        private String warehouseId;
        private String goodName;
        private String goodImage;
        private Integer goodNum;
        private Integer priceType;
        private Integer goodPrice;
        private Integer goodAmount;
        private Integer memberPrice;
        private String modelName;
        private String virtualFlag;
        //虚拟商品类型 1:优惠券,2:买买金,3:话费 4:直冲话费
        private Integer virtualType;
        private String snapshotData;
        private Integer discountAmount;
        private Integer couponAmount;
    }

    @Data
    public static class Payment {
        private String payType;
        private Integer payAmount;
        private Integer payStatus;
        private String payNo;
        private String payDesc;
    }

    @Data
    public static class Consignee {
        /**
         * 省
         */
        private String province;
        /**
         * 市
         */
        private String city;
        /**
         * 区/县
         */
        private String area;
        /**
         * 收货地址
         */
        private String consumerAddr;
        /**
         * 收货人
         */
        private String consumerName;
        /**
         * 收货电话
         */
        private String consumerMobile;
    }
}
