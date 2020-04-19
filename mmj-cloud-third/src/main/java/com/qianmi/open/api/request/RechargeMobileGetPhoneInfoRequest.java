package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.RechargeMobileGetPhoneInfoResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.recharge.mobile.getPhoneInfo request
 *
 * @author auto
 * @since 1.0
 */
public class RechargeMobileGetPhoneInfoRequest implements QianmiRequest<RechargeMobileGetPhoneInfoResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 *  所在市名称
	 */
	private String city;

	/** 
	 *  运营商名称
	 */
	private String operator;

	/** 
	 * 固话或者手机号码
	 */
	private String phoneNo;

	/** 
	 *  所在省名称
	 */
	private String province;

	/** 
	 * 返回值内容类型 area:仅返回区域信息 detail:仅返回账户详情(包含余额) all:返回所有信息,默认返回区域
	 */
	private String respType;

	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getOperator() {
		return this.operator;
	}

	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}
	public String getPhoneNo() {
		return this.phoneNo;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince() {
		return this.province;
	}

	public void setRespType(String respType) {
		this.respType = respType;
	}
	public String getRespType() {
		return this.respType;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.recharge.mobile.getPhoneInfo";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("city", this.city);
		txtParams.put("operator", this.operator);
		txtParams.put("phoneNo", this.phoneNo);
		txtParams.put("province", this.province);
		txtParams.put("respType", this.respType);
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

	public Class<RechargeMobileGetPhoneInfoResponse> getResponseClass() {
		return RechargeMobileGetPhoneInfoResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(phoneNo, "phoneNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}