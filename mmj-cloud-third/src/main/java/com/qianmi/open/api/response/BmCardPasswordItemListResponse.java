package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.AdminItem;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: bm.elife.cardPassword.item.list response.
 *
 * @author auto
 * @since 2.0
 */
public class BmCardPasswordItemListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 货源对接策略列表
	 */
	@ApiListField("items")
	@ApiField("item")
	private List<AdminItem> items;

	/** 
	 * 页码，从0开始
	 */
	@ApiField("pageNo")
	private Integer pageNo;

	/** 
	 * 条数
	 */
	@ApiField("pageSize")
	private Integer pageSize;

	/** 
	 * 总条数
	 */
	@ApiField("totalCount")
	private Integer totalCount;

	public void setItems(List<AdminItem> items) {
		this.items = items;
	}
	public List<AdminItem> getItems( ) {
		return this.items;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageNo( ) {
		return this.pageNo;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize( ) {
		return this.pageSize;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}
	public Integer getTotalCount( ) {
		return this.totalCount;
	}

}
