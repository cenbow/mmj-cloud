package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.CorrectOrder;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: bm.elife.recharge.mobile.correct.detail response.
 *
 * @author auto
 * @since 2.0
 */
public class BmRechargeMobileCorrectDetailResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 冲正订单详情: 1-发起冲正成功 0-发起冲正失败
	 */
	@ApiField("correctOrder")
	private CorrectOrder correctOrder;

	public void setCorrectOrder(CorrectOrder correctOrder) {
		this.correctOrder = correctOrder;
	}
	public CorrectOrder getCorrectOrder( ) {
		return this.correctOrder;
	}

}
