package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.UnitsListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.units.list request
 *
 * @author auto
 * @since 1.0
 */
public class UnitsListRequest implements QianmiRequest<UnitsListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 市属性V编号
	 */
	private String cityVid;

	/** 
	 * 缴费项目编号，接口查询返回，多个时以','分隔 :<br>水费-c2670<br>电费-c2680<br>燃气费-c2681<br>有线电视费-c2682
	 */
	private String projectIds;

	/** 
	 * 省属性V编号
	 */
	private String provinceVid;

	public void setCityVid(String cityVid) {
		this.cityVid = cityVid;
	}
	public String getCityVid() {
		return this.cityVid;
	}

	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}
	public String getProjectIds() {
		return this.projectIds;
	}

	public void setProvinceVid(String provinceVid) {
		this.provinceVid = provinceVid;
	}
	public String getProvinceVid() {
		return this.provinceVid;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.units.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("cityVid", this.cityVid);
		txtParams.put("projectIds", this.projectIds);
		txtParams.put("provinceVid", this.provinceVid);
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

	public Class<UnitsListResponse> getResponseClass() {
		return UnitsListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(cityVid, "cityVid");
		RequestCheckUtils.checkNotEmpty(projectIds, "projectIds");
		RequestCheckUtils.checkNotEmpty(provinceVid, "provinceVid");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}