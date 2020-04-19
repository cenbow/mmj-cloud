package com.mmj.active.callCharge.model.dto;

import lombok.Data;

/**
 * @description: 话费商品
 * @auther: KK
 * @date: 2019/8/1
 */
@Data
public class RechargeGoodsDto {
    /**
     * 商品id
     */
    private Integer goodsId;

    /**
     * 话费商品标题
     */
    private String goodsTitle;

    /**
     * 原价
     */
    private String originalPrice;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 会员价格
     */
    private String memberPrice;

    /**
     * 优惠券金额
     */
    private String discountedPrice;

    /**
     * 当天剩余数量
     */
    private Integer todayLastNumber;

    /**
     * 是否有权益
     */
    private boolean hasRight;
}
