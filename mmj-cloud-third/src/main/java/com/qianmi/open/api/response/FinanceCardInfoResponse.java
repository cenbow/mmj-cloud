package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.BankCard;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.finance.card.info response.
 *
 * @author auto
 * @since 2.0
 */
public class FinanceCardInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 银行卡片详情
	 */
	@ApiField("bankCard")
	private BankCard bankCard;

	public void setBankCard(BankCard bankCard) {
		this.bankCard = bankCard;
	}
	public BankCard getBankCard( ) {
		return this.bankCard;
	}

}
