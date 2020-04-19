package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Airline;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.air.lines.list response.
 *
 * @author auto
 * @since 2.0
 */
public class AirLinesListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 飞机航线列表
	 */
	@ApiListField("airlines")
	@ApiField("airline")
	private List<Airline> airlines;

	public void setAirlines(List<Airline> airlines) {
		this.airlines = airlines;
	}
	public List<Airline> getAirlines( ) {
		return this.airlines;
	}

}
