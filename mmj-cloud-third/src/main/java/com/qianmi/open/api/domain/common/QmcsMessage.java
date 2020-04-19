package com.qianmi.open.api.domain.common;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 消息
 *
 * @author auto
 * @since 2.0
 */
public class QmcsMessage extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 消息详细内容，格式为JSON
	 */
	@ApiField("content")
	private String content;

	/**
	 * 消息ID
	 */
	@ApiField("id")
	private String id;

	/**
	 * 消息发布者的AppKey
	 */
	@ApiField("pub_app_key")
	private String pubAppKey;

	/**
	 * 消息发布时间
	 */
	@ApiField("pub_time")
	private String pubTime;

	/**
	 * 消息所属主题
	 */
	@ApiField("topic")
	private String topic;

	/**
	 * 用户编号
	 */
	@ApiField("user_id")
	private String userId;

	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getPubAppKey() {
		return this.pubAppKey;
	}
	public void setPubAppKey(String pubAppKey) {
		this.pubAppKey = pubAppKey;
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