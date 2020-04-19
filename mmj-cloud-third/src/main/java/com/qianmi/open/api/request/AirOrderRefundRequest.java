package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.AirOrderRefundResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.air.order.refund request
 *
 * @author auto
 * @since 1.0
 */
public class AirOrderRefundRequest implements QianmiRequest<AirOrderRefundResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 订单子单编号集合
	 */
	private String orderNos;

	/** 
	 * 飞机票退票类型:1-退废票,3-退票
	 */
	private String returnType;

	/** 
	 * 订单主单号
	 */
	private String tradeNo;

	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}
	public String getOrderNos() {
		return this.orderNos;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getReturnType() {
		return this.returnType;
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
		return "qianmi.elife.air.order.refund";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("orderNos", this.orderNos);
		txtParams.put("returnType", this.returnType);
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

	public Class<AirOrderRefundResponse> getResponseClass() {
		return AirOrderRefundResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderNos, "orderNos");
		RequestCheckUtils.checkNotEmpty(returnType, "returnType");
		RequestCheckUtils.checkNotEmpty(tradeNo, "tradeNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}