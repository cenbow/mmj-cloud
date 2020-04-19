package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.tools.time.get response.
 *
 * @author auto
 * @since 2.0
 */
public class ToolsTimeGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 千米开放平台系统当前时间
	 */
	@ApiField("time")
	private String time;

	public void setTime(String time) {
		this.time = time;
	}
	public String getTime( ) {
		return this.time;
	}

}
