package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 火车票站次信息
 *
 * @author auto
 * @since 2.0
 */
public class Trainline extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 当前终点站名称
	 */
	@ApiField("currentEndStationName")
	private String currentEndStationName;

	/**
	 * 当前起始站名称
	 */
	@ApiField("currentStartStationName")
	private String currentStartStationName;

	/**
	 * 抵达时间
	 */
	@ApiField("endTime")
	private String endTime;

	/**
	 * 耗时 单位：秒
	 */
	@ApiField("runTime")
	private String runTime;

	/**
	 * 发车时间
	 */
	@ApiField("startTime")
	private String startTime;

	/**
	 * 车次号
	 */
	@ApiField("trainNumber")
	private String trainNumber;

	/**
	 * 座位列表
	 */
	@ApiListField("trainSeats")
	@ApiField("trainSeat")
	private List<TrainSeat> trainSeats;

	/**
	 * 车次类型
	 */
	@ApiField("trainTypeName")
	private String trainTypeName;

	public String getCurrentEndStationName() {
		return this.currentEndStationName;
	}
	public void setCurrentEndStationName(String currentEndStationName) {
		this.currentEndStationName = currentEndStationName;
	}

	public String getCurrentStartStationName() {
		return this.currentStartStationName;
	}
	public void setCurrentStartStationName(String currentStartStationName) {
		this.currentStartStationName = currentStartStationName;
	}

	public String getEndTime() {
		return this.endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRunTime() {
		return this.runTime;
	}
	public void setRunTime(String runTime) {
		this.runTime = runTime;
	}

	public String getStartTime() {
		return this.startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getTrainNumber() {
		return this.trainNumber;
	}
	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}

	public List<TrainSeat> getTrainSeats() {
		return this.trainSeats;
	}
	public void setTrainSeats(List<TrainSeat> trainSeats) {
		this.trainSeats = trainSeats;
	}

	public String getTrainTypeName() {
		return this.trainTypeName;
	}
	public void setTrainTypeName(String trainTypeName) {
		this.trainTypeName = trainTypeName;
	}

}