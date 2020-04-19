package com.mmj.user.member.dto;

import lombok.extern.slf4j.Slf4j;

import com.mmj.common.constants.OrderType;
import com.mmj.common.model.Details;
import com.mmj.common.utils.DoubleUtil;

@Slf4j
public class MyKingExchangeParam {
	
	private int orderType;
	
	private Details[] details;
	
	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public Details[] getDetails() {
		return details;
	}

	public void setDetails(Details[] details) {
		this.details = details;
	}

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
}
