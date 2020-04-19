package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.CatvCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.catv.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class CatvCreateBillRequest implements QianmiRequest<CatvCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 客户地址
	 */
	private String customerAddress;

	/** 
	 * 用户姓名
	 */
	private String customerName;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 缴费账号或户号，卡号
	 */
	private String rechargeAccount;

	/** 
	 * 充值金额，支持两位小数
	 */
	private String rechargeAmount;

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	public String getCustomerAddress() {
		return this.customerAddress;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerName() {
		return this.customerName;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
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
		return "qianmi.elife.catv.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("customerAddress", this.customerAddress);
		txtParams.put("customerName", this.customerName);
		txtParams.put("itemId", this.itemId);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("rechargeAccount", this.rechargeAccount);
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

	public Class<CatvCreateBillResponse> getResponseClass() {
		return CatvCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(rechargeAccount, "rechargeAccount");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}