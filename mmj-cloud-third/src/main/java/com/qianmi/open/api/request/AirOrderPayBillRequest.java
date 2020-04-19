package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.AirOrderPayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.air.order.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class AirOrderPayBillRequest implements QianmiRequest<AirOrderPayBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 航空公司三字碼
	 */
	private String companyCode;

	/** 
	 * 订票联系人
	 */
	private String contactName;

	/** 
	 * 联系电话
	 */
	private String contactTel;

	/** 
	 * 出发日期
	 */
	private String date;

	/** 
	 * 航班编号
	 */
	private String flightNo;

	/** 
	 * 出发站点三字码
	 */
	private String from;

	/** 
	 * 飞机票商品编号
	 */
	private String itemId;

	/** 
	 * 乘客信息
	 */
	private String passagers;

	/** 
	 * 舱位编码
	 */
	private String seatCode;

	/** 
	 * 抵达站点三字码
	 */
	private String to;

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}
	public String getCompanyCode() {
		return this.companyCode;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactName() {
		return this.contactName;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}
	public String getContactTel() {
		return this.contactTel;
	}

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

	public void setPassagers(String passagers) {
		this.passagers = passagers;
	}
	public String getPassagers() {
		return this.passagers;
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
		return "qianmi.elife.air.order.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("companyCode", this.companyCode);
		txtParams.put("contactName", this.contactName);
		txtParams.put("contactTel", this.contactTel);
		txtParams.put("date", this.date);
		txtParams.put("flightNo", this.flightNo);
		txtParams.put("from", this.from);
		txtParams.put("itemId", this.itemId);
		txtParams.put("passagers", this.passagers);
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

	public Class<AirOrderPayBillResponse> getResponseClass() {
		return AirOrderPayBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(companyCode, "companyCode");
		RequestCheckUtils.checkNotEmpty(contactName, "contactName");
		RequestCheckUtils.checkNotEmpty(contactTel, "contactTel");
		RequestCheckUtils.checkNotEmpty(date, "date");
		RequestCheckUtils.checkNotEmpty(flightNo, "flightNo");
		RequestCheckUtils.checkNotEmpty(from, "from");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
		RequestCheckUtils.checkNotEmpty(seatCode, "seatCode");
		RequestCheckUtils.checkNotEmpty(to, "to");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}