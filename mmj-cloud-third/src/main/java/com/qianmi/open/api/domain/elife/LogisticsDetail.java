package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 物流详情
 *
 * @author auto
 * @since 2.0
 */
public class LogisticsDetail extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 详情
	 */
	@ApiField("context")
	private String context;

	/**
	 * 时间
	 */
	@ApiField("time")
	private String time;

	public String getContext() {
		return this.context;
	}
	public void setContext(String context) {
		this.context = context;
	}

	public String getTime() {
		return this.time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}