package com.qianmi.open.api.tool.util;

import java.util.HashMap;
import java.util.Map;

/**
 * API请求参数容器。
 */
public class RequestParametersHolder {

	private String requestUrl;
	private String responseBody;

	private QianmiHashMap protocalMustParams;
	private QianmiHashMap protocalOptParams;
	private QianmiHashMap applicationParams;

	public String getRequestUrl() {
		return this.requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getResponseBody() {
		return this.responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public QianmiHashMap getProtocalMustParams() {
		return this.protocalMustParams;
	}

	public void setProtocalMustParams(QianmiHashMap protocalMustParams) {
		this.protocalMustParams = protocalMustParams;
	}

	public QianmiHashMap getProtocalOptParams() {
		return this.protocalOptParams;
	}

	public void setProtocalOptParams(QianmiHashMap protocalOptParams) {
		this.protocalOptParams = protocalOptParams;
	}

	public QianmiHashMap getApplicationParams() {
		return this.applicationParams;
	}

	public void setApplicationParams(QianmiHashMap applicationParams) {
		this.applicationParams = applicationParams;
	}

	public Map<String, String> getAllParams() {
		Map<String, String> params = new HashMap<String, String>();
		if (protocalMustParams != null && !protocalMustParams.isEmpty()) {
			params.putAll(protocalMustParams);
		}
		if (protocalOptParams != null && !protocalOptParams.isEmpty()) {
			params.putAll(protocalOptParams);
		}
		if (applicationParams != null && !applicationParams.isEmpty()) {
			params.putAll(applicationParams);
		}
		return params;
	}

}
