package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.CoachCreateBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.coach.createBill request
 *
 * @author auto
 * @since 1.0
 */
public class CoachCreateBillRequest implements QianmiRequest<CoachCreateBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 到达站
	 */
	private String arrStation;

	/** 
	 * 汽车票车次
	 */
	private String coachNO;

	/** 
	 * 取票人姓名
	 */
	private String contactName;

	/** 
	 * 取票人联系电话
	 */
	private String contactTel;

	/** 
	 * 出发城市
	 */
	private String departure;

	/** 
	 * 到达城市
	 */
	private String destination;

	/** 
	 * 出发时间
	 */
	private String dptDateTime;

	/** 
	 * 出发站
	 */
	private String dptStation;

	/** 
	 * 取票人二代身份证号
	 */
	private String idnumber;

	/** 
	 * 汽车票商品编号
	 */
	private String itemIdCoach;

	/** 
	 * 保险商品编号
	 */
	private String itemIdInsur;

	/** 
	 * 乘客信息,以英文逗号分隔,依次为：乘客姓名,乘客证件号码,多个乘客时以英文分号分隔，同一笔订单最多支持五个乘客
	 */
	private String passagers;

	/** 
	 * 票面价
	 */
	private String seatPrice;

	/** 
	 * 汽车站点编号，查询车次接口返回
	 */
	private String stationCode;

	public void setArrStation(String arrStation) {
		this.arrStation = arrStation;
	}
	public String getArrStation() {
		return this.arrStation;
	}

	public void setCoachNO(String coachNO) {
		this.coachNO = coachNO;
	}
	public String getCoachNO() {
		return this.coachNO;
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

	public void setDeparture(String departure) {
		this.departure = departure;
	}
	public String getDeparture() {
		return this.departure;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getDestination() {
		return this.destination;
	}

	public void setDptDateTime(String dptDateTime) {
		this.dptDateTime = dptDateTime;
	}
	public String getDptDateTime() {
		return this.dptDateTime;
	}

	public void setDptStation(String dptStation) {
		this.dptStation = dptStation;
	}
	public String getDptStation() {
		return this.dptStation;
	}

	public void setIdnumber(String idnumber) {
		this.idnumber = idnumber;
	}
	public String getIdnumber() {
		return this.idnumber;
	}

	public void setItemIdCoach(String itemIdCoach) {
		this.itemIdCoach = itemIdCoach;
	}
	public String getItemIdCoach() {
		return this.itemIdCoach;
	}

	public void setItemIdInsur(String itemIdInsur) {
		this.itemIdInsur = itemIdInsur;
	}
	public String getItemIdInsur() {
		return this.itemIdInsur;
	}

	public void setPassagers(String passagers) {
		this.passagers = passagers;
	}
	public String getPassagers() {
		return this.passagers;
	}

	public void setSeatPrice(String seatPrice) {
		this.seatPrice = seatPrice;
	}
	public String getSeatPrice() {
		return this.seatPrice;
	}

	public void setStationCode(String stationCode) {
		this.stationCode = stationCode;
	}
	public String getStationCode() {
		return this.stationCode;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.coach.createBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("arrStation", this.arrStation);
		txtParams.put("coachNO", this.coachNO);
		txtParams.put("contactName", this.contactName);
		txtParams.put("contactTel", this.contactTel);
		txtParams.put("departure", this.departure);
		txtParams.put("destination", this.destination);
		txtParams.put("dptDateTime", this.dptDateTime);
		txtParams.put("dptStation", this.dptStation);
		txtParams.put("idnumber", this.idnumber);
		txtParams.put("itemIdCoach", this.itemIdCoach);
		txtParams.put("itemIdInsur", this.itemIdInsur);
		txtParams.put("passagers", this.passagers);
		txtParams.put("seatPrice", this.seatPrice);
		txtParams.put("stationCode", this.stationCode);
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

	public Class<CoachCreateBillResponse> getResponseClass() {
		return CoachCreateBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(arrStation, "arrStation");
		RequestCheckUtils.checkNotEmpty(coachNO, "coachNO");
		RequestCheckUtils.checkNotEmpty(contactName, "contactName");
		RequestCheckUtils.checkNotEmpty(contactTel, "contactTel");
		RequestCheckUtils.checkNotEmpty(departure, "departure");
		RequestCheckUtils.checkNotEmpty(destination, "destination");
		RequestCheckUtils.checkNotEmpty(dptDateTime, "dptDateTime");
		RequestCheckUtils.checkNotEmpty(dptStation, "dptStation");
		RequestCheckUtils.checkNotEmpty(idnumber, "idnumber");
		RequestCheckUtils.checkNotEmpty(itemIdCoach, "itemIdCoach");
		RequestCheckUtils.checkNotEmpty(passagers, "passagers");
		RequestCheckUtils.checkNotEmpty(stationCode, "stationCode");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}