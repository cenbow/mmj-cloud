package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.RechargeBaseCalcPriceResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.recharge.base.calcPrice request
 *
 * @author auto
 * @since 1.0
 */
public class RechargeBaseCalcPriceRequest implements QianmiRequest<RechargeBaseCalcPriceResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 建议零售价设价表达式
	 */
	private String advicePriceExpression;

	/** 
	 * 建议零售价设价方式
	 */
	private String advicePriceOpt;

	/** 
	 * 进价设价表达式
	 */
	private String inPriceExpression;

	/** 
	 * 进价设价方式
	 */
	private String inPriceOpt;

	/** 
	 * 充值金额
	 */
	private String itemNum;

	public void setAdvicePriceExpression(String advicePriceExpression) {
		this.advicePriceExpression = advicePriceExpression;
	}
	public String getAdvicePriceExpression() {
		return this.advicePriceExpression;
	}

	public void setAdvicePriceOpt(String advicePriceOpt) {
		this.advicePriceOpt = advicePriceOpt;
	}
	public String getAdvicePriceOpt() {
		return this.advicePriceOpt;
	}

	public void setInPriceExpression(String inPriceExpression) {
		this.inPriceExpression = inPriceExpression;
	}
	public String getInPriceExpression() {
		return this.inPriceExpression;
	}

	public void setInPriceOpt(String inPriceOpt) {
		this.inPriceOpt = inPriceOpt;
	}
	public String getInPriceOpt() {
		return this.inPriceOpt;
	}

	public void setItemNum(String itemNum) {
		this.itemNum = itemNum;
	}
	public String getItemNum() {
		return this.itemNum;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.recharge.base.calcPrice";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("advicePriceExpression", this.advicePriceExpression);
		txtParams.put("advicePriceOpt", this.advicePriceOpt);
		txtParams.put("inPriceExpression", this.inPriceExpression);
		txtParams.put("inPriceOpt", this.inPriceOpt);
		txtParams.put("itemNum", this.itemNum);
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

	public Class<RechargeBaseCalcPriceResponse> getResponseClass() {
		return RechargeBaseCalcPriceResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(advicePriceExpression, "advicePriceExpression");
		RequestCheckUtils.checkNotEmpty(advicePriceOpt, "advicePriceOpt");
		RequestCheckUtils.checkNotEmpty(inPriceExpression, "inPriceExpression");
		RequestCheckUtils.checkNotEmpty(inPriceOpt, "inPriceOpt");
		RequestCheckUtils.checkNotEmpty(itemNum, "itemNum");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}