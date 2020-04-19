package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.CoachLinesListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.coach.lines.list request
 *
 * @author auto
 * @since 1.0
 */
public class CoachLinesListRequest implements QianmiRequest<CoachLinesListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 出发日期 格式 yyyy-MM-dd
	 */
	private String date;

	/** 
	 * 起始站中文名称
	 */
	private String from;

	/** 
	 * 到达站中文名称
	 */
	private String to;

	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return this.date;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	public String getFrom() {
		return this.from;
	}

	public void setTo(String to) {
		this.to = to;
	}
	public String getTo() {
		return this.to;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.coach.lines.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("date", this.date);
		txtParams.put("from", this.from);
		txtParams.put("to", this.to);
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

	public Class<CoachLinesListResponse> getResponseClass() {
		return CoachLinesListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(date, "date");
		RequestCheckUtils.checkNotEmpty(from, "from");
		RequestCheckUtils.checkNotEmpty(to, "to");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}