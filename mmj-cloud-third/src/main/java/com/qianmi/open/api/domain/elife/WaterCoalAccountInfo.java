package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 水电煤账户信息
 *
 * @author auto
 * @since 2.0
 */
public class WaterCoalAccountInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户地址
	 */
	@ApiField("customerAddress")
	private String customerAddress;

	/**
	 * 户名
	 */
	@ApiField("customerName")
	private String customerName;

	/**
	 * 应缴金额
	 */
	@ApiField("payAmount")
	private String payAmount;

	public String getCustomerAddress() {
		return this.customerAddress;
	}
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public String getCustomerName() {
		return this.customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getPayAmount() {
		return this.payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

}