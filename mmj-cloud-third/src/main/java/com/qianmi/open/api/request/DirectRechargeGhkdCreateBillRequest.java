package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.DirectRechargeGhkdCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.directRecharge.ghkd.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class DirectRechargeGhkdCreateBillRequest implements QianmiRequest<DirectRechargeGhkdCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 充值帐号
	 */
	private String account;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 城市编号
	 */
	private String cityId;

	/** 
	 * 充值金额编号(面值)
	 */
	private String faceValueId;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 购买数量(默认为1)
	 */
	private String itemNum;

	/** 
	 * 固话宽带充值缴费方式编号 （月租费，账号）
	 */
	private String modeId;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 省份编号
	 */
	private String provinceId;

	/** 
	 * 固话宽带充值类型编号 （固话，宽带，综合）
	 */
	private String typeId;

	/** 
	 * 固话宽带充值类型名称 （固话，宽带，综合）
	 */
	private String typeName;

	/** 
	 * 缴费单位编号
	 */
	private String unitId;

	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount() {
		return this.account;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityId() {
		return this.cityId;
	}

	public void setFaceValueId(String faceValueId) {
		this.faceValueId = faceValueId;
	}
	public String getFaceValueId() {
		return this.faceValueId;
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

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}
	public String getModeId() {
		return this.modeId;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceId() {
		return this.provinceId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeId() {
		return this.typeId;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeName() {
		return this.typeName;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getUnitId() {
		return this.unitId;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.directRecharge.ghkd.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("account", this.account);
		txtParams.put("callback", this.callback);
		txtParams.put("cityId", this.cityId);
		txtParams.put("faceValueId", this.faceValueId);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemNum", this.itemNum);
		txtParams.put("modeId", this.modeId);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("provinceId", this.provinceId);
		txtParams.put("typeId", this.typeId);
		txtParams.put("typeName", this.typeName);
		txtParams.put("unitId", this.unitId);
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

	public Class<DirectRechargeGhkdCreateBillResponse> getResponseClass() {
		return DirectRechargeGhkdCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(account, "account");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}