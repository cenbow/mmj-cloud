package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.common.QmcsMsgConsumeRecord;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.qmcs.msg.consume.record response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsMsgConsumeRecordResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 消息消费记录
	 */
	@ApiListField("msg_consume_records")
	@ApiField("qmcs_msg_consume_record")
	private List<QmcsMsgConsumeRecord> msgConsumeRecords;

	/** 
	 * 总消费次数
	 */
	@ApiField("total_results")
	private Integer totalResults;

	public void setMsgConsumeRecords(List<QmcsMsgConsumeRecord> msgConsumeRecords) {
		this.msgConsumeRecords = msgConsumeRecords;
	}
	public List<QmcsMsgConsumeRecord> getMsgConsumeRecords( ) {
		return this.msgConsumeRecords;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	public Integer getTotalResults( ) {
		return this.totalResults;
	}

}
