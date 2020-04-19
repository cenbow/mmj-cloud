package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.TrainOrderRefundResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.train.order.refund request
 *
 * @author auto
 * @since 1.0
 */
public class TrainOrderRefundRequest implements QianmiRequest<TrainOrderRefundResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 订单子单编号集合
	 */
	private String orderNos;

	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}
	public String getOrderNos() {
		return this.orderNos;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.train.order.refund";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("orderNos", this.orderNos);
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

	public Class<TrainOrderRefundResponse> getResponseClass() {
		return TrainOrderRefundResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(orderNos, "orderNos");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}