package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BankTransferCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.bankTransfer.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class BankTransferCreateBillRequest implements QianmiRequest<BankTransferCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 金额
	 */
	private String amount;

	/** 
	 * 银行卡号
	 */
	private String bankCardNo;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 客户名称
	 */
	private String customerName;

	/** 
	 * 客户电话
	 */
	private String customerTel;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 转账银行ID
	 */
	private String targetBankId;

	/** 
	 * 转账银行名称
	 */
	private String targetBankName;

	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAmount() {
		return this.amount;
	}

	public void setBankCardNo(String bankCardNo) {
		this.bankCardNo = bankCardNo;
	}
	public String getBankCardNo() {
		return this.bankCardNo;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getCustomerName() {
		return this.customerName;
	}

	public void setCustomerTel(String customerTel) {
		this.customerTel = customerTel;
	}
	public String getCustomerTel() {
		return this.customerTel;
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

	public void setTargetBankId(String targetBankId) {
		this.targetBankId = targetBankId;
	}
	public String getTargetBankId() {
		return this.targetBankId;
	}

	public void setTargetBankName(String targetBankName) {
		this.targetBankName = targetBankName;
	}
	public String getTargetBankName() {
		return this.targetBankName;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.bankTransfer.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("amount", this.amount);
		txtParams.put("bankCardNo", this.bankCardNo);
		txtParams.put("callback", this.callback);
		txtParams.put("customerName", this.customerName);
		txtParams.put("customerTel", this.customerTel);
		txtParams.put("itemId", this.itemId);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("targetBankId", this.targetBankId);
		txtParams.put("targetBankName", this.targetBankName);
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

	public Class<BankTransferCreateBillResponse> getResponseClass() {
		return BankTransferCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(amount, "amount");
		RequestCheckUtils.checkNotEmpty(bankCardNo, "bankCardNo");
		RequestCheckUtils.checkNotEmpty(customerName, "customerName");
		RequestCheckUtils.checkNotEmpty(customerTel, "customerTel");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(targetBankId, "targetBankId");
		RequestCheckUtils.checkNotEmpty(targetBankName, "targetBankName");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}