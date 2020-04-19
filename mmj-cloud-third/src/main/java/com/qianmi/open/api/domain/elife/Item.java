package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 商品
 *
 * @author auto
 * @since 2.0
 */
public class Item extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 建议零售价
	 */
	@ApiField("advicePrice")
	private String advicePrice;

	/**
	 * 建议零售价设价表达式
	 */
	@ApiField("advicePriceExpression")
	private String advicePriceExpression;

	/**
	 * 建议零售价设价方式
	 */
	@ApiField("advicePriceOpt")
	private String advicePriceOpt;

	/**
	 * 面值名称
	 */
	@ApiField("faceName")
	private String faceName;

	/**
	 * 面值
	 */
	@ApiField("facePriceValue")
	private String facePriceValue;

	/**
	 * 商品进价
	 */
	@ApiField("inPrice")
	private String inPrice;

	/**
	 * 进价设价表达式
	 */
	@ApiField("inPriceExpression")
	private String inPriceExpression;

	/**
	 * 进价设价方式
	 */
	@ApiField("inPriceOpt")
	private String inPriceOpt;

	/**
	 * 标准商品对应的小类id
	 */
	@ApiField("itemClassId")
	private String itemClassId;

	/**
	 * 标准商品ID
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 标准商品名称
	 */
	@ApiField("itemName")
	private String itemName;

	/**
	 * 购买可选数量 1-10 or 1,5,10,20,50
	 */
	@ApiField("numberChoice")
	private String numberChoice;

	/**
	 * 标准类目编号
	 */
	@ApiField("productCateId")
	private String productCateId;

	/**
	 * 充值模板编号
	 */
	@ApiField("rechargeTplid")
	private String rechargeTplid;

	/**
	 * 是否支持冲正 1 支持 0 不支持
	 */
	@ApiField("reverseFlag")
	private String reverseFlag;

	/**
	 * 商品库存
	 */
	@ApiField("stock")
	private String stock;

	/**
	 * 真实供货商id
	 */
	@ApiField("supUserId")
	private String supUserId;

	/**
	 * 供货商备注
	 */
	@ApiField("supplyRemark")
	private String supplyRemark;

	/**
	 * 货源类型 1自供货 2小SUP 3其他供货
	 */
	@ApiField("supplyType")
	private String supplyType;

	/**
	 * 商品对应的模板id
	 */
	@ApiField("tplId")
	private String tplId;

	public String getAdvicePrice() {
		return this.advicePrice;
	}
	public void setAdvicePrice(String advicePrice) {
		this.advicePrice = advicePrice;
	}

	public String getAdvicePriceExpression() {
		return this.advicePriceExpression;
	}
	public void setAdvicePriceExpression(String advicePriceExpression) {
		this.advicePriceExpression = advicePriceExpression;
	}

	public String getAdvicePriceOpt() {
		return this.advicePriceOpt;
	}
	public void setAdvicePriceOpt(String advicePriceOpt) {
		this.advicePriceOpt = advicePriceOpt;
	}

	public String getFaceName() {
		return this.faceName;
	}
	public void setFaceName(String faceName) {
		this.faceName = faceName;
	}

	public String getFacePriceValue() {
		return this.facePriceValue;
	}
	public void setFacePriceValue(String facePriceValue) {
		this.facePriceValue = facePriceValue;
	}

	public String getInPrice() {
		return this.inPrice;
	}
	public void setInPrice(String inPrice) {
		this.inPrice = inPrice;
	}

	public String getInPriceExpression() {
		return this.inPriceExpression;
	}
	public void setInPriceExpression(String inPriceExpression) {
		this.inPriceExpression = inPriceExpression;
	}

	public String getInPriceOpt() {
		return this.inPriceOpt;
	}
	public void setInPriceOpt(String inPriceOpt) {
		this.inPriceOpt = inPriceOpt;
	}

	public String getItemClassId() {
		return this.itemClassId;
	}
	public void setItemClassId(String itemClassId) {
		this.itemClassId = itemClassId;
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

	public String getNumberChoice() {
		return this.numberChoice;
	}
	public void setNumberChoice(String numberChoice) {
		this.numberChoice = numberChoice;
	}

	public String getProductCateId() {
		return this.productCateId;
	}
	public void setProductCateId(String productCateId) {
		this.productCateId = productCateId;
	}

	public String getRechargeTplid() {
		return this.rechargeTplid;
	}
	public void setRechargeTplid(String rechargeTplid) {
		this.rechargeTplid = rechargeTplid;
	}

	public String getReverseFlag() {
		return this.reverseFlag;
	}
	public void setReverseFlag(String reverseFlag) {
		this.reverseFlag = reverseFlag;
	}

	public String getStock() {
		return this.stock;
	}
	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getSupUserId() {
		return this.supUserId;
	}
	public void setSupUserId(String supUserId) {
		this.supUserId = supUserId;
	}

	public String getSupplyRemark() {
		return this.supplyRemark;
	}
	public void setSupplyRemark(String supplyRemark) {
		this.supplyRemark = supplyRemark;
	}

	public String getSupplyType() {
		return this.supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getTplId() {
		return this.tplId;
	}
	public void setTplId(String tplId) {
		this.tplId = tplId;
	}

}