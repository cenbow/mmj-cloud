package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Station;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.train.stations.list response.
 *
 * @author auto
 * @since 2.0
 */
public class TrainStationsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 火车票所有站点返回信息
	 */
	@ApiListField("stations")
	@ApiField("station")
	private List<Station> stations;

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
	public List<Station> getStations( ) {
		return this.stations;
	}

}
