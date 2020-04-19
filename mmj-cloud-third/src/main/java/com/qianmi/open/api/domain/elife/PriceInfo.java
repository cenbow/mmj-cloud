package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 价格信息
 *
 * @author auto
 * @since 2.0
 */
public class PriceInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 建议零售价
	 */
	@ApiField("advicePrice")
	private String advicePrice;

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

	public String getAdvicePrice() {
		return this.advicePrice;
	}
	public void setAdvicePrice(String advicePrice) {
		this.advicePrice = advicePrice;
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

}