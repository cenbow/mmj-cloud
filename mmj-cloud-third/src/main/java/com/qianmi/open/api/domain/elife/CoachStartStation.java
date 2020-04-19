package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 汽车票出发站信息
 *
 * @author auto
 * @since 2.0
 */
public class CoachStartStation extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 出发城市列表
	 */
	@ApiListField("cities")
	@ApiField("coachCity")
	private List<CoachCity> cities;

	/**
	 * 所在省份中文名称
	 */
	@ApiField("province")
	private String province;

	public List<CoachCity> getCities() {
		return this.cities;
	}
	public void setCities(List<CoachCity> cities) {
		this.cities = cities;
	}

	public String getProvince() {
		return this.province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

}