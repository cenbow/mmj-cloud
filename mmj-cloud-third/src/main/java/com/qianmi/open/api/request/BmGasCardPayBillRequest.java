package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmGasCardPayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.gasCard.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class BmGasCardPayBillRequest implements QianmiRequest<BmGasCardPayBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 卡号类型
	 */
	private String cardType;

	/** 
	 * 持卡人姓名
	 */
	private String gasCardName;

	/** 
	 * 加油卡卡号
	 */
	private String gasCardNo;

	/** 
	 * 持卡人手机号码
	 */
	private String gasCardTel;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 省份名称（不带省）
	 */
	private String province;

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

	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getCardType() {
		return this.cardType;
	}

	public void setGasCardName(String gasCardName) {
		this.gasCardName = gasCardName;
	}
	public String getGasCardName() {
		return this.gasCardName;
	}

	public void setGasCardNo(String gasCardNo) {
		this.gasCardNo = gasCardNo;
	}
	public String getGasCardNo() {
		return this.gasCardNo;
	}

	public void setGasCardTel(String gasCardTel) {
		this.gasCardTel = gasCardTel;
	}
	public String getGasCardTel() {
		return this.gasCardTel;
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

	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince() {
		return this.province;
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
		return "bm.elife.gasCard.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("cardType", this.cardType);
		txtParams.put("gasCardName", this.gasCardName);
		txtParams.put("gasCardNo", this.gasCardNo);
		txtParams.put("gasCardTel", this.gasCardTel);
		txtParams.put("itemId", this.itemId);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("province", this.province);
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

	public Class<BmGasCardPayBillResponse> getResponseClass() {
		return BmGasCardPayBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(gasCardName, "gasCardName");
		RequestCheckUtils.checkNotEmpty(gasCardNo, "gasCardNo");
		RequestCheckUtils.checkNotEmpty(gasCardTel, "gasCardTel");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}