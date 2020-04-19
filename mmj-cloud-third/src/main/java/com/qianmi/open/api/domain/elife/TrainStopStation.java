package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 经停站信息
 *
 * @author auto
 * @since 2.0
 */
public class TrainStopStation extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 到达该站点时间
	 */
	@ApiField("arrivalTime")
	private String arrivalTime;

	/**
	 * 该站点出发时间
	 */
	@ApiField("departureTime")
	private String departureTime;

	/**
	 * 公里数（暂无数据）
	 */
	@ApiField("miles")
	private String miles;

	/**
	 * 车次序号（可做排序用）
	 */
	@ApiField("serialNumber")
	private String serialNumber;

	/**
	 * 站点名称（中文）
	 */
	@ApiField("station")
	private String station;

	/**
	 * 停留时长（分钟）（首末站无时间，为----）
	 */
	@ApiField("stayTimeSpan")
	private String stayTimeSpan;

	public String getArrivalTime() {
		return this.arrivalTime;
	}
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getDepartureTime() {
		return this.departureTime;
	}
	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getMiles() {
		return this.miles;
	}
	public void setMiles(String miles) {
		this.miles = miles;
	}

	public String getSerialNumber() {
		return this.serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getStation() {
		return this.station;
	}
	public void setStation(String station) {
		this.station = station;
	}

	public String getStayTimeSpan() {
		return this.stayTimeSpan;
	}
	public void setStayTimeSpan(String stayTimeSpan) {
		this.stayTimeSpan = stayTimeSpan;
	}

}