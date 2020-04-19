package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 订单详情-用户展示订单列表
 *
 * @author auto
 * @since 2.0
 */
public class OrderInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 订单编号
	 */
	@ApiField("billId")
	private String billId;

	/**
	 * 商品类型：1">实物商品2">直充商品3">卡密商品4">话费充值6">支付和金币充值
	 */
	@ApiField("classType")
	private String classType;

	/**
	 * 面值
	 */
	@ApiField("facePrice")
	private String facePrice;

	/**
	 * 商品单价
	 */
	@ApiField("itemCost")
	private String itemCost;

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
	 * 商品数量
	 */
	@ApiField("itemNum")
	private String itemNum;

	/**
	 * 处理时间
	 */
	@ApiField("operateTime")
	private String operateTime;

	/**
	 * 订单成本
	 */
	@ApiField("orderCost")
	private String orderCost;

	/**
	 * 订单利润
	 */
	@ApiField("orderProfit")
	private String orderProfit;

	/**
	 * 订单生成时间
	 */
	@ApiField("orderTime")
	private String orderTime;

	/**
	 * 订单时间(全部)
	 */
	@ApiField("orderTimeFull")
	private String orderTimeFull;

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
	 * 充值帐号
	 */
	@ApiField("rechargeAccount")
	private String rechargeAccount;

	/**
	 * 订单充值状态 0充值中 1成功 9撤销
	 */
	@ApiField("rechargeState")
	private String rechargeState;

	/**
	 * 撤销原因
	 */
	@ApiField("revokeMessage")
	private String revokeMessage;

	/**
	 * 销售金额(<span style="color:red">他的订单表示成本</span>)
	 */
	@ApiField("saleAmount")
	private String saleAmount;

	/**
	 * 供货商编号
	 */
	@ApiField("supUserId")
	private String supUserId;

	public String getBillId() {
		return this.billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getClassType() {
		return this.classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getFacePrice() {
		return this.facePrice;
	}
	public void setFacePrice(String facePrice) {
		this.facePrice = facePrice;
	}

	public String getItemCost() {
		return this.itemCost;
	}
	public void setItemCost(String itemCost) {
		this.itemCost = itemCost;
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

	public String getItemNum() {
		return this.itemNum;
	}
	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}

	public String getOperateTime() {
		return this.operateTime;
	}
	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getOrderCost() {
		return this.orderCost;
	}
	public void setOrderCost(String orderCost) {
		this.orderCost = orderCost;
	}

	public String getOrderProfit() {
		return this.orderProfit;
	}
	public void setOrderProfit(String orderProfit) {
		this.orderProfit = orderProfit;
	}

	public String getOrderTime() {
		return this.orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public String getOrderTimeFull() {
		return this.orderTimeFull;
	}
	public void setOrderTimeFull(String orderTimeFull) {
		this.orderTimeFull = orderTimeFull;
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

	public String getRechargeAccount() {
		return this.rechargeAccount;
	}
	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}

	public String getRechargeState() {
		return this.rechargeState;
	}
	public void setRechargeState(String rechargeState) {
		this.rechargeState = rechargeState;
	}

	public String getRevokeMessage() {
		return this.revokeMessage;
	}
	public void setRevokeMessage(String revokeMessage) {
		this.revokeMessage = revokeMessage;
	}

	public String getSaleAmount() {
		return this.saleAmount;
	}
	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getSupUserId() {
		return this.supUserId;
	}
	public void setSupUserId(String supUserId) {
		this.supUserId = supUserId;
	}

}