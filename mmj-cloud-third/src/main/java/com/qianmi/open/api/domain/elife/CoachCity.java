package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 汽车票城市信息
 *
 * @author auto
 * @since 2.0
 */
public class CoachCity extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 城市详情
	 */
	@ApiField("city")
	private CoachCityDetail city;

	/**
	 * 所在城市支持乡镇列表
	 */
	@ApiListField("counties")
	@ApiField("county")
	private List<CoachCountyDetail> counties;

	public CoachCityDetail getCity() {
		return this.city;
	}
	public void setCity(CoachCityDetail city) {
		this.city = city;
	}

	public List<CoachCountyDetail> getCounties() {
		return this.counties;
	}
	public void setCounties(List<CoachCountyDetail> counties) {
		this.counties = counties;
	}

}