package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmRechargeMobileCorrectAvailableListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.recharge.mobile.correct.available.list request
 *
 * @author auto
 * @since 1.0
 */
public class BmRechargeMobileCorrectAvailableListRequest implements QianmiRequest<BmRechargeMobileCorrectAvailableListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 标准商品名称
	 */
	private String itemName;

	/** 
	 * 冲正手机号码
	 */
	private String mobileNo;

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemName() {
		return this.itemName;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.recharge.mobile.correct.available.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemName", this.itemName);
		txtParams.put("mobileNo", this.mobileNo);
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

	public Class<BmRechargeMobileCorrectAvailableListResponse> getResponseClass() {
		return BmRechargeMobileCorrectAvailableListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}