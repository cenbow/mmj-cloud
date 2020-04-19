package com.qianmi.open.api.domain.common;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 消息消费记录
 *
 * @author auto
 * @since 2.0
 */
public class QmcsMsgConsumeRecord extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 消息消费时间
	 */
	@ApiField("consume_time")
	private String consumeTime;

	/**
	 * 消息ID
	 */
	@ApiField("id")
	private String id;

	/**
	 * 消息发布时间
	 */
	@ApiField("pub_time")
	private String pubTime;

	/**
	 * 消息主题
	 */
	@ApiField("topic")
	private String topic;

	/**
	 * 授权用户编号
	 */
	@ApiField("user_id")
	private String userId;

	public String getConsumeTime() {
		return this.consumeTime;
	}
	public void setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getPubTime() {
		return this.pubTime;
	}
	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	public String getTopic() {
		return this.topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getUserId() {
		return this.userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

}