package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.LogisticsCode;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.logistics.codes.get response.
 *
 * @author auto
 * @since 2.0
 */
public class LogisticsCodesGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 快递公司编码
	 */
	@ApiListField("logisticsCodes")
	@ApiField("logisticsCode")
	private List<LogisticsCode> logisticsCodes;

	public void setLogisticsCodes(List<LogisticsCode> logisticsCodes) {
		this.logisticsCodes = logisticsCodes;
	}
	public List<LogisticsCode> getLogisticsCodes( ) {
		return this.logisticsCodes;
	}

}
