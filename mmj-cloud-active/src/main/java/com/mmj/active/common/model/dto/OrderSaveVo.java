package com.mmj.active.common.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description: 下单
 * @auther: KK
 * @date: 2019/7/23
 */
@Data
public class OrderSaveVo {
    private String userId;

    private String couponAmount;

    private String discountAmount;

    private String freight;

    private String freightRemarks;

    private Integer orderType;

    private String orderAmount;  // 订单金额

    private String passingData; // 传递数据

    private Integer type;  // 下单类型 0 购物车 1 立即下单

    private Integer businessId;  // 关联id（活动id）

    private String source;

    private String consumerDesc;  //  用户备注

    private List<GoodsVo> good;

    private ConsigneeVo consigness;   // 收件人信息

    private boolean memberOrder = false;  //  会员订单

    private String couponCode;  //  优惠券编码

    /**
     * 是否选择使用买买金
     */
    private boolean kingSelected = false;

    /**
     * 使用买买金的个数
     */
    private Integer useKingNum;

    /**
     * 买买金兑换的金额/抵扣的金额
     */
    private String exchangeMoney;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GoodsVo {
        private Integer goodId;

        private Integer saleId;

        private String goodTitle;

        private String goodImage;

        private String goodSku;

        private Integer goodNum;

        private String unitPrice;  // 单价

        private String originalPrice;  // 原价

        private String couponPrice;    // 优惠价

        private String discountedPrice;  // 折扣价

        private String freight;  // 运费

        private String modelId;

        private String modelName;

        private boolean memberFlag = false;

        /**
         * 买买金兑换金额
         */
        private Integer goldPrice = 0;

        /**
         * 会员价
         */
        private String memberPrice;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConsigneeVo {
        private String country;

        private String province;

        private String city;

        private String area;

        private String consumerAddr;

        private String consumerName;

        private String consumerMobile;
    }
}
