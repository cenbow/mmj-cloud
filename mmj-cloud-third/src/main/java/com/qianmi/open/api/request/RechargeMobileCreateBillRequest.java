package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.RechargeMobileCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.recharge.mobile.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class RechargeMobileCreateBillRequest implements QianmiRequest<RechargeMobileCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 手机号码
	 */
	private String mobileNo;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 充值金额
	 */
	private String rechargeAmount;

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setRechargeAmount(String rechargeAmount) {
		this.rechargeAmount = rechargeAmount;
	}
	public String getRechargeAmount() {
		return this.rechargeAmount;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.recharge.mobile.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("itemId", this.itemId);
		txtParams.put("mobileNo", this.mobileNo);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("rechargeAmount", this.rechargeAmount);
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

	public Class<RechargeMobileCreateBillResponse> getResponseClass() {
		return RechargeMobileCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
		RequestCheckUtils.checkNotEmpty(rechargeAmount, "rechargeAmount");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}