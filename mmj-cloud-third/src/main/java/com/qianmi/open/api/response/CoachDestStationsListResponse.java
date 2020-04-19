package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.CoachDestStation;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.coach.destStations.list response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachDestStationsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 可用汽车到达站列表
	 */
	@ApiListField("stations")
	@ApiField("station")
	private List<CoachDestStation> stations;

	public void setStations(List<CoachDestStation> stations) {
		this.stations = stations;
	}
	public List<CoachDestStation> getStations( ) {
		return this.stations;
	}

}
