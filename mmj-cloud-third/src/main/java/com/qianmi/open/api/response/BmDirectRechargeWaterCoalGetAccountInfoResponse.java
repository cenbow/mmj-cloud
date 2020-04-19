package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.WaterCoalBill;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.directRecharge.waterCoal.getAccountInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class BmDirectRechargeWaterCoalGetAccountInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 查询标识
	 */
	@ApiField("channel")
	private String channel;

	/** 
	 * 查询到的账户信息为空时的说明信息，如:已缴费成功
	 */
	@ApiField("message")
	private String message;

	/** 
	 * 查询状态: 1-账户信息正常返回 0-查询异常或无返回
	 */
	@ApiField("status")
	private String status;

	/** 
	 * 账单列表
	 */
	@ApiListField("waterCoalBills")
	@ApiField("waterCoalBill")
	private List<WaterCoalBill> waterCoalBills;

	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getChannel( ) {
		return this.channel;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public String getMessage( ) {
		return this.message;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatus( ) {
		return this.status;
	}

	public void setWaterCoalBills(List<WaterCoalBill> waterCoalBills) {
		this.waterCoalBills = waterCoalBills;
	}
	public List<WaterCoalBill> getWaterCoalBills( ) {
		return this.waterCoalBills;
	}

}
