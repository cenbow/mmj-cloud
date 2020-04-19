package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.common.QmcsMessage;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.qmcs.messages.consume response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsMessagesConsumeResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 消息列表
	 */
	@ApiListField("qmcs_messages")
	@ApiField("qmcs_message")
	private List<QmcsMessage> qmcsMessages;

	public void setQmcsMessages(List<QmcsMessage> qmcsMessages) {
		this.qmcsMessages = qmcsMessages;
	}
	public List<QmcsMessage> getQmcsMessages( ) {
		return this.qmcsMessages;
	}

}
