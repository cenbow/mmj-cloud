package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.LogisticsDetail;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.logistics.detail.get response.
 *
 * @author auto
 * @since 2.0
 */
public class LogisticsDetailGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 物流流转详情
	 */
	@ApiListField("logisticsDetails")
	@ApiField("logisticsDetail")
	private List<LogisticsDetail> logisticsDetails;

	public void setLogisticsDetails(List<LogisticsDetail> logisticsDetails) {
		this.logisticsDetails = logisticsDetails;
	}
	public List<LogisticsDetail> getLogisticsDetails( ) {
		return this.logisticsDetails;
	}

}
