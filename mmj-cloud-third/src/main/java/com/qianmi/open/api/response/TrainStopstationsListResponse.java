package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.TrainStopStation;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.train.stopstations.list response.
 *
 * @author auto
 * @since 2.0
 */
public class TrainStopstationsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 火车票经停信息列表
	 */
	@ApiListField("stopStationList")
	@ApiField("trainstopstations")
	private List<TrainStopStation> stopStationList;

	public void setStopStationList(List<TrainStopStation> stopStationList) {
		this.stopStationList = stopStationList;
	}
	public List<TrainStopStation> getStopStationList( ) {
		return this.stopStationList;
	}

}
