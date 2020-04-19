package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 订单详情-用于展示订单详情
 *
 * @author auto
 * @since 2.0
 */
public class OrderDetailInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 支付金额
	 */
	@ApiField("actPrice")
	private String actPrice;

	/**
	 * 魔兽账号
	 */
	@ApiField("battleAccount")
	private String battleAccount;

	/**
	 * 订单编号
	 */
	@ApiField("billId")
	private String billId;

	/**
	 * 预定人地址
	 */
	@ApiField("buyerAddress")
	private String buyerAddress;

	/**
	 * 备注信息
	 */
	@ApiField("buyerRemark")
	private String buyerRemark;

	/**
	 * 预定电话
	 */
	@ApiField("buyerTel")
	private String buyerTel;

	/**
	 * 卡号
	 */
	@ApiField("cardNo")
	private String cardNo;

	/**
	 * 卡密信息
	 */
	@ApiListField("cardPasswords")
	@ApiField("cardPassword")
	private List<CardPwd> cardPasswords;

	/**
	 * 商品类型：1">实物商品2">直充商品3">卡密商品4">话费充值6">支付和金币充值
	 */
	@ApiField("classType")
	private String classType;

	/**
	 * 还款人姓名
	 */
	@ApiField("customerName")
	private String customerName;

	/**
	 * 还款人手机号码
	 */
	@ApiField("customerTel")
	private String customerTel;

	/**
	 * 支付宝的充值码
	 */
	@ApiField("extPay")
	private String extPay;

	/**
	 * 商品面值
	 */
	@ApiField("facePrice")
	private String facePrice;

	/**
	 * 游戏区
	 */
	@ApiField("gameArea")
	private String gameArea;

	/**
	 * 游戏服
	 */
	@ApiField("gameServer")
	private String gameServer;

	/**
	 * 身份证地址
	 */
	@ApiField("idAddress")
	private String idAddress;

	/**
	 * 身份证反面照片地址
	 */
	@ApiField("idBackImage")
	private String idBackImage;

	/**
	 * 身份证正面照片地址
	 */
	@ApiField("idFrontImage")
	private String idFrontImage;

	/**
	 * 身份证号码
	 */
	@ApiField("idNo")
	private String idNo;

	/**
	 * 是否批发<span style="color:red"><系统写入></span> 1:批发,0:零售
	 */
	@ApiField("isBatch")
	private Integer isBatch;

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
	 * 号卡开卡字段
	 */
	@ApiField("minConsume")
	private String minConsume;

	/**
	 * 手机号码
	 */
	@ApiField("mobileNo")
	private String mobileNo;

	/**
	 * 开户行
	 */
	@ApiField("openBank")
	private String openBank;

	/**
	 * 处理时间
	 */
	@ApiField("operateTime")
	private String operateTime;

	/**
	 * 总进价 (订单总成本)
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
	 * 外部订单号
	 */
	@ApiField("outerTid")
	private String outerTid;

	/**
	 * 套餐名称
	 */
	@ApiField("packageName")
	private String packageName;

	/**
	 * 订单付款状态 0 未付款1 已付款
	 */
	@ApiField("payState")
	private String payState;

	/**
	 * 预存款
	 */
	@ApiField("preStore")
	private String preStore;

	/**
	 * 预订人
	 */
	@ApiField("purchaser")
	private String purchaser;

	/**
	 * 手机号码
	 */
	@ApiField("receiveMobile")
	private String receiveMobile;

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
	 * 备注
	 */
	@ApiField("remark")
	private String remark;

	/**
	 * 销售额
	 */
	@ApiField("saleAmount")
	private String saleAmount;

	/**
	 * sim卡ID
	 */
	@ApiField("simCardId")
	private String simCardId;

	/**
	 * 供应商联系人姓名
	 */
	@ApiField("supContactUser")
	private String supContactUser;

	/**
	 * 供应商编号
	 */
	@ApiField("supId")
	private String supId;

	/**
	 * 供应商手机号
	 */
	@ApiField("supMobile")
	private String supMobile;

	/**
	 * 供应商用户名
	 */
	@ApiField("supNickName")
	private String supNickName;

	/**
	 * 供应商QQ
	 */
	@ApiField("supQq")
	private String supQq;

	/**
	 * 模板编号
	 */
	@ApiField("tplId")
	private String tplId;

	/**
	 * 订单所有人
	 */
	@ApiField("userCode")
	private String userCode;

	public String getActPrice() {
		return this.actPrice;
	}
	public void setActPrice(String actPrice) {
		this.actPrice = actPrice;
	}

	public String getBattleAccount() {
		return this.battleAccount;
	}
	public void setBattleAccount(String battleAccount) {
		this.battleAccount = battleAccount;
	}

	public String getBillId() {
		return this.billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}

	public String getBuyerAddress() {
		return this.buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	public String getBuyerRemark() {
		return this.buyerRemark;
	}
	public void setBuyerRemark(String buyerRemark) {
		this.buyerRemark = buyerRemark;
	}

	public String getBuyerTel() {
		return this.buyerTel;
	}
	public void setBuyerTel(String buyerTel) {
		this.buyerTel = buyerTel;
	}

	public String getCardNo() {
		return this.cardNo;
	}
	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public List<CardPwd> getCardPasswords() {
		return this.cardPasswords;
	}
	public void setCardPasswords(List<CardPwd> cardPasswords) {
		this.cardPasswords = cardPasswords;
	}

	public String getClassType() {
		return this.classType;
	}
	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getCustomerName() {
		return this.customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerTel() {
		return this.customerTel;
	}
	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}

	public String getExtPay() {
		return this.extPay;
	}
	public void setExtPay(String extPay) {
		this.extPay = extPay;
	}

	public String getFacePrice() {
		return this.facePrice;
	}
	public void setFacePrice(String facePrice) {
		this.facePrice = facePrice;
	}

	public String getGameArea() {
		return this.gameArea;
	}
	public void setGameArea(String gameArea) {
		this.gameArea = gameArea;
	}

	public String getGameServer() {
		return this.gameServer;
	}
	public void setGameServer(String gameServer) {
		this.gameServer = gameServer;
	}

	public String getIdAddress() {
		return this.idAddress;
	}
	public void setIdAddress(String idAddress) {
		this.idAddress = idAddress;
	}

	public String getIdBackImage() {
		return this.idBackImage;
	}
	public void setIdBackImage(String idBackImage) {
		this.idBackImage = idBackImage;
	}

	public String getIdFrontImage() {
		return this.idFrontImage;
	}
	public void setIdFrontImage(String idFrontImage) {
		this.idFrontImage = idFrontImage;
	}

	public String getIdNo() {
		return this.idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public Integer getIsBatch() {
		return this.isBatch;
	}
	public void setIsBatch(Integer isBatch) {
		this.isBatch = isBatch;
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

	public String getMinConsume() {
		return this.minConsume;
	}
	public void setMinConsume(String minConsume) {
		this.minConsume = minConsume;
	}

	public String getMobileNo() {
		return this.mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getOpenBank() {
		return this.openBank;
	}
	public void setOpenBank(String openBank) {
		this.openBank = openBank;
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

	public String getOuterTid() {
		return this.outerTid;
	}
	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}

	public String getPackageName() {
		return this.packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPayState() {
		return this.payState;
	}
	public void setPayState(String payState) {
		this.payState = payState;
	}

	public String getPreStore() {
		return this.preStore;
	}
	public void setPreStore(String preStore) {
		this.preStore = preStore;
	}

	public String getPurchaser() {
		return this.purchaser;
	}
	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}

	public String getReceiveMobile() {
		return this.receiveMobile;
	}
	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
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

	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSaleAmount() {
		return this.saleAmount;
	}
	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}

	public String getSimCardId() {
		return this.simCardId;
	}
	public void setSimCardId(String simCardId) {
		this.simCardId = simCardId;
	}

	public String getSupContactUser() {
		return this.supContactUser;
	}
	public void setSupContactUser(String supContactUser) {
		this.supContactUser = supContactUser;
	}

	public String getSupId() {
		return this.supId;
	}
	public void setSupId(String supId) {
		this.supId = supId;
	}

	public String getSupMobile() {
		return this.supMobile;
	}
	public void setSupMobile(String supMobile) {
		this.supMobile = supMobile;
	}

	public String getSupNickName() {
		return this.supNickName;
	}
	public void setSupNickName(String supNickName) {
		this.supNickName = supNickName;
	}

	public String getSupQq() {
		return this.supQq;
	}
	public void setSupQq(String supQq) {
		this.supQq = supQq;
	}

	public String getTplId() {
		return this.tplId;
	}
	public void setTplId(String tplId) {
		this.tplId = tplId;
	}

	public String getUserCode() {
		return this.userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

}