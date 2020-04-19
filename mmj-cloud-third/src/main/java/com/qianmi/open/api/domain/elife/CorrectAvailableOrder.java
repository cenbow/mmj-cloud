package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 可冲正的订单
 *
 * @author auto
 * @since 2.0
 */
public class CorrectAvailableOrder extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 冲正账号-手机号
	 */
	@ApiField("account")
	private String account;

	/**
	 * 订单编号
	 */
	@ApiField("billId")
	private String billId;

	/**
	 * 订单创建时间
	 */
	@ApiField("created")
	private String created;

	/**
	 * 标准商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 标准商品名称
	 */
	@ApiField("itemName")
	private String itemName;

	/**
	 * 充值金额
	 */
	@ApiField("rechargeAmount")
	private String rechargeAmount;

	/**
	 * 实际支付金额
	 */
	@ApiField("totalCash")
	private String totalCash;

	public String getAccount() {
		return this.account;
	}
	public void setAccount(String account) {
		this.account = account;
	}

	public String getBillId() {
		return this.billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getCreated() {
		return this.created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public String getItemId() {
		return this.itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemName() {
		return this.itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRechargeAmount() {
		return this.rechargeAmount;
	}
	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getTotalCash() {
		return this.totalCash;
	}
	public void setTotalCash(String totalCash) {
		this.totalCash = totalCash;
	}

}