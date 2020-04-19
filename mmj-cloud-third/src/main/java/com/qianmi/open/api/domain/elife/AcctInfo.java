package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 账户信息
 *
 * @author auto
 * @since 2.0
 */
public class AcctInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 账户余额
	 */
	@ApiField("balance")
	private String balance;

	public String getBalance() {
		return this.balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}

}