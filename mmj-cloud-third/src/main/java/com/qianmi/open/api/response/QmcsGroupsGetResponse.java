package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.common.QmcsGroup;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.qmcs.groups.get response.
 *
 * @author auto
 * @since 2.0
 */
public class QmcsGroupsGetResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 分组列表
	 */
	@ApiListField("qmcs_groups")
	@ApiField("group")
	private List<QmcsGroup> qmcsGroups;

	/** 
	 * 总条数
	 */
	@ApiField("total_results")
	private Integer totalResults;

	public void setQmcsGroups(List<QmcsGroup> qmcsGroups) {
		this.qmcsGroups = qmcsGroups;
	}
	public List<QmcsGroup> getQmcsGroups( ) {
		return this.qmcsGroups;
	}

	public void setTotalResults(Integer totalResults) {
		this.totalResults = totalResults;
	}
	public Integer getTotalResults( ) {
		return this.totalResults;
	}

}
