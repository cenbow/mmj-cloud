package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.DirectRechargeLifeRechargeCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.directRecharge.lifeRecharge.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class DirectRechargeLifeRechargeCreateBillRequest implements QianmiRequest<DirectRechargeLifeRechargeCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 账期(光大使用)
	 */
	private String billCycle;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 城市ID
	 */
	private String cityId;

	/** 
	 * 合同号(光大使用)
	 */
	private String contractNo;

	/** 
	 * 用户地址(水电煤根据户号查询出来)
	 */
	private String customerAddress;

	/** 
	 * 户名(水电煤，根据户号查询出来)
	 */
	private String customerName;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 购买数量
	 */
	private String itemNum;

	/** 
	 * 缴费方式类型(光大使用) 1是条形码 2是户号
	 */
	private String modeType;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 缴费项目ID
	 */
	private String projectId;

	/** 
	 * 省份ID
	 */
	private String provinceId;

	/** 
	 * 充值帐号
	 */
	private String rechargeAccount;

	/** 
	 * 缴费单位ID
	 */
	private String unitId;

	public void setBillCycle(String billCycle) {
		this.billCycle = billCycle;
	}
	public String getBillCycle() {
		return this.billCycle;
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

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}
	public String getContractNo() {
		return this.contractNo;
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

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getItemNum() {
		return this.itemNum;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}
	public String getModeType() {
		return this.modeType;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectId() {
		return this.projectId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}
	public String getProvinceId() {
		return this.provinceId;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}
	public String getRechargeAccount() {
		return this.rechargeAccount;
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
		return "qianmi.elife.directRecharge.lifeRecharge.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("billCycle", this.billCycle);
		txtParams.put("callback", this.callback);
		txtParams.put("cityId", this.cityId);
		txtParams.put("contractNo", this.contractNo);
		txtParams.put("customerAddress", this.customerAddress);
		txtParams.put("customerName", this.customerName);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemNum", this.itemNum);
		txtParams.put("modeType", this.modeType);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("projectId", this.projectId);
		txtParams.put("provinceId", this.provinceId);
		txtParams.put("rechargeAccount", this.rechargeAccount);
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

	public Class<DirectRechargeLifeRechargeCreateBillResponse> getResponseClass() {
		return DirectRechargeLifeRechargeCreateBillResponse.class;
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