package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Bank;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.finance.banks.list response.
 *
 * @author auto
 * @since 2.0
 */
public class FinanceBanksListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 银行信息
	 */
	@ApiListField("banks")
	@ApiField("bank")
	private List<Bank> banks;

	public void setBanks(List<Bank> banks) {
		this.banks = banks;
	}
	public List<Bank> getBanks( ) {
		return this.banks;
	}

}
