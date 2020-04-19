package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.PhoneInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: bm.elife.directRecharge.ghkd.getPhoneInfo response.
 *
 * @author auto
 * @since 2.0
 */
public class BmDirectRechargeGhkdGetPhoneInfoResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 查询手机号或固话号详情信息返回
	 */
	@ApiField("phoneInfo")
	private PhoneInfo phoneInfo;

	public void setPhoneInfo(PhoneInfo phoneInfo) {
		this.phoneInfo = phoneInfo;
	}
	public PhoneInfo getPhoneInfo( ) {
		return this.phoneInfo;
	}

}
