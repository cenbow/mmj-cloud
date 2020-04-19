package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 火车票座位详情信息
 *
 * @author auto
 * @since 2.0
 */
public class TrainSeat extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 座位余票
	 */
	@ApiField("remainderTrainTickets")
	private String remainderTrainTickets;

	/**
	 * 座位类型
	 */
	@ApiField("seatId")
	private String seatId;

	/**
	 * 座位类型名称
	 */
	@ApiField("seatName")
	private String seatName;

	/**
	 * 座位票面价
	 */
	@ApiField("seatPrice")
	private String seatPrice;

	public String getRemainderTrainTickets() {
		return this.remainderTrainTickets;
	}
	public void setRemainderTrainTickets(String remainderTrainTickets) {
		this.remainderTrainTickets = remainderTrainTickets;
	}

	public String getSeatId() {
		return this.seatId;
	}
	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}

	public String getSeatName() {
		return this.seatName;
	}
	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public String getSeatPrice() {
		return this.seatPrice;
	}
	public void setSeatPrice(String seatPrice) {
		this.seatPrice = seatPrice;
	}

}