package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 汽车票车次信息
 *
 * @author auto
 * @since 2.0
 */
public class CoachLine extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 到达站
	 */
	@ApiField("arrStation")
	private String arrStation;

	/**
	 * 车次
	 */
	@ApiField("coachNO")
	private String coachNO;

	/**
	 * 车型
	 */
	@ApiField("coachType")
	private String coachType;

	/**
	 * 出发地
	 */
	@ApiField("departure")
	private String departure;

	/**
	 * 到达地
	 */
	@ApiField("destination")
	private String destination;

	/**
	 * 发车日期
	 */
	@ApiField("dptDate")
	private String dptDate;

	/**
	 * 发车日期时间
	 */
	@ApiField("dptDateTime")
	private String dptDateTime;

	/**
	 * 始发站
	 */
	@ApiField("dptStation")
	private String dptStation;

	/**
	 * 发车时间
	 */
	@ApiField("dptTime")
	private String dptTime;

	/**
	 * 扩展字段1
	 */
	@ApiField("exData1")
	private String exData1;

	/**
	 * 扩展字段2
	 */
	@ApiField("exData2")
	private String exData2;

	/**
	 * 手续费
	 */
	@ApiField("fee")
	private String fee;

	/**
	 * 是否可预订
	 */
	@ApiField("isReservable")
	private String isReservable;

	/**
	 * 操作类型(0:预售 ,1:订票)
	 */
	@ApiField("optionType")
	private String optionType;

	/**
	 * 站点编号
	 */
	@ApiField("stationCode")
	private String stationCode;

	/**
	 * 余票
	 */
	@ApiField("ticketLeft")
	private String ticketLeft;

	/**
	 * 票价
	 */
	@ApiField("ticketPrice")
	private String ticketPrice;

	public String getArrStation() {
		return this.arrStation;
	}
	public void setArrStation(String arrStation) {
		this.arrStation = arrStation;
	}

	public String getCoachNO() {
		return this.coachNO;
	}
	public void setCoachNO(String coachNO) {
		this.coachNO = coachNO;
	}

	public String getCoachType() {
		return this.coachType;
	}
	public void setCoachType(String coachType) {
		this.coachType = coachType;
	}

	public String getDeparture() {
		return this.departure;
	}
	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getDestination() {
		return this.destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDptDate() {
		return this.dptDate;
	}
	public void setDptDate(String dptDate) {
		this.dptDate = dptDate;
	}

	public String getDptDateTime() {
		return this.dptDateTime;
	}
	public void setDptDateTime(String dptDateTime) {
		this.dptDateTime = dptDateTime;
	}

	public String getDptStation() {
		return this.dptStation;
	}
	public void setDptStation(String dptStation) {
		this.dptStation = dptStation;
	}

	public String getDptTime() {
		return this.dptTime;
	}
	public void setDptTime(String dptTime) {
		this.dptTime = dptTime;
	}

	public String getExData1() {
		return this.exData1;
	}
	public void setExData1(String exData1) {
		this.exData1 = exData1;
	}

	public String getExData2() {
		return this.exData2;
	}
	public void setExData2(String exData2) {
		this.exData2 = exData2;
	}

	public String getFee() {
		return this.fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}

	public String getIsReservable() {
		return this.isReservable;
	}
	public void setIsReservable(String isReservable) {
		this.isReservable = isReservable;
	}

	public String getOptionType() {
		return this.optionType;
	}
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}

	public String getStationCode() {
		return this.stationCode;
	}
	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}

	public String getTicketLeft() {
		return this.ticketLeft;
	}
	public void setTicketLeft(String ticketLeft) {
		this.ticketLeft = ticketLeft;
	}

	public String getTicketPrice() {
		return this.ticketPrice;
	}
	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

}