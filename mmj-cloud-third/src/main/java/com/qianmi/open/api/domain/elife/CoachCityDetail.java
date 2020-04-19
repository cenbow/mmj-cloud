package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 汽车票城市详情
 *
 * @author auto
 * @since 2.0
 */
public class CoachCityDetail extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 乡镇名编码
	 */
	@ApiField("code")
	private String code;

	/**
	 * 是否可以预定
	 */
	@ApiField("isCanOrder")
	private String isCanOrder;

	/**
	 * 乡镇名
	 */
	@ApiField("name")
	private String name;

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getIsCanOrder() {
		return this.isCanOrder;
	}
	public void setIsCanOrder(String isCanOrder) {
		this.isCanOrder = isCanOrder;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

}