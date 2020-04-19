package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.domain.elife.ItemProp;
import com.qianmi.open.api.tool.mapping.ApiField;
import com.qianmi.open.api.tool.mapping.ApiListField;

import java.util.List;

/**
 * API: qianmi.elife.item.props.list response.
 *
 * @author auto
 * @since 2.0
 */
public class ItemPropsListResponse extends QianmiResponse {

	private static final long serialVersionUID = 1L;

	/** 
	 * 所在标准类目编号（公共事业商品即缴费类型projectId）
	 */
	@ApiField("cid")
	private String cid;

	/** 
	 * 标准商品编号
	 */
	@ApiField("itemId")
	private String itemId;

	/** 
	 * 标准属性集合
	 */
	@ApiListField("itemProps")
	@ApiField("itemProp")
	private List<ItemProp> itemProps;

	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getCid( ) {
		return this.cid;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId( ) {
		return this.itemId;
	}

	public void setItemProps(List<ItemProp> itemProps) {
		this.itemProps = itemProps;
	}
	public List<ItemProp> getItemProps( ) {
		return this.itemProps;
	}

}
