package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.CorrectOrder;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.recharge.mobile.correct.list response.
 *
 * @author auto
 * @since 2.0
 */
public class RechargeMobileCorrectListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 冲正的订单列表
	 */
	@ApiListField("correctOrders")
	@ApiField("correctOrder")
	private List<CorrectOrder> correctOrders;

	/** 
	 * 总条数
	 */
	@ApiField("totalCount")
	private Integer totalCount;

	public void setCorrectOrders(List<CorrectOrder> correctOrders) {
		this.correctOrders = correctOrders;
	}
	public List<CorrectOrder> getCorrectOrders( ) {
		return this.correctOrders;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getTotalCount( ) {
		return this.totalCount;
	}

}
