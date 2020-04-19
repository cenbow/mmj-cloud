package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.TicketTrade;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.coach.createBill response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachCreateBillResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 票务订单
	 */
	@ApiField("ticketTrade")
	private TicketTrade ticketTrade;

	public void setTicketTrade(TicketTrade ticketTrade) {
		this.ticketTrade = ticketTrade;
	}
	public TicketTrade getTicketTrade( ) {
		return this.ticketTrade;
	}

}
