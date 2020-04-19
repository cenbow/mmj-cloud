package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.DirectRechargeAlipayCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.directRecharge.alipay.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class DirectRechargeAlipayCreateBillRequest implements QianmiRequest<DirectRechargeAlipayCreateBillResponse> {

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
	 * 购买数量
	 */
	private String itemNum;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 充值帐号
	 */
	private String rechargeAccount;

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

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getItemNum() {
		return this.itemNum;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}
	public String getRechargeAccount() {
		return this.rechargeAccount;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.directRecharge.alipay.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemNum", this.itemNum);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("rechargeAccount", this.rechargeAccount);
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

	public Class<DirectRechargeAlipayCreateBillResponse> getResponseClass() {
		return DirectRechargeAlipayCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(itemNum, "itemNum");
		RequestCheckUtils.checkNotEmpty(rechargeAccount, "rechargeAccount");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}