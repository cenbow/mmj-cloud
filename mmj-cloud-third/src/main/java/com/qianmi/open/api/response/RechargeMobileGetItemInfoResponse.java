package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.MobileItemInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.recharge.mobile.getItemInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class RechargeMobileGetItemInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 话费商品信息
	 */
	@ApiField("mobileItemInfo")
	private MobileItemInfo mobileItemInfo;

	public void setMobileItemInfo(MobileItemInfo mobileItemInfo) {
		this.mobileItemInfo = mobileItemInfo;
	}
	public MobileItemInfo getMobileItemInfo( ) {
		return this.mobileItemInfo;
	}

}
