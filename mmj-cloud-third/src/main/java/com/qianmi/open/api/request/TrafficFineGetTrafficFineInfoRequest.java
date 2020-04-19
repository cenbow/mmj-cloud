package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.TrafficFineGetTrafficFineInfoResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.trafficFine.getTrafficFineInfo request
 *
 * @author auto
 * @since 1.0
 */
public class TrafficFineGetTrafficFineInfoRequest implements QianmiRequest<TrafficFineGetTrafficFineInfoResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 车牌号码
	 */
	private String carNo;

	/** 
	 * 车辆类型
	 */
	private String carType;

	/** 
	 * 城市中文
	 */
	private String city;

	/** 
	 * 发动机号
	 */
	private String engineId;

	/** 
	 * 车架号
	 */
	private String frameId;

	/** 
	 * 省份中文
	 */
	private String province;

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getCarNo() {
		return this.carNo;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getCarType() {
		return this.carType;
	}

	public void setCity(String city) {
		this.city = city;
	}
	public String getCity() {
		return this.city;
	}

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}
	public String getEngineId() {
		return this.engineId;
	}

	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}
	public String getFrameId() {
		return this.frameId;
	}

	public void setProvince(String province) {
		this.province = province;
	}
	public String getProvince() {
		return this.province;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.trafficFine.getTrafficFineInfo";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("carNo", this.carNo);
		txtParams.put("carType", this.carType);
		txtParams.put("city", this.city);
		txtParams.put("engineId", this.engineId);
		txtParams.put("frameId", this.frameId);
		txtParams.put("province", this.province);
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

	public Class<TrafficFineGetTrafficFineInfoResponse> getResponseClass() {
		return TrafficFineGetTrafficFineInfoResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(carNo, "carNo");
		RequestCheckUtils.checkNotEmpty(carType, "carType");
		RequestCheckUtils.checkNotEmpty(city, "city");
		RequestCheckUtils.checkNotEmpty(engineId, "engineId");
		RequestCheckUtils.checkNotEmpty(province, "province");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}