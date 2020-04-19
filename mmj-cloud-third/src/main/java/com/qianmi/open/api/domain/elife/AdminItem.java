package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 上级对接的标准商品信息
 *
 * @author auto
 * @since 2.0
 */
public class AdminItem extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 标准商品进价
	 */
	@ApiField("inPrice")
	private String inPrice;

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

}