package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 水电煤账户信息
 *
 * @author auto
 * @since 2.0
 */
public class WaterCoalBill extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 账号
	 */
	@ApiField("accountNo")
	private String accountNo;

	/**
	 * 余额
	 */
	@ApiField("balance")
	private String balance;

	/**
	 * 起始日期
	 */
	@ApiField("beginDate")
	private String beginDate;

	/**
	 * 返回的账期(供下单充值)
	 */
	@ApiField("billCycle")
	private String billCycle;

	/**
	 * 合同号
	 */
	@ApiField("contractNo")
	private String contractNo;

	/**
	 * 地址
	 */
	@ApiField("customerAddress")
	private String customerAddress;

	/**
	 * 客户姓名
	 */
	@ApiField("customerName")
	private String customerName;

	/**
	 * 结束日期
	 */
	@ApiField("endDate")
	private String endDate;

	/**
	 * 多账期
	 */
	@ApiField("filed1")
	private String filed1;

	/**
	 * 账单月
	 */
	@ApiField("month")
	private String month;

	/**
	 * 应缴金额
	 */
	@ApiField("payAmount")
	private String payAmount;

	/**
	 * penalty
	 */
	@ApiField("penalty")
	private String penalty;

	public String getAccountNo() {
		return this.accountNo;
	}
	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBalance() {
		return this.balance;
	}
	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getBeginDate() {
		return this.beginDate;
	}
	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getBillCycle() {
		return this.billCycle;
	}
	public void setBillCycle(String billCycle) {
		this.billCycle = billCycle;
	}

	public String getContractNo() {
		return this.contractNo;
	}
	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

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

	public String getEndDate() {
		return this.endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getFiled1() {
		return this.filed1;
	}
	public void setFiled1(String filed1) {
		this.filed1 = filed1;
	}

	public String getMonth() {
		return this.month;
	}
	public void setMonth(String month) {
		this.month = month;
	}

	public String getPayAmount() {
		return this.payAmount;
	}
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	public String getPenalty() {
		return this.penalty;
	}
	public void setPenalty(String penalty) {
		this.penalty = penalty;
	}

}