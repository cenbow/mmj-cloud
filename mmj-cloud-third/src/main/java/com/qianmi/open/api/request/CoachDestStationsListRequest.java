package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.CoachDestStationsListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.coach.destStations.list request
 *
 * @author auto
 * @since 1.0
 */
public class CoachDestStationsListRequest implements QianmiRequest<CoachDestStationsListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 起始站中文
	 */
	private String startStation;

	public void setStartStation(String startStation) {
		this.startStation = startStation;
	}
	public String getStartStation() {
		return this.startStation;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.coach.destStations.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("startStation", this.startStation);
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

	public Class<CoachDestStationsListResponse> getResponseClass() {
		return CoachDestStationsListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(startStation, "startStation");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}