package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.GasCardInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.elife.gasCard.getAccountInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class GasCardGetAccountInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 加油卡查询 （姓名，状态）
	 */
	@ApiField("gasCardInfo")
	private GasCardInfo gasCardInfo;

	public void setGasCardInfo(GasCardInfo gasCardInfo) {
		this.gasCardInfo = gasCardInfo;
	}
	public GasCardInfo getGasCardInfo( ) {
		return this.gasCardInfo;
	}

}
