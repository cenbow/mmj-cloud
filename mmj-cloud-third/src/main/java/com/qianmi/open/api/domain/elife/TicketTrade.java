package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 交易订单详情(主单)
 *
 * @author auto
 * @since 2.0
 */
public class TicketTrade extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 支付状态 0：未支付，1：已支付
	 */
	@ApiField("billState")
	private Integer billState;

	/**
	 * 支付时间
	 */
	@ApiField("billTime")
	private String billTime;

	/**
	 * 联系人
	 */
	@ApiField("contactName")
	private String contactName;

	/**
	 * 联系人电话
	 */
	@ApiField("contactTel")
	private String contactTel;

	/**
	 * 订单创建时间
	 */
	@ApiField("ctime")
	private String ctime;

	/**
	 * 完成时间
	 */
	@ApiField("etime")
	private String etime;

	/**
	 * 航段
	 */
	@ApiField("legs")
	private String legs;

	/**
	 * 票务类型  1:火车票  2:飞机票  3:汽车票
	 */
	@ApiField("orderType")
	private Integer orderType;

	/**
	 * 抵达站
	 */
	@ApiField("recevieStation")
	private String recevieStation;

	/**
	 * 到站时间
	 */
	@ApiField("recevieTime")
	private String recevieTime;

	/**
	 * 备注,预订失败时，会展示失败原因
	 */
	@ApiField("remark")
	private String remark;

	/**
	 * 出发站
	 */
	@ApiField("startStation")
	private String startStation;

	/**
	 * 发车时间
	 */
	@ApiField("startTime")
	private String startTime;

	/**
	 * 订单状态：0：预定完成,2：预定中，1：已完成，9：已取消
	 */
	@ApiField("state")
	private Integer state;

	/**
	 * 0：预定中,2：预定完成，1：已完成，9：已取消
	 */
	@ApiField("stateName")
	private String stateName;

	/**
	 * 商品子单
	 */
	@ApiListField("ticketOrders")
	@ApiField("ticketOrder")
	private List<TicketOrder> ticketOrders;

	/**
	 * 订单标题
	 */
	@ApiField("title")
	private String title;

	/**
	 * 票面价，商品面值
	 */
	@ApiField("totalFacePrice")
	private String totalFacePrice;

	/**
	 * 其它费用总和(火车票：如资源方附加费；飞机票：如燃油附加费)
	 */
	@ApiField("totalOtherFee")
	private String totalOtherFee;

	/**
	 * 实际支付的金额
	 */
	@ApiField("totalPayCash")
	private String totalPayCash;

	/**
	 * 实际票价，实际网点的采购价
	 */
	@ApiField("totalPurPrice")
	private String totalPurPrice;

	/**
	 * 实际差额退款金额
	 */
	@ApiField("totalRefundAmount")
	private String totalRefundAmount;

	/**
	 * 退款手续费总和
	 */
	@ApiField("totalRefundFee")
	private String totalRefundFee;

	/**
	 * 建议零售价
	 */
	@ApiField("totalSalePrice")
	private String totalSalePrice;

	/**
	 * 发货模板编号
	 */
	@ApiField("tplId")
	private String tplId;

	/**
	 * 订单编号
	 */
	@ApiField("tradeNo")
	private String tradeNo;

	/**
	 * 车次
	 */
	@ApiField("trainNo")
	private String trainNo;

	public Integer getBillState() {
		return this.billState;
	}
	public void setBillState(Integer billState) {
		this.billState = billState;
	}

	public String getBillTime() {
		return this.billTime;
	}
	public void setBillTime(String billTime) {
		this.billTime = billTime;
	}

	public String getContactName() {
		return this.contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return this.contactTel;
	}
	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public String getCtime() {
		return this.ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getEtime() {
		return this.etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getLegs() {
		return this.legs;
	}
	public void setLegs(String legs) {
		this.legs = legs;
	}

	public Integer getOrderType() {
		return this.orderType;
	}
	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public String getRecevieStation() {
		return this.recevieStation;
	}
	public void setRecevieStation(String recevieStation) {
		this.recevieStation = recevieStation;
	}

	public String getRecevieTime() {
		return this.recevieTime;
	}
	public void setRecevieTime(String recevieTime) {
		this.recevieTime = recevieTime;
	}

	public String getRemark() {
		return this.remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getStartStation() {
		return this.startStation;
	}
	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}

	public String getStartTime() {
		return this.startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
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

	public List<TicketOrder> getTicketOrders() {
		return this.ticketOrders;
	}
	public void setTicketOrders(List<TicketOrder> ticketOrders) {
		this.ticketOrders = ticketOrders;
	}

	public String getTitle() {
		return this.title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getTotalFacePrice() {
		return this.totalFacePrice;
	}
	public void setTotalFacePrice(String totalFacePrice) {
		this.totalFacePrice = totalFacePrice;
	}

	public String getTotalOtherFee() {
		return this.totalOtherFee;
	}
	public void setTotalOtherFee(String totalOtherFee) {
		this.totalOtherFee = totalOtherFee;
	}

	public String getTotalPayCash() {
		return this.totalPayCash;
	}
	public void setTotalPayCash(String totalPayCash) {
		this.totalPayCash = totalPayCash;
	}

	public String getTotalPurPrice() {
		return this.totalPurPrice;
	}
	public void setTotalPurPrice(String totalPurPrice) {
		this.totalPurPrice = totalPurPrice;
	}

	public String getTotalRefundAmount() {
		return this.totalRefundAmount;
	}
	public void setTotalRefundAmount(String totalRefundAmount) {
		this.totalRefundAmount = totalRefundAmount;
	}

	public String getTotalRefundFee() {
		return this.totalRefundFee;
	}
	public void setTotalRefundFee(String totalRefundFee) {
		this.totalRefundFee = totalRefundFee;
	}

	public String getTotalSalePrice() {
		return this.totalSalePrice;
	}
	public void setTotalSalePrice(String totalSalePrice) {
		this.totalSalePrice = totalSalePrice;
	}

	public String getTplId() {
		return this.tplId;
	}
	public void setTplId(String tplId) {
		this.tplId = tplId;
	}

	public String getTradeNo() {
		return this.tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getTrainNo() {
		return this.trainNo;
	}
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}

}