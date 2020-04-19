package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 卡密信息
 *
 * @author auto
 * @since 2.0
 */
public class CardPwd extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 卡号
	 */
	@ApiField("cardNo")
	private String cardNo;

	/**
	 * 密码
	 */
	@ApiField("cardPassword")
	private String cardPassword;

	/**
	 * 过期时间
	 */
	@ApiField("expireDate")
	private String expireDate;

	public String getCardNo() {
		return this.cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getCardPassword() {
		return this.cardPassword;
	}
	public void setCardPassword(String cardPassword) {
		this.cardPassword = cardPassword;
	}

	public String getExpireDate() {
		return this.expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}

}