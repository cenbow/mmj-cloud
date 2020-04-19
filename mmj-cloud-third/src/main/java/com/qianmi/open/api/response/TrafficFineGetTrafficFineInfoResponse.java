package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.TrafficFineInfo;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.trafficFine.getTrafficFineInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class TrafficFineGetTrafficFineInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 查询到的信息，如您没有违章记录
	 */
	@ApiField("message")
	private String message;

	/** 
	 * 罚单列表
	 */
	@ApiListField("trafficFineList")
	@ApiField("trafficFineInfo")
	private List<TrafficFineInfo> trafficFineList;

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage( ) {
		return this.message;
	}

	public void setTrafficFineList(List<TrafficFineInfo> trafficFineList) {
		this.trafficFineList = trafficFineList;
	}
	public List<TrafficFineInfo> getTrafficFineList( ) {
		return this.trafficFineList;
	}

}
