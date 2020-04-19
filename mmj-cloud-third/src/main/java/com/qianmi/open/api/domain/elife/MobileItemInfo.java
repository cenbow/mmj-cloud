package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 话费充值商品详情
 *
 * @author auto
 * @since 2.0
 */
public class MobileItemInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 建议零售价
	 */
	@ApiField("advicePrice")
	private String advicePrice;

	/**
	 * 手机所在市
	 */
	@ApiField("city")
	private String city;

	/**
	 * 进价
	 */
	@ApiField("inPrice")
	private String inPrice;

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
	 * 充值号码
	 */
	@ApiField("mobileNo")
	private String mobileNo;

	/**
	 * 可购买数量
	 */
	@ApiField("numberChoice")
	private String numberChoice;

	/**
	 * 手机运营商
	 */
	@ApiField("operator")
	private String operator;

	/**
	 * 手机所在省
	 */
	@ApiField("province")
	private String province;

	/**
	 * 充值金额
	 */
	@ApiField("rechargeAmount")
	private String rechargeAmount;

	/**
	 * 是否支持冲正 1 支持 0 不支持
	 */
	@ApiField("reverseFlag")
	private String reverseFlag;

	public String getAdvicePrice() {
		return this.advicePrice;
	}
	public void setAdvicePrice(String advicePrice) {
		this.advicePrice = advicePrice;
	}

	public String getCity() {
		return this.city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	public String getInPrice() {
		return this.inPrice;
	}
	public void setInPrice(String inPrice) {
		this.inPrice = inPrice;
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

	public String getMobileNo() {
		return this.mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getNumberChoice() {
		return this.numberChoice;
	}
	public void setNumberChoice(String numberChoice) {
		this.numberChoice = numberChoice;
	}

	public String getOperator() {
		return this.operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public String getRechargeAmount() {
		return this.rechargeAmount;
	}
	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}

	public String getReverseFlag() {
		return this.reverseFlag;
	}
	public void setReverseFlag(String reverseFlag) {
		this.reverseFlag = reverseFlag;
	}

}