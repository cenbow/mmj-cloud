package com.mmj.pay.dto;

import lombok.Data;

@Data
public class OrderPriceDetailsInfo {
	
	/**
     * 订单总价(包含运费,不包含任何优惠)，单位：元
     */
    private Double orderTotalPrice;

    /**
     * 商品总金额，不包含其它任何费用，单位：元
     */
    private Double goodTotalPrice;

    /**
     * 优惠券优惠金额，单位：元
     */
    private Double preferentialMoney;

    /**
     * 支付金额，单位：元
     */
    private Double payPrice;

    /**
     * 运费，单位：元
     */
    private Double freight;

    /**
     * 免邮描述
     */
    private String freightFreeDesc;

    /***
     *  新客免邮--免邮特权
     */
    private Double freePost;

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
	 * 买多少送多少活动送的买买金数量
	 */
	private int buyGiveKingCount;

	 /**
     * 非优惠券的优惠金额
     */
    private Double discountAmount;

}
