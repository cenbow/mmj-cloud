package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: bm.elife.recharge.mobile.correct response.
 *
 * @author auto
 * @since 2.0
 */
public class BmRechargeMobileCorrectResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 冲正订单结果: 1-发起冲正成功 0-发起冲正失败
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
