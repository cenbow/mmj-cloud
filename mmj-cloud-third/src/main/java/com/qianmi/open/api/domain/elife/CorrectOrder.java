package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 冲正订单详情
 *
 * @author auto
 * @since 2.0
 */
public class CorrectOrder extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 充值账号-手机号
	 */
	@ApiField("account")
	private String account;

	/**
	 * S订单号
	 */
	@ApiField("billId")
	private String billId;

	/**
	 * 冲正单号
	 */
	@ApiField("correctId")
	private String correctId;

	/**
	 * 冲正状态  1-待冲正 4-冲正成功 5-冲正失败
	 */
	@ApiField("correctState")
	private String correctState;

	/**
	 * 冲正订单发起时间
	 */
	@ApiField("created")
	private String created;

	/**
	 * 用户退款说明
	 */
	@ApiField("des")
	private String des;

	/**
	 * 冲正完成时间
	 */
	@ApiField("endTime")
	private String endTime;

	/**
	 * 标准商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 购买数量
	 */
	@ApiField("itemNum")
	private String itemNum;

	/**
	 * 订单金额
	 */
	@ApiField("orderFee")
	private String orderFee;

	/**
	 * 退款金额
	 */
	@ApiField("refundFee")
	private String refundFee;

	/**
	 * 用户编号
	 */
	@ApiField("userCode")
	private String userCode;

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

	public String getCorrectId() {
		return this.correctId;
	}
	public void setCorrectId(String correctId) {
		this.correctId = correctId;
	}

	public String getCorrectState() {
		return this.correctState;
	}
	public void setCorrectState(String correctState) {
		this.correctState = correctState;
	}

	public String getCreated() {
		return this.created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public String getDes() {
		return this.des;
	}
	public void setDes(String des) {
		this.des = des;
	}

	public String getEndTime() {
		return this.endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getItemId() {
		return this.itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemNum() {
		return this.itemNum;
	}
	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}

	public String getOrderFee() {
		return this.orderFee;
	}
	public void setOrderFee(String orderFee) {
		this.orderFee = orderFee;
	}

	public String getRefundFee() {
		return this.refundFee;
	}
	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}

	public String getUserCode() {
		return this.userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}