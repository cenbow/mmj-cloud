package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Item;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.mobile.flow.items.list2 response.
 *
 * @author auto
 * @since 2.0
 */
public class BmMobileFlowItemsList2Response extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 流量商品详情列表
	 */
	@ApiListField("items")
	@ApiField("item")
	private List<Item> items;

	public void setItems(List<Item> items) {
		this.items = items;
	}
	public List<Item> getItems( ) {
		return this.items;
	}

}
