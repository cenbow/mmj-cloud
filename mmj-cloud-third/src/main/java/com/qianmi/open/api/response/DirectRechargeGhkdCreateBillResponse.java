package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.GhkdOrderInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.directRecharge.ghkd.createBill response.
 *
 * @author auto
 * @since 2.0
 */
public class DirectRechargeGhkdCreateBillResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 固话宽带订单信息
	 */
	@ApiField("ghkdOrderInfo")
	private GhkdOrderInfo ghkdOrderInfo;

	public void setGhkdOrderInfo(GhkdOrderInfo ghkdOrderInfo) {
		this.ghkdOrderInfo = ghkdOrderInfo;
	}
	public GhkdOrderInfo getGhkdOrderInfo( ) {
		return this.ghkdOrderInfo;
	}

}
