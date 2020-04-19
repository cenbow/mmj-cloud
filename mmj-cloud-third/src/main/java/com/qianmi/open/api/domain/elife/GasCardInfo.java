package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 加油卡详情
 *
 * @author auto
 * @since 2.0
 */
public class GasCardInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 加油卡卡号
	 */
	@ApiField("cardNo")
	private String cardNo;

	/**
	 * 加油卡状态,加油卡用户名
	 */
	@ApiField("cardState")
	private String cardState;

	public String getCardNo() {
		return this.cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardState() {
		return this.cardState;
	}
	public void setCardState(String cardState) {
		this.cardState = cardState;
	}

}