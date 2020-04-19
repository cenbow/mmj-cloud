package com.mmj.common.model.order;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @description: 订单队列模板
 * @auther: KK
 * @date: 2019/8/8
 */
@Data
public class OrdersMQDto {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 客户端标识
     */
    private String appId;
    /**
     * 订单类型
     */
    private Integer orderType;
    /**
     * 订单状态
     */
    private Integer orderStatus;
    /**
     * 订单时间
     */
    private Date orderDate;
    /**
     * 订单金额
     */
    private Integer orderAmount;
    /**
     * 传递数据
     */
    private String passingData;
    /**
     * 关联id（活动id）
     */
    private Integer businessId;
    /**
     * 优惠券编码
     */
    private String couponCode;
    /**
     * 使用买买金的个数
     */
    private Integer useKingNum = 0;
    /**
     * 是否会员下单
     */
    private Boolean memberOrder;
    /**
     * 下单来源 0 购物车 1 立即下单
     */
    private Integer type;
    /**
     * 订单来源 MIN(小程序) MH5(站内H5) H5(站外h5)
     */
    private String source;
    /**
     * 微信用户标识
     */
    private String openId;
    /**
     * 订单渠道
     */
    private String channel;
    /**
     * 商品信息
     */
    private List<Goods> goods;
    /**
     * 收件人信息
     */
    private OrderConsigneeMQDto consignee;
    /**
     * 支付信息
     */
    private OrderPayDto pay;

    @Data
    public static class Goods {
        private String goodName;
        private String goodImage;
        private String goodSku;
        private Integer goodNum;
        private Integer goodId;
    }
}
