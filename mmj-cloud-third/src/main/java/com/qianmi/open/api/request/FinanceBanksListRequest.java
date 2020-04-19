package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.FinanceBanksListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;

import java.util.Map;

/**
 * API: qianmi.elife.finance.banks.list request
 *
 * @author auto
 * @since 1.0
 */
public class FinanceBanksListRequest implements QianmiRequest<FinanceBanksListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 银行编码
	 */
	private String code;

	/** 
	 * 是否热门 Y-热门 N-非热门
	 */
	private String hot;

	/** 
	 * 银行中文名,支持模糊匹配
	 */
	private String name;

	/** 
	 * 银行名称拼音前缀
	 */
	private String prepin;

	/** 
	 * 银行名称短拼
	 */
	private String shortpin;

	public void setCode(String code) {
		this.code = code;
	}
	public String getCode() {
		return this.code;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}
	public String getHot() {
		return this.hot;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return this.name;
	}

	public void setPrepin(String prepin) {
		this.prepin = prepin;
	}
	public String getPrepin() {
		return this.prepin;
	}

	public void setShortpin(String shortpin) {
		this.shortpin = shortpin;
	}
	public String getShortpin() {
		return this.shortpin;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.finance.banks.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("code", this.code);
		txtParams.put("hot", this.hot);
		txtParams.put("name", this.name);
		txtParams.put("prepin", this.prepin);
		txtParams.put("shortpin", this.shortpin);
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

	public Class<FinanceBanksListResponse> getResponseClass() {
		return FinanceBanksListResponse.class;
	}

	public void check() throws ApiRuleException {
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}