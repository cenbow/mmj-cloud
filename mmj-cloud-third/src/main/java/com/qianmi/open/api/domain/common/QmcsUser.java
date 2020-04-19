package com.qianmi.open.api.domain.common;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 用户消息
 *
 * @author auto
 * @since 2.0
 */
public class QmcsUser extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 接收用户消息的组名
	 */
	@ApiField("group_name")
	private String groupName;

	/**
	 * 用户开通的消息列表
	 */
	@ApiField("topics")
	private String topics;

	/**
	 * 用户编号
	 */
	@ApiField("user_id")
	private String userId;

	public String getGroupName() {
		return this.groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getTopics() {
		return this.topics;
	}
	public void setTopics(String topics) {
		this.topics = topics;
	}

	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}