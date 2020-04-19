package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.PriceInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.recharge.base.calcPrice response.
 *
 * @author auto
 * @since 2.0
 */
public class RechargeBaseCalcPriceResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 价格信息
	 */
	@ApiField("priceInfo")
	private PriceInfo priceInfo;

	public void setPriceInfo(PriceInfo priceInfo) {
		this.priceInfo = priceInfo;
	}
	public PriceInfo getPriceInfo( ) {
		return this.priceInfo;
	}

}
