package com.mmj.common.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @description: 下单数据缓存（有效期一个月）
 * @auther: KK
 * @date: 2019/9/3
 */
@Data
public class OrderStore implements Serializable {
    private static final long serialVersionUID = 8735132092273200831L;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单类型
     */
    private Integer orderType;
    /**
     * 订单金额
     */
    private Integer orderAmount;
    /**
     * 活动ID
     */
    private Integer businessId;
    /**
     * 参数  activeId(businessId) 活动ID
     */
    private String passingData;
    /**
     * 商品信息
     */
    private List<Goods> goodsList; //商品信息
    /**
     * 收货信息
     */
    private Consignee consignee;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 用户昵称
     */
    private String userFullName;

    private String source;
    private String channel;
    private String appId;
    private String openId;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goods {
        private String goodName;//商品名称
        private String modelName; //规格值
        private Integer goodId; //商品id
        private String spu;
        private Integer saleId; //销售ID
        private String sku;
        /**
         * 单价类型 0店铺价 1会员价 2原价 3活动价
         */
        private Integer priceType;
        private Integer unitPrice;    //单价
        private Integer originalPrice; // 原价
        private Integer goodNum;    //购买数量
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Consignee {
        /**
         * 收货人
         */
        private String name;
        /**
         * 收货人电话
         */
        private String mobile;
        /**
         * 省
         */
        private String province;
        /**
         * 市
         */
        private String city;
        /**
         * 区
         */
        private String area;
        /**
         * 详细地址
         */
        private String address;
    }
}
