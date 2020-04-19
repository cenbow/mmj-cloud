package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.AirOrdersListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.air.orders.list request
 *
 * @author auto
 * @since 1.0
 */
public class AirOrdersListRequest implements QianmiRequest<AirOrdersListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 订单结束时间
	 */
	private String endTime;

	/** 
	 * 订单状态, 0:预定中, 1:待支付, 2:已取消, 3:出票中, 4:已出票, 5:出票失败
	 */
	private String orderStatus;

	/** 
	 * 订单类型 1:火车票  2:飞机票  3:汽车票
	 */
	private Integer orderType;

	/** 
	 * 当前页码 从0开始
	 */
	private Integer pageNo;

	/** 
	 * 每页显示条数
	 */
	private Integer pageSize;

	/** 
	 * 按订单生成时间排序标志,默认降序 格式: asc-升序/desc-降序
	 */
	private String sort;

	/** 
	 * 订单开始时间
	 */
	private String startTime;

	/** 
	 * 供货商编号
	 */
	private String supUserId;

	/** 
	 * 订单主单号
	 */
	private String tradeNo;

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEndTime() {
		return this.endTime;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	public String getOrderStatus() {
		return this.orderStatus;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}
	public Integer getOrderType() {
		return this.orderType;
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

	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getSort() {
		return this.sort;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return this.startTime;
	}

	public void setSupUserId(String supUserId) {
		this.supUserId = supUserId;
	}
	public String getSupUserId() {
		return this.supUserId;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}
	public String getTradeNo() {
		return this.tradeNo;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.air.orders.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("endTime", this.endTime);
		txtParams.put("orderStatus", this.orderStatus);
		txtParams.put("orderType", this.orderType);
		txtParams.put("pageNo", this.pageNo);
		txtParams.put("pageSize", this.pageSize);
		txtParams.put("sort", this.sort);
		txtParams.put("startTime", this.startTime);
		txtParams.put("supUserId", this.supUserId);
		txtParams.put("tradeNo", this.tradeNo);
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

	public Class<AirOrdersListResponse> getResponseClass() {
		return AirOrdersListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(endTime, "endTime");
		RequestCheckUtils.checkNotEmpty(startTime, "startTime");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}