package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.QmcsMessagesConfirmResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.qmcs.messages.confirm request
 *
 * @author auto
 * @since 1.0
 */
public class QmcsMessagesConfirmRequest implements QianmiRequest<QmcsMessagesConfirmResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 处理成功的消息ID列表，最大200个ID
	 */
	private String sMessageIds;

	public void setsMessageIds(String sMessageIds) {
		this.sMessageIds = sMessageIds;
	}
	public String getsMessageIds() {
		return this.sMessageIds;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.qmcs.messages.confirm";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("s_message_ids", this.sMessageIds);
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

	public Class<QmcsMessagesConfirmResponse> getResponseClass() {
		return QmcsMessagesConfirmResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(sMessageIds, "sMessageIds");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}