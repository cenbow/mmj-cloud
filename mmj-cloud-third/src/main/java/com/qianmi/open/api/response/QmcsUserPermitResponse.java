package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.qmcs.user.permit response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsUserPermitResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 是否成功
	 */
	@ApiField("is_success")
	private Boolean isSuccess;

	public void setIsSuccess(Boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public Boolean getIsSuccess( ) {
		return this.isSuccess;
	}

}
