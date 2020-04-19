package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.OrderInfo;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.recharge.order.list response.
 *
 * @author auto
 * @since 2.0
 */
public class BmRechargeOrderListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 订单详情-用户展示订单列表
	 */
	@ApiListField("orderInfos")
	@ApiField("orderInfo")
	private List<OrderInfo> orderInfos;

	/** 
	 * 页码，从0开始
	 */
	@ApiField("pageNo")
	private Integer pageNo;

	/** 
	 * 每页条数
	 */
	@ApiField("pageSize")
	private Integer pageSize;

	/** 
	 * 总条数
	 */
	@ApiField("totalCount")
	private String totalCount;

	public void setOrderInfos(List<OrderInfo> orderInfos) {
		this.orderInfos = orderInfos;
	}
	public List<OrderInfo> getOrderInfos( ) {
		return this.orderInfos;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageNo( ) {
		return this.pageNo;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize( ) {
		return this.pageSize;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getTotalCount( ) {
		return this.totalCount;
	}

}
