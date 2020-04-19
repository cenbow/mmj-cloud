package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.LocalItemOrderInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.cardPassword.localItem.createBill response.
 *
 * @author auto
 * @since 2.0
 */
public class CardPasswordLocalItemCreateBillResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 本地商品订单
	 */
	@ApiField("localItemOrderInfo")
	private LocalItemOrderInfo localItemOrderInfo;

	public void setLocalItemOrderInfo(LocalItemOrderInfo localItemOrderInfo) {
		this.localItemOrderInfo = localItemOrderInfo;
	}
	public LocalItemOrderInfo getLocalItemOrderInfo( ) {
		return this.localItemOrderInfo;
	}

}
