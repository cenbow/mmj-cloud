package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.common.QmcsUser;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.qmcs.user.get response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsUserGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 用户消息
	 */
	@ApiField("qmcs_user")
	private QmcsUser qmcsUser;

	public void setQmcsUser(QmcsUser qmcsUser) {
		this.qmcsUser = qmcsUser;
	}
	public QmcsUser getQmcsUser( ) {
		return this.qmcsUser;
	}

}
