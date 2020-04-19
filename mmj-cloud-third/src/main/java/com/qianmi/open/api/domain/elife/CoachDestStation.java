package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 汽车票抵达站详情
 *
 * @author auto
 * @since 2.0
 */
public class CoachDestStation extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 城镇编码
	 */
	@ApiField("code")
	private String code;

	/**
	 * 城镇名
	 */
	@ApiField("name")
	private String name;

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

}