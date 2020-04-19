package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.CoachStartStation;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.coach.startStations.list response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachStartStationsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 可用汽车出发站列表
	 */
	@ApiListField("stations")
	@ApiField("station")
	private List<CoachStartStation> stations;

	public void setStations(List<CoachStartStation> stations) {
		this.stations = stations;
	}
	public List<CoachStartStation> getStations( ) {
		return this.stations;
	}

}
