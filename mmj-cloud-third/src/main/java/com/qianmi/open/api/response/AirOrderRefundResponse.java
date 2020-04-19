package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.air.order.refund response.
 *
 * @author auto
 * @since 2.0
 */
public class AirOrderRefundResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 操作结果 1 成功 0 失败
	 */
	@ApiField("result")
	private String result;

	public void setResult(String result) {
		this.result = result;
	}
	public String getResult( ) {
		return this.result;
	}

}
