package com.mmj.pay.model.vo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.constants.OrderType;
import com.mmj.common.model.Details;
import com.mmj.common.utils.DoubleUtil;

@Slf4j
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class CartOrderCouponParam implements Serializable {

    private static final long serialVersionUID = -8624524826956069384L;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 订单下的商品总价，单位：元
     */
    private Double goodsTotalPrice;

    /**
     * 商品总件数
     */
    private Integer totalCount;

    /**
     * 商品详情，可包含多种商品
     */
    private Details[] details;

    /**
     * 优惠券编码
     */
    private String couponCode;

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
    private Integer useKingNum = 0;

    /**
     * 买买金兑换的金额/抵扣的金额
     */
    private Double exchangeMoney = 0d;

    /**
     * 活动Id
     */
    private Integer businessId;

    private String passingData;
    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 计算商品总金额
     *
     * @param isMember
     * @return
     */
    public double getGoodTotalPrice(boolean isMember) {
        double goodTotalPrice = 0.0d;
        boolean orderCanUseMemberPrice = isMember && (orderType == OrderType.TEN_YUAN_SHOP || orderType == OrderType.TWO_GROUP);
        double unitPrice = 0.0d;
        double unitTotalPrice = 0.0d;
        for (Details good : details) {
            unitPrice = orderCanUseMemberPrice ? good.getMemberPrice() : good.getUnitPrice();
            unitTotalPrice = DoubleUtil.mul(unitPrice, Double.valueOf(good.getCount()));
            goodTotalPrice = DoubleUtil.add(goodTotalPrice, unitTotalPrice);
        }

        log.info("-->计算商品总价为:{}元，是否会员：{}", goodTotalPrice, isMember);
        return goodTotalPrice;
    }

    /**
     * 计算商品总件数
     *
     * @return
     */
    public int getGoodTotalCount() {
        int count = 0;
        for (Details good : details) {
            count += good.getCount();
        }
        log.info("-->计算商品总件数为:{}", count);
        return count;
    }

}
