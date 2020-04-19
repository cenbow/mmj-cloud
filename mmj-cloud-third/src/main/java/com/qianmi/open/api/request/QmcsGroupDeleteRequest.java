package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.QmcsGroupDeleteResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.qmcs.group.delete request
 *
 * @author auto
 * @since 1.0
 */
public class QmcsGroupDeleteRequest implements QianmiRequest<QmcsGroupDeleteResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 分组名称，分组删除后，用户的消息将会存储于默认分组中。警告：由于分组已经删除，用户之前未消费的消息将无法再获取。不能以default开头，default开头为系统默认组。
	 */
	private String groupName;

	/** 
	 * 用户编号列表，不传表示删除整个分组，如果用户全部删除后，也会自动删除整个分组
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
		return "qianmi.qmcs.group.delete";
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

	public Class<QmcsGroupDeleteResponse> getResponseClass() {
		return QmcsGroupDeleteResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(groupName, "groupName");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}