package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmRechargeMobileCorrectResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.recharge.mobile.correct request
 *
 * @author auto
 * @since 1.0
 */
public class BmRechargeMobileCorrectRequest implements QianmiRequest<BmRechargeMobileCorrectResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 订单编号
	 */
	private String billId;

	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBillId() {
		return this.billId;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.recharge.mobile.correct";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("billId", this.billId);
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

	public Class<BmRechargeMobileCorrectResponse> getResponseClass() {
		return BmRechargeMobileCorrectResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(billId, "billId");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}