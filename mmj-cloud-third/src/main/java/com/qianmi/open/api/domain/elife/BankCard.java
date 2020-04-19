package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 银行卡片信息
 *
 * @author auto
 * @since 2.0
 */
public class BankCard extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 银行卡号
	 */
	@ApiField("bankCardNo")
	private String bankCardNo;

	/**
	 * 银行英文简称
	 */
	@ApiField("bankCode")
	private String bankCode;

	/**
	 * 银行中文名称
	 */
	@ApiField("bankName")
	private String bankName;

	/**
	 * 第三方提供的银行logo地址
	 */
	@ApiField("logo")
	private String logo;

	/**
	 * 提示信息
	 */
	@ApiField("message")
	private String message;

	/**
	 * 卡片类型 DC 储蓄卡 CC 信用卡
	 */
	@ApiField("type")
	private String type;

	/**
	 * 卡片类型名称
	 */
	@ApiField("typeName")
	private String typeName;

	public String getBankCardNo() {
		return this.bankCardNo;
	}
	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}

	public String getBankCode() {
		return this.bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return this.bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getLogo() {
		return this.logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getTypeName() {
		return this.typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

}