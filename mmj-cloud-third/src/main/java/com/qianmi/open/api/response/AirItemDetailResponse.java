package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Item;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.air.item.detail response.
 *
 * @author auto
 * @since 2.0
 */
public class AirItemDetailResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 商品详情
	 */
	@ApiField("item")
	private Item item;

	public void setItem(Item item) {
		this.item = item;
	}
	public Item getItem( ) {
		return this.item;
	}

}
