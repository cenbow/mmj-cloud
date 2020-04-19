package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmCardPasswordPayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.cardPassword.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class BmCardPasswordPayBillRequest implements QianmiRequest<BmCardPasswordPayBillResponse> {

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
	 * 卡密接收邮箱
	 */
	private String receiveEmail;

	/** 
	 * 卡密接收手机号码
	 */
	private String receiveMobile;

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

	public void setReceiveEmail(String receiveEmail) {
		this.receiveEmail = receiveEmail;
	}
	public String getReceiveEmail() {
		return this.receiveEmail;
	}

	public void setReceiveMobile(String receiveMobile) {
		this.receiveMobile = receiveMobile;
	}
	public String getReceiveMobile() {
		return this.receiveMobile;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.cardPassword.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemNum", this.itemNum);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("receiveEmail", this.receiveEmail);
		txtParams.put("receiveMobile", this.receiveMobile);
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

	public Class<BmCardPasswordPayBillResponse> getResponseClass() {
		return BmCardPasswordPayBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(itemNum, "itemNum");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}