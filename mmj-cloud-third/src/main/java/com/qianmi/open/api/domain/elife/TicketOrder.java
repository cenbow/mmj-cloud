package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 子单详情
 *
 * @author auto
 * @since 2.0
 */
public class TicketOrder extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 票面价格，商品面值
	 */
	@ApiField("facePrice")
	private String facePrice;

	/**
	 * 费用明细
	 */
	@ApiField("feeDetail")
	private String feeDetail;

	/**
	 * 证件号码
	 */
	@ApiField("idcardNo")
	private String idcardNo;

	/**
	 * 证件类型 0：身份证
	 */
	@ApiField("idcardType")
	private Integer idcardType;

	/**
	 * 保险信息
	 */
	@ApiField("insurance")
	private OInsurance insurance;

	/**
	 * 商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/**
	 * 商品单号
	 */
	@ApiField("orderNo")
	private String orderNo;

	/**
	 * 其它费用总和(火车票：如资源方附加费；飞机票：如燃油附加费)
	 */
	@ApiField("otherFee")
	private String otherFee;

	/**
	 * 乘客姓名
	 */
	@ApiField("passengerName")
	private String passengerName;

	/**
	 * 乘客号码
	 */
	@ApiField("passengerTel")
	private String passengerTel;

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
	 * 退款手续费
	 */
	@ApiField("refundFee")
	private String refundFee;

	/**
	 * 销售订单号, 以S开头
	 */
	@ApiField("saleOrderNo")
	private String saleOrderNo;

	/**
	 * 销售金额
	 */
	@ApiField("salePrice")
	private String salePrice;

	/**
	 * 出票以后的座次详情
	 */
	@ApiField("seatInfo")
	private String seatInfo;

	/**
	 * 座位类型名称
	 */
	@ApiField("seatName")
	private String seatName;

	/**
	 * 座位类型 0:二等座 1:一等座 2:特等座 3:商务座 4:无座 5:硬座 6:软座 7:硬卧 8:软卧; 9:高级软卧 10:火车其他座11:经济舱  12:头等舱; 21:汽车座位
	 */
	@ApiField("seatType")
	private Integer seatType;

	/**
	 * 订单展示状态 0:交易中,9:出票失败,1:出票成功,6:退票中,7:退票失败,10:已退票,11:已退款
	 */
	@ApiField("state")
	private Integer state;

	/**
	 * 订单展示状态 0:交易中,9:出票失败,1:出票成功,6:退票中,7:退票失败,10:已退票,11:已退款
	 */
	@ApiField("stateName")
	private String stateName;

	/**
	 * 车票号码
	 */
	@ApiField("ticketNo")
	private String ticketNo;

	/**
	 * 票务类型  0:成人票  1:儿童票  2:学生票
	 */
	@ApiField("ticketType")
	private Integer ticketType;

	/**
	 * 订单标题
	 */
	@ApiField("title")
	private String title;

	/**
	 * 实际差额退款金额
	 */
	@ApiField("totalRefundAmount")
	private String totalRefundAmount;

	public String getFacePrice() {
		return this.facePrice;
	}
	public void setFacePrice(String facePrice) {
		this.facePrice = facePrice;
	}

	public String getFeeDetail() {
		return this.feeDetail;
	}
	public void setFeeDetail(String feeDetail) {
		this.feeDetail = feeDetail;
	}

	public String getIdcardNo() {
		return this.idcardNo;
	}
	public void setIdcardNo(String idcardNo) {
		this.idcardNo = idcardNo;
	}

	public Integer getIdcardType() {
		return this.idcardType;
	}
	public void setIdcardType(Integer idcardType) {
		this.idcardType = idcardType;
	}

	public OInsurance getInsurance() {
		return this.insurance;
	}
	public void setInsurance(OInsurance insurance) {
		this.insurance = insurance;
	}

	public String getItemId() {
		return this.itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getOrderNo() {
		return this.orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getOtherFee() {
		return this.otherFee;
	}
	public void setOtherFee(String otherFee) {
		this.otherFee = otherFee;
	}

	public String getPassengerName() {
		return this.passengerName;
	}
	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getPassengerTel() {
		return this.passengerTel;
	}
	public void setPassengerTel(String passengerTel) {
		this.passengerTel = passengerTel;
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

	public String getRefundFee() {
		return this.refundFee;
	}
	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
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

	public String getSeatInfo() {
		return this.seatInfo;
	}
	public void setSeatInfo(String seatInfo) {
		this.seatInfo = seatInfo;
	}

	public String getSeatName() {
		return this.seatName;
	}
	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public Integer getSeatType() {
		return this.seatType;
	}
	public void setSeatType(Integer seatType) {
		this.seatType = seatType;
	}

	public Integer getState() {
		return this.state;
	}
	public void setState(Integer state) {
		this.state = state;
	}

	public String getStateName() {
		return this.stateName;
	}
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getTicketNo() {
		return this.ticketNo;
	}
	public void setTicketNo(String ticketNo) {
		this.ticketNo = ticketNo;
	}

	public Integer getTicketType() {
		return this.ticketType;
	}
	public void setTicketType(Integer ticketType) {
		this.ticketType = ticketType;
	}

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTotalRefundAmount() {
		return this.totalRefundAmount;
	}
	public void setTotalRefundAmount(String totalRefundAmount) {
		this.totalRefundAmount = totalRefundAmount;
	}

}