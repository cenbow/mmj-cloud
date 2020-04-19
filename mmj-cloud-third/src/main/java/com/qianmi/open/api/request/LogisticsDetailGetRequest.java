package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.LogisticsDetailGetResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.logistics.detail.get request
 *
 * @author auto
 * @since 1.0
 */
public class LogisticsDetailGetRequest implements QianmiRequest<LogisticsDetailGetResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 物流公司编号
	 */
	private String code;

	/** 
	 * 物流单号
	 */
	private String expNo;

	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return this.code;
	}

	public void setExpNo(String expNo) {
		this.expNo = expNo;
	}
	public String getExpNo() {
		return this.expNo;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.logistics.detail.get";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("code", this.code);
		txtParams.put("expNo", this.expNo);
		if(udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public void putOtherTextParam(String key, String value) {
		if(this.udfParams == null) {
			this.udfParams = new QianmiHashMap();
		}
		this.udfParams.put(key, value);
	}

	public Class<LogisticsDetailGetResponse> getResponseClass() {
		return LogisticsDetailGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(code, "code");
		RequestCheckUtils.checkNotEmpty(expNo, "expNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}