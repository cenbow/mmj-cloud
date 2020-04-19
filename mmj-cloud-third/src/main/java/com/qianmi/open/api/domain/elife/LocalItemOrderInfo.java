package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 本地商品订单
 *
 * @author auto
 * @since 2.0
 */
public class LocalItemOrderInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 卡密信息
	 */
	@ApiListField("cardPasswords")
	@ApiField("cardPassword")
	private List<CardPwd> cardPasswords;

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
	 * 购买数量
	 */
	@ApiField("itemNum")
	private Integer itemNum;

	/**
	 * 订单编号
	 */
	@ApiField("orderId")
	private String orderId;

	public List<CardPwd> getCardPasswords() {
		return this.cardPasswords;
	}
	public void setCardPasswords(List<CardPwd> cardPasswords) {
		this.cardPasswords = cardPasswords;
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

	public Integer getItemNum() {
		return this.itemNum;
	}
	public void setItemNum(Integer itemNum) {
		this.itemNum = itemNum;
	}

	public String getOrderId() {
		return this.orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

}