package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Airline;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.air.seats.left response.
 *
 * @author auto
 * @since 2.0
 */
public class AirSeatsLeftResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 余票信息
	 */
	@ApiField("airline")
	private Airline airline;

	public void setAirline(Airline airline) {
		this.airline = airline;
	}
	public Airline getAirline( ) {
		return this.airline;
	}

}
