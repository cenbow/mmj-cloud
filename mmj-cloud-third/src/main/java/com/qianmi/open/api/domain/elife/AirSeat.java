package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 飞机航班舱位
 *
 * @author auto
 * @since 2.0
 */
public class AirSeat extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 航空公司编号，二字码
	 */
	@ApiField("airlineCode")
	private String airlineCode;

	/**
	 * 改期临界时间之后需要的改期费用
	 */
	@ApiField("changePercentAfter")
	private Double changePercentAfter;

	/**
	 * 改期临界时间之前需要的改期费用
	 */
	@ApiField("changePercentBefore")
	private Double changePercentBefore;

	/**
	 * 改期费提示说明
	 */
	@ApiField("changeStipulate")
	private String changeStipulate;

	/**
	 * 改期临界时间
	 */
	@ApiField("changeTimePoint")
	private Integer changeTimePoint;

	/**
	 * 返现
	 */
	@ApiField("commisionMoney")
	private Double commisionMoney;

	/**
	 * 政策返点
	 */
	@ApiField("commisionPoint")
	private String commisionPoint;

	/**
	 * 折扣
	 */
	@ApiField("discount")
	private Double discount;

	/**
	 * 标识 退改签政策 是否在后台已经查询到
	 */
	@ApiField("hasQueryedStipulate")
	private boolean hasQueryedStipulate;

	/**
	 * 票面价，即界面展示价格，打折后价格
	 */
	@ApiField("parPrice")
	private Integer parPrice;

	/**
	 * 政策的ID
	 */
	@ApiField("policyId")
	private Integer policyId;

	/**
	 * 起飞后退票费率
	 */
	@ApiField("refundPercentAfter")
	private Double refundPercentAfter;

	/**
	 * 起飞前退票费率
	 */
	@ApiField("refundPercentBefore")
	private Double refundPercentBefore;

	/**
	 * 退票规定
	 */
	@ApiField("refundStipulate")
	private String refundStipulate;

	/**
	 * 起飞前退票时间点
	 */
	@ApiField("refundTimePoint")
	private Integer refundTimePoint;

	/**
	 * 舱位码
	 */
	@ApiField("seatCode")
	private String seatCode;

	/**
	 * 舱位  （经济舱;  超级经济舱;   头等舱  ）
	 */
	@ApiField("seatMsg")
	private String seatMsg;

	/**
	 * 当可用坐席大于9时用"A"表示；当小于等于9时，显示确切的可用坐席数；
	 */
	@ApiField("seatStatus")
	private String seatStatus;

	/**
	 * 服务级别 （若是要取舱位类型，直接取seatMsg，不要取次字段，保存的时候也是保存seatMsg）
	 */
	@ApiField("serviceLevel")
	private String serviceLevel;

	/**
	 * 成人的结算价，实际需要支付的价格
	 */
	@ApiField("settlePrice")
	private Double settlePrice;

	/**
	 * CY充值模板的舱位key
	 */
	@ApiField("verifyKey")
	private String verifyKey;

	/**
	 * 废票时间
	 */
	@ApiField("vtWorkTime")
	private String vtWorkTime;

	/**
	 * 供应商工作时间
	 */
	@ApiField("workTime")
	private String workTime;

	public String getAirlineCode() {
		return this.airlineCode;
	}
	public void setAirlineCode(String airlineCode) {
		this.airlineCode = airlineCode;
	}

	public Double getChangePercentAfter() {
		return this.changePercentAfter;
	}
	public void setChangePercentAfter(Double changePercentAfter) {
		this.changePercentAfter = changePercentAfter;
	}

	public Double getChangePercentBefore() {
		return this.changePercentBefore;
	}
	public void setChangePercentBefore(Double changePercentBefore) {
		this.changePercentBefore = changePercentBefore;
	}

	public String getChangeStipulate() {
		return this.changeStipulate;
	}
	public void setChangeStipulate(String changeStipulate) {
		this.changeStipulate = changeStipulate;
	}

	public Integer getChangeTimePoint() {
		return this.changeTimePoint;
	}
	public void setChangeTimePoint(Integer changeTimePoint) {
		this.changeTimePoint = changeTimePoint;
	}

	public Double getCommisionMoney() {
		return this.commisionMoney;
	}
	public void setCommisionMoney(Double commisionMoney) {
		this.commisionMoney = commisionMoney;
	}

	public String getCommisionPoint() {
		return this.commisionPoint;
	}
	public void setCommisionPoint(String commisionPoint) {
		this.commisionPoint = commisionPoint;
	}

	public Double getDiscount() {
		return this.discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public boolean getHasQueryedStipulate() {
		return this.hasQueryedStipulate;
	}
	public void setHasQueryedStipulate(boolean hasQueryedStipulate) {
		this.hasQueryedStipulate = hasQueryedStipulate;
	}

	public Integer getParPrice() {
		return this.parPrice;
	}
	public void setParPrice(Integer parPrice) {
		this.parPrice = parPrice;
	}

	public Integer getPolicyId() {
		return this.policyId;
	}
	public void setPolicyId(Integer policyId) {
		this.policyId = policyId;
	}

	public Double getRefundPercentAfter() {
		return this.refundPercentAfter;
	}
	public void setRefundPercentAfter(Double refundPercentAfter) {
		this.refundPercentAfter = refundPercentAfter;
	}

	public Double getRefundPercentBefore() {
		return this.refundPercentBefore;
	}
	public void setRefundPercentBefore(Double refundPercentBefore) {
		this.refundPercentBefore = refundPercentBefore;
	}

	public String getRefundStipulate() {
		return this.refundStipulate;
	}
	public void setRefundStipulate(String refundStipulate) {
		this.refundStipulate = refundStipulate;
	}

	public Integer getRefundTimePoint() {
		return this.refundTimePoint;
	}
	public void setRefundTimePoint(Integer refundTimePoint) {
		this.refundTimePoint = refundTimePoint;
	}

	public String getSeatCode() {
		return this.seatCode;
	}
	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}

	public String getSeatMsg() {
		return this.seatMsg;
	}
	public void setSeatMsg(String seatMsg) {
		this.seatMsg = seatMsg;
	}

	public String getSeatStatus() {
		return this.seatStatus;
	}
	public void setSeatStatus(String seatStatus) {
		this.seatStatus = seatStatus;
	}

	public String getServiceLevel() {
		return this.serviceLevel;
	}
	public void setServiceLevel(String serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public Double getSettlePrice() {
		return this.settlePrice;
	}
	public void setSettlePrice(Double settlePrice) {
		this.settlePrice = settlePrice;
	}

	public String getVerifyKey() {
		return this.verifyKey;
	}
	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}

	public String getVtWorkTime() {
		return this.vtWorkTime;
	}
	public void setVtWorkTime(String vtWorkTime) {
		this.vtWorkTime = vtWorkTime;
	}

	public String getWorkTime() {
		return this.workTime;
	}
	public void setWorkTime(String workTime) {
		this.workTime = workTime;
	}

}