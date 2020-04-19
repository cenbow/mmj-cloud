package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmMobileFlowItemsList2Response;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.mobile.flow.items.list2 request
 *
 * @author auto
 * @since 1.0
 */
public class BmMobileFlowItemsList2Request implements QianmiRequest<BmMobileFlowItemsList2Response> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 流量大小，以M或者G为单位(后面不带B)，例如50M,1G
	 */
	private String flow;

	/** 
	 * 国内11位手机号码
	 */
	private String mobileNo;

	/** 
	 * 使用范围：p:省内；c:全国
	 */
	private String useScope;

	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getFlow() {
		return this.flow;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setUseScope(String useScope) {
		this.useScope = useScope;
	}
	public String getUseScope() {
		return this.useScope;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.mobile.flow.items.list2";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("flow", this.flow);
		txtParams.put("mobileNo", this.mobileNo);
		txtParams.put("useScope", this.useScope);
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

	public Class<BmMobileFlowItemsList2Response> getResponseClass() {
		return BmMobileFlowItemsList2Response.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(flow, "flow");
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}