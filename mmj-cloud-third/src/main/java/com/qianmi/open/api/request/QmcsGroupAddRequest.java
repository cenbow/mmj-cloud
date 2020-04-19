package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.QmcsGroupAddResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.qmcs.group.add request
 *
 * @author auto
 * @since 1.0
 */
public class QmcsGroupAddRequest implements QianmiRequest<QmcsGroupAddResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 分组名称，同一个应用下需要保证唯一性，最长32个字符。添加分组后，消息通道会为用户的消息分配独立分组，但之前的消息还是存储于默认分组中。不能以default开头，default开头为系统默认组。
	 */
	private String groupName;

	/** 
	 * 用户编号列表，以半角逗号分隔
	 */
	private String userIds;

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupName() {
		return this.groupName;
	}

	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}
	public String getUserIds() {
		return this.userIds;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.qmcs.group.add";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("group_name", this.groupName);
		txtParams.put("user_ids", this.userIds);
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

	public Class<QmcsGroupAddResponse> getResponseClass() {
		return QmcsGroupAddResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(groupName, "groupName");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}