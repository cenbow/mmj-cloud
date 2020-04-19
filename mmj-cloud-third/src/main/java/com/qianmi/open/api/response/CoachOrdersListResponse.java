package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.TicketTrade;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.coach.orders.list response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachOrdersListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 页码，从0开始
	 */
	@ApiField("pageNo")
	private Integer pageNo;

	/** 
	 * 单页返回的记录数，默认10
	 */
	@ApiField("pageSize")
	private Integer pageSize;

	/** 
	 * 订单列表
	 */
	@ApiListField("ticketTrades")
	@ApiField("ticketTrade")
	private List<TicketTrade> ticketTrades;

	/** 
	 * 总记录数
	 */
	@ApiField("totolCount")
	private Integer totolCount;

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

	public void setTicketTrades(List<TicketTrade> ticketTrades) {
		this.ticketTrades = ticketTrades;
	}
	public List<TicketTrade> getTicketTrades( ) {
		return this.ticketTrades;
	}

	public void setTotolCount(Integer totolCount) {
		this.totolCount = totolCount;
	}
	public Integer getTotolCount( ) {
		return this.totolCount;
	}

}
