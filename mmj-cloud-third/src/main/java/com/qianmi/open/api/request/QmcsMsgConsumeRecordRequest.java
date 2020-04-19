package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.QmcsMsgConsumeRecordResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.qmcs.msg.consume.record request
 *
 * @author auto
 * @since 1.0
 */
public class QmcsMsgConsumeRecordRequest implements QianmiRequest<QmcsMsgConsumeRecordResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 用户自定义的消息分组名称
	 */
	private String groupName;

	/** 
	 * 消息ID
	 */
	private String id;

	/** 
	 * 消息主题
	 */
	private String topic;

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupName() {
		return this.groupName;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return this.id;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getTopic() {
		return this.topic;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.qmcs.msg.consume.record";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("group_name", this.groupName);
		txtParams.put("id", this.id);
		txtParams.put("topic", this.topic);
		if(udfParams != null) {
			txtParams.putAll(this.udfParams);
		}
		return txtParams;
	}

	public void putOtherTextParam(String key, String value) {
		if(this.udfParams == null) {
			this.udfParams = new QianmiHashMap();
		}
		this.udfParams.put(key, value);
	}

	public Class<QmcsMsgConsumeRecordResponse> getResponseClass() {
		return QmcsMsgConsumeRecordResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(id, "id");
		RequestCheckUtils.checkNotEmpty(topic, "topic");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}