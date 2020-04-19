package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 保单信息
 *
 * @author auto
 * @since 2.0
 */
public class OInsurance extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 票面价
	 */
	@ApiField("facePrice")
	private String facePrice;

	/**
	 * 保单信息
	 */
	@ApiField("insuranceInfo")
	private String insuranceInfo;

	/**
	 * 保险单订单号
	 */
	@ApiField("insuranceNo")
	private String insuranceNo;

	/**
	 * 保险单号,支付购买成功后展示
	 */
	@ApiField("insurancePolicyNo")
	private String insurancePolicyNo;

	/**
	 * 商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 实际支付的金额
	 */
	@ApiField("payCash")
	private String payCash;

	/**
	 * 实际票价，实际网点的采购价
	 */
	@ApiField("purPrice")
	private String purPrice;

	/**
	 * 销售订单号
	 */
	@ApiField("saleOrderNo")
	private String saleOrderNo;

	/**
	 * 建议零售价
	 */
	@ApiField("salePrice")
	private String salePrice;

	/**
	 * 订单展示状态 0:交易中,9:出票失败,1:出票成功,6:退票中,7:退票失败,10:已退票,11:已退款
	 */
	@ApiField("state")
	private Integer state;

	/**
	 * 用户姓名
	 */
	@ApiField("userName")
	private String userName;

	/**
	 * 用户号码
	 */
	@ApiField("userTel")
	private String userTel;

	public String getFacePrice() {
		return this.facePrice;
	}
	public void setFacePrice(String facePrice) {
		this.facePrice = facePrice;
	}

	public String getInsuranceInfo() {
		return this.insuranceInfo;
	}
	public void setInsuranceInfo(String insuranceInfo) {
		this.insuranceInfo = insuranceInfo;
	}

	public String getInsuranceNo() {
		return this.insuranceNo;
	}
	public void setInsuranceNo(String insuranceNo) {
		this.insuranceNo = insuranceNo;
	}

	public String getInsurancePolicyNo() {
		return this.insurancePolicyNo;
	}
	public void setInsurancePolicyNo(String insurancePolicyNo) {
		this.insurancePolicyNo = insurancePolicyNo;
	}

	public String getItemId() {
		return this.itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getPayCash() {
		return this.payCash;
	}
	public void setPayCash(String payCash) {
		this.payCash = payCash;
	}

	public String getPurPrice() {
		return this.purPrice;
	}
	public void setPurPrice(String purPrice) {
		this.purPrice = purPrice;
	}

	public String getSaleOrderNo() {
		return this.saleOrderNo;
	}
	public void setSaleOrderNo(String saleOrderNo) {
		this.saleOrderNo = saleOrderNo;
	}

	public String getSalePrice() {
		return this.salePrice;
	}
	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public Integer getState() {
		return this.state;
	}
	public void setState(Integer state) {
		this.state = state;
	}

	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserTel() {
		return this.userTel;
	}
	public void setUserTel(String userTel) {
		this.userTel = userTel;
	}

}