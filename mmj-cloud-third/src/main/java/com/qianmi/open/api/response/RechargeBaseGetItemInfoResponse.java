package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.ItemInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.recharge.base.getItemInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class RechargeBaseGetItemInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 商品
	 */
	@ApiField("itemInfo")
	private ItemInfo itemInfo;

	public void setItemInfo(ItemInfo itemInfo) {
		this.itemInfo = itemInfo;
	}
	public ItemInfo getItemInfo( ) {
		return this.itemInfo;
	}

}
