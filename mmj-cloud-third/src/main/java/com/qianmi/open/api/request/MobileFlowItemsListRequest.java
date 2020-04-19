package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.MobileFlowItemsListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.mobile.flow.items.list request
 *
 * @author auto
 * @since 1.0
 */
public class MobileFlowItemsListRequest implements QianmiRequest<MobileFlowItemsListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 流量大小，以M或者G为单位(后面不带B)，例如50M,1G
	 */
	private String flow;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 标准商品名称,支持不带特殊字符的模糊匹配
	 */
	private String itemName;

	/** 
	 * 国内11位手机号
	 */
	private String mobileNo;

	/** 
	 * 页码 从0开始
	 */
	private Integer pageNo;

	/** 
	 * 单页返回的记录数
	 */
	private Integer pageSize;

	public void setFlow(String flow) {
		this.flow = flow;
	}
	public String getFlow() {
		return this.flow;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public String getItemName() {
		return this.itemName;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize() {
		return this.pageSize;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.mobile.flow.items.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("flow", this.flow);
		txtParams.put("itemId", this.itemId);
		txtParams.put("itemName", this.itemName);
		txtParams.put("mobileNo", this.mobileNo);
		txtParams.put("pageNo", this.pageNo);
		txtParams.put("pageSize", this.pageSize);
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

	public Class<MobileFlowItemsListResponse> getResponseClass() {
		return MobileFlowItemsListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}