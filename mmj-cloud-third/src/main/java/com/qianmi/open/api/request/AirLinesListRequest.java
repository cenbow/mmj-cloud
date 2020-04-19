package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.AirLinesListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.air.lines.list request
 *
 * @author auto
 * @since 1.0
 */
public class AirLinesListRequest implements QianmiRequest<AirLinesListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 航空公司列表，以|分割
	 */
	private String companys;

	/** 
	 * 起飞日期
	 */
	private String date;

	/** 
	 * 起飞机场编号
	 */
	private String from;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 舱位类型列表，以|分割
	 */
	private String seatTypes;

	/** 
	 * 时间段列表，时间段以|分割
	 */
	private String timePeriods;

	/** 
	 * 目的地机场编号
	 */
	private String to;

	public void setCompanys(String companys) {
		this.companys = companys;
	}
	public String getCompanys() {
		return this.companys;
	}

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

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setSeatTypes(String seatTypes) {
		this.seatTypes = seatTypes;
	}
	public String getSeatTypes() {
		return this.seatTypes;
	}

	public void setTimePeriods(String timePeriods) {
		this.timePeriods = timePeriods;
	}
	public String getTimePeriods() {
		return this.timePeriods;
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
		return "qianmi.elife.air.lines.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("companys", this.companys);
		txtParams.put("date", this.date);
		txtParams.put("from", this.from);
		txtParams.put("itemId", this.itemId);
		txtParams.put("seat_types", this.seatTypes);
		txtParams.put("time_periods", this.timePeriods);
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

	public Class<AirLinesListResponse> getResponseClass() {
		return AirLinesListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(date, "date");
		RequestCheckUtils.checkNotEmpty(from, "from");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(to, "to");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}