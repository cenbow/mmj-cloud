package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * 标准商品省属性信息
 *
 * @author auto
 * @since 2.0
 */
public class Province extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 市属性信息
	 */
	@ApiListField("cities")
	@ApiField("city")
	private List<City> cities;

	/**
	 * 省属性v编号
	 */
	@ApiField("vid")
	private String vid;

	/**
	 * 省属性名称
	 */
	@ApiField("vname")
	private String vname;

	public List<City> getCities() {
		return this.cities;
	}
	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public String getVid() {
		return this.vid;
	}
	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getVname() {
		return this.vname;
	}
	public void setVname(String vname) {
		this.vname = vname;
	}

}