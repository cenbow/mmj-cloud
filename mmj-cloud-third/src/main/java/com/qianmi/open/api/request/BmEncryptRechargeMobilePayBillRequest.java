package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmEncryptRechargeMobilePayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;

import java.util.Map;

/**
 * API: bm.elife.encrypt.recharge.mobile.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class BmEncryptRechargeMobilePayBillRequest implements QianmiRequest<BmEncryptRechargeMobilePayBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 加密后的请求串
	 */
	private String param;

	public void setParam(String param) {
		this.param = param;
	}
	public String getParam() {
		return this.param;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.encrypt.recharge.mobile.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("param", this.param);
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

	public Class<BmEncryptRechargeMobilePayBillResponse> getResponseClass() {
		return BmEncryptRechargeMobilePayBillResponse.class;
	}

	public void check() throws ApiRuleException {
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}