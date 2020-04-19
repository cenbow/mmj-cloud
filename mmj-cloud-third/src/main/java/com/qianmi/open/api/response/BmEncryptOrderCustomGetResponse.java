package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: bm.elife.encrypt.order.custom.get response.
 *
 * @author auto
 * @since 2.0
 */
public class BmEncryptOrderCustomGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 加密后的订单详情
	 */
	@ApiField("orderDetailInfo")
	private String orderDetailInfo;

	public void setOrderDetailInfo(String orderDetailInfo) {
		this.orderDetailInfo = orderDetailInfo;
	}
	public String getOrderDetailInfo( ) {
		return this.orderDetailInfo;
	}

}
