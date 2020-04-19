package com.mmj.order.common.model.vo;

import lombok.Data;

import java.io.Serializable;

import com.mmj.common.model.Details;
import lombok.ToString;

@Data
@ToString
public class CartOrderGoodsDetails implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4188094496571465030L;

    /**
     * 用户微信唯一标识
     */
    private Long userid;

    /**
     * 订单总金额（不含优惠），包含运费
     */
    private Double orderTotalPrice;

    /**
     * 商品总金额
     */
    private Double goodsTotalPrice;

    /**
     * 支付金额
     */
    private Double payPrice;

    /**
     * 优惠券
     */
    private String couponCode;

    /**
     * 商品详情
     */
    private Details[] details;

    /**
     * 运费
     */
    private Double freight;

    /**
     * 优惠券的优惠金额
     */
    private Double preferentialMoney;

    /**
     * 免邮描述
     */
    private String freightFreeDesc;

    /**
     * 订单类型
     */
    private Integer orderType;


    /**
     * 是否选择使用买买金
     */
    private boolean kingSelected;

    /**
     * 使用买买金的个数
     */
    private Integer useKingNum;

    /**
     * 买买金兑换的金额/抵扣的金额
     */
    private Double exchangeMoney;

    /**
     * 活动Id
     */
    private Integer businessId;

    private String passingData;

    private String orderNo;

    /**
     * 非优惠券的优惠金额
     */
    private Double discountAmount;
}
