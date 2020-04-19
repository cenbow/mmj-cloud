package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.AirSeatsLeftResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.air.seats.left request
 *
 * @author auto
 * @since 1.0
 */
public class AirSeatsLeftRequest implements QianmiRequest<AirSeatsLeftResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 出发日期
	 */
	private String date;

	/** 
	 * 航班号
	 */
	private String flightNo;

	/** 
	 * 出发机场三字码
	 */
	private String from;

	/** 
	 * 飞机票标准商品编号
	 */
	private String itemId;

	/** 
	 * 舱位代码
	 */
	private String seatCode;

	/** 
	 * 抵达机场三字码
	 */
	private String to;

	public void setDate(String date) {
		this.date = date;
	}
	public String getDate() {
		return this.date;
	}

	public void setFlightNo(String flightNo) {
		this.flightNo = flightNo;
	}
	public String getFlightNo() {
		return this.flightNo;
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

	public void setSeatCode(String seatCode) {
		this.seatCode = seatCode;
	}
	public String getSeatCode() {
		return this.seatCode;
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
		return "qianmi.elife.air.seats.left";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("date", this.date);
		txtParams.put("flightNo", this.flightNo);
		txtParams.put("from", this.from);
		txtParams.put("itemId", this.itemId);
		txtParams.put("seatCode", this.seatCode);
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

	public Class<AirSeatsLeftResponse> getResponseClass() {
		return AirSeatsLeftResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(date, "date");
		RequestCheckUtils.checkNotEmpty(flightNo, "flightNo");
		RequestCheckUtils.checkNotEmpty(from, "from");
		RequestCheckUtils.checkNotEmpty(seatCode, "seatCode");
		RequestCheckUtils.checkNotEmpty(to, "to");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}