package com.qianmi.open.api.domain.common;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 消息通道的分组结构
 *
 * @author auto
 * @since 2.0
 */
public class QmcsGroup extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 创建时间
	 */
	@ApiField("created")
	private String created;

	/**
	 * 分组名称
	 */
	@ApiField("name")
	private String name;

	/**
	 * 用户编号列表
	 */
	@ApiField("user_ids")
	private String userIds;

	public String getCreated() {
		return this.created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getUserIds() {
		return this.userIds;
	}
	public void setUserIds(String userIds) {
		this.userIds = userIds;
	}

}