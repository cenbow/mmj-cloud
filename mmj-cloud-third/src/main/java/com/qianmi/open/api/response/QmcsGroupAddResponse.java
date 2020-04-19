package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * API: qianmi.qmcs.group.add response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsGroupAddResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 创建时间
	 */
	@ApiField("created")
	private String created;

	/** 
	 * 分组名称
	 */
	@ApiField("group_name")
	private String groupName;

	public void setCreated(String created) {
		this.created = created;
	}
	public String getCreated( ) {
		return this.created;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getGroupName( ) {
		return this.groupName;
	}

}
