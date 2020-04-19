package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.AdminItem;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.coach.insurs.list response.
 *
 * @author auto
 * @since 2.0
 */
public class CoachInsursListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 可选择的汽车票保险标准商品信息
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
	 * 单页返回的记录数
	 */
	@ApiField("pageSize")
	private Integer pageSize;

	/** 
	 * 总刻录数
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
