package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.RechargeMobileCorrectDetailResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.recharge.mobile.correct.detail request
 *
 * @author auto
 * @since 1.0
 */
public class RechargeMobileCorrectDetailRequest implements QianmiRequest<RechargeMobileCorrectDetailResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 关联订单编号
	 */
	private String billId;

	/** 
	 * 冲正状态 1-待冲正 4-冲正失败 5-冲正成功
	 */
	private String correctStatus;

	/** 
	 * 订单结束时间
	 */
	private String endTime;

	/** 
	 * 冲正手机号码
	 */
	private String mobileNo;

	/** 
	 * 订单开始时间
	 */
	private String startTime;

	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBillId() {
		return this.billId;
	}

	public void setCorrectStatus(String correctStatus) {
		this.correctStatus = correctStatus;
	}
	public String getCorrectStatus() {
		return this.correctStatus;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEndTime() {
		return this.endTime;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return this.startTime;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.recharge.mobile.correct.detail";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("billId", this.billId);
		txtParams.put("correctStatus", this.correctStatus);
		txtParams.put("endTime", this.endTime);
		txtParams.put("mobileNo", this.mobileNo);
		txtParams.put("startTime", this.startTime);
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

	public Class<RechargeMobileCorrectDetailResponse> getResponseClass() {
		return RechargeMobileCorrectDetailResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(billId, "billId");
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}