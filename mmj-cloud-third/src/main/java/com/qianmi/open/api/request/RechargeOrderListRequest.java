package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.RechargeOrderListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.recharge.order.list request
 *
 * @author auto
 * @since 1.0
 */
public class RechargeOrderListRequest implements QianmiRequest<RechargeOrderListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 订单状态  1 成功 2 充值中 3 已撤销 4 未付款
	 */
	private String orderState;

	/** 
	 * 订单时间
	 */
	private String orderTime;

	/** 
	 * 页码
	 */
	private String pageNo;

	/** 
	 * 行号
	 */
	private String pageSize;

	/** 
	 * 充值帐号
	 */
	private String rechargeAccount;

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	public String getOrderState() {
		return this.orderState;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getOrderTime() {
		return this.orderTime;
	}

	public void setPageNo(String pageNo) {
		this.pageNo = pageNo;
	}
	public String getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String getPageSize() {
		return this.pageSize;
	}

	public void setRechargeAccount(String rechargeAccount) {
		this.rechargeAccount = rechargeAccount;
	}
	public String getRechargeAccount() {
		return this.rechargeAccount;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.recharge.order.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("orderState", this.orderState);
		txtParams.put("orderTime", this.orderTime);
		txtParams.put("pageNo", this.pageNo);
		txtParams.put("pageSize", this.pageSize);
		txtParams.put("rechargeAccount", this.rechargeAccount);
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

	public Class<RechargeOrderListResponse> getResponseClass() {
		return RechargeOrderListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderTime, "orderTime");
		RequestCheckUtils.checkNotEmpty(pageNo, "pageNo");
		RequestCheckUtils.checkNotEmpty(pageSize, "pageSize");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}