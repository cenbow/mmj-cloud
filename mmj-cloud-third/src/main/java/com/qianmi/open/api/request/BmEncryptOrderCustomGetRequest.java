package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmEncryptOrderCustomGetResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.encrypt.order.custom.get request
 *
 * @author auto
 * @since 1.0
 */
public class BmEncryptOrderCustomGetRequest implements QianmiRequest<BmEncryptOrderCustomGetResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 外部订单编号
	 */
	private String outerTid;

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.encrypt.order.custom.get";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("outerTid", this.outerTid);
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

	public Class<BmEncryptOrderCustomGetResponse> getResponseClass() {
		return BmEncryptOrderCustomGetResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(outerTid, "outerTid");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}