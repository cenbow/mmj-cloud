package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.Province;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.regions.list response.
 *
 * @author auto
 * @since 2.0
 */
public class RegionsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 省市地域信息
	 */
	@ApiListField("provinces")
	@ApiField("province")
	private List<Province> provinces;

	public void setProvinces(List<Province> provinces) {
		this.provinces = provinces;
	}
	public List<Province> getProvinces( ) {
		return this.provinces;
	}

}
