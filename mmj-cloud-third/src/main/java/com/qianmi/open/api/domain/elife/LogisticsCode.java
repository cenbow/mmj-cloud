package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 物流公司名称与编号
 *
 * @author auto
 * @since 2.0
 */
public class LogisticsCode extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 物流公司编号
	 */
	@ApiField("code")
	private String code;

	/**
	 * 快递公司名
	 */
	@ApiField("expName")
	private String expName;

	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getExpName() {
		return this.expName;
	}
	public void setExpName(String expName) {
		this.expName = expName;
	}

}