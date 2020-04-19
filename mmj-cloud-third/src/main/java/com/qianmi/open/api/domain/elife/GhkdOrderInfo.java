package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 固话宽带订单信息
 *
 * @author auto
 * @since 2.0
 */
public class GhkdOrderInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 充值账号
	 */
	@ApiField("account")
	private String account;

	/**
	 * 商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 商品名称
	 */
	@ApiField("itemName")
	private String itemName;

	/**
	 * 外部订单号
	 */
	@ApiField("outerTid")
	private String outerTid;

	/**
	 * 订单付款状态 0 未付款1 已付款
	 */
	@ApiField("payState")
	private String payState;

	/**
	 * 订单充值状态 0充值中 1成功 9撤销
	 */
	@ApiField("rechargeState")
	private String rechargeState;

	/**
	 * 售价（应收金额）
	 */
	@ApiField("salePrice")
	private String salePrice;

	/**
	 * 订单编号
	 */
	@ApiField("tid")
	private String tid;

	public String getAccount() {
		return this.account;
	}
	public void setAccount(String account) {
		this.account = account;
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

	public String getOuterTid() {
		return this.outerTid;
	}
	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}

	public String getPayState() {
		return this.payState;
	}
	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getRechargeState() {
		return this.rechargeState;
	}
	public void setRechargeState(String rechargeState) {
		this.rechargeState = rechargeState;
	}

	public String getSalePrice() {
		return this.salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getTid() {
		return this.tid;
	}
	public void setTid(String tid) {
		this.tid = tid;
	}

}