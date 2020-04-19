package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.DirectRechargeWaterCoalGetAccountInfoResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.directRecharge.waterCoal.getAccountInfo request
 *
 * @author auto
 * @since 1.0
 */
public class DirectRechargeWaterCoalGetAccountInfoRequest implements QianmiRequest<DirectRechargeWaterCoalGetAccountInfoResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 缴费单标识号（户号或条形码）
	 */
	private String account;

	/** 
	 * 城市名称(后面不带'市')
	 */
	private String city;

	/** 
	 * 城市V编号
	 */
	private String cityId;

	/** 
	 * 标准商品编号，光大与翼支付查询方式必须传入，普通查询无需传入
	 */
	private String itemId;

	/** 
	 * 缴费方式V编号
	 */
	private String modeId;

	/** 
	 * 缴费方式：1是条形码 2是户号, 光大查询需要传入
	 */
	private String modeType;

	/** 
	 * 缴费项目编号
	 */
	private String projectId;

	/** 
	 * 省名称(后面不带'省')
	 */
	private String province;

	/** 
	 * 供货商编号
	 */
	private String supUserId;

	/** 
	 * 缴费单位V编号
	 */
	private String unitId;

	/** 
	 * 缴费单位名称
	 */
	private String unitName;

	public void setAccount(String account) {
		this.account = account;
	}
	public String getAccount() {
		return this.account;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityId() {
		return this.cityId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setModeId(String modeId) {
		this.modeId = modeId;
	}
	public String getModeId() {
		return this.modeId;
	}

	public void setModeType(String modeType) {
		this.modeType = modeType;
	}
	public String getModeType() {
		return this.modeType;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public String getProjectId() {
		return this.projectId;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince() {
		return this.province;
	}

	public void setSupUserId(String supUserId) {
		this.supUserId = supUserId;
	}
	public String getSupUserId() {
		return this.supUserId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}
	public String getUnitId() {
		return this.unitId;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}
	public String getUnitName() {
		return this.unitName;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.directRecharge.waterCoal.getAccountInfo";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("account", this.account);
		txtParams.put("city", this.city);
		txtParams.put("cityId", this.cityId);
		txtParams.put("itemId", this.itemId);
		txtParams.put("modeId", this.modeId);
		txtParams.put("modeType", this.modeType);
		txtParams.put("projectId", this.projectId);
		txtParams.put("province", this.province);
		txtParams.put("supUserId", this.supUserId);
		txtParams.put("unitId", this.unitId);
		txtParams.put("unitName", this.unitName);
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

	public Class<DirectRechargeWaterCoalGetAccountInfoResponse> getResponseClass() {
		return DirectRechargeWaterCoalGetAccountInfoResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(account, "account");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}