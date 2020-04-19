package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.common.QmcsQueueInfo;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.qmcs.queue.get response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsQueueGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 消息队列积压情况
	 */
	@ApiField("qmcs_queue_info")
	private QmcsQueueInfo qmcsQueueInfo;

	public void setQmcsQueueInfo(QmcsQueueInfo qmcsQueueInfo) {
		this.qmcsQueueInfo = qmcsQueueInfo;
	}
	public QmcsQueueInfo getQmcsQueueInfo( ) {
		return this.qmcsQueueInfo;
	}

}
