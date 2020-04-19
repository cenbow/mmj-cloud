package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.TrainOrderCreateResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: qianmi.elife.train.order.create request
 *
 * @author auto
 * @since 1.0
 */
public class TrainOrderCreateRequest implements QianmiRequest<TrainOrderCreateResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 在线选座坐席
	 */
	private String chooseSeats;

	/** 
	 * 联系人姓名
	 */
	private String contactName;

	/** 
	 * 联系人电话
	 */
	private String contactTel;

	/** 
	 * 出发日期
	 */
	private String date;

	/** 
	 * 起始站中文
	 */
	private String from;

	/** 
	 * 保险商品编号
	 */
	private String itemIdInsur;

	/** 
	 * 火车票商品编号
	 */
	private String itemIdTrain;

	/** 
	 * 乘车人信息
	 */
	private String passagers;

	/** 
	 * 发车时间 eg 00:05
	 */
	private String startTime;

	/** 
	 * 终点站中文
	 */
	private String to;

	/** 
	 * 车次号：如G11
	 */
	private String trainNumber;

	public void setChooseSeats(String chooseSeats) {
		this.chooseSeats = chooseSeats;
	}
	public String getChooseSeats() {
		return this.chooseSeats;
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

	public void setFrom(String from) {
		this.from = from;
	}
	public String getFrom() {
		return this.from;
	}

	public void setItemIdInsur(String itemIdInsur) {
		this.itemIdInsur = itemIdInsur;
	}
	public String getItemIdInsur() {
		return this.itemIdInsur;
	}

	public void setItemIdTrain(String itemIdTrain) {
		this.itemIdTrain = itemIdTrain;
	}
	public String getItemIdTrain() {
		return this.itemIdTrain;
	}

	public void setPassagers(String passagers) {
		this.passagers = passagers;
	}
	public String getPassagers() {
		return this.passagers;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return this.startTime;
	}

	public void setTo(String to) {
		this.to = to;
	}
	public String getTo() {
		return this.to;
	}

	public void setTrainNumber(String trainNumber) {
		this.trainNumber = trainNumber;
	}
	public String getTrainNumber() {
		return this.trainNumber;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "qianmi.elife.train.order.create";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("chooseSeats", this.chooseSeats);
		txtParams.put("contactName", this.contactName);
		txtParams.put("contactTel", this.contactTel);
		txtParams.put("date", this.date);
		txtParams.put("from", this.from);
		txtParams.put("itemIdInsur", this.itemIdInsur);
		txtParams.put("itemIdTrain", this.itemIdTrain);
		txtParams.put("passagers", this.passagers);
		txtParams.put("startTime", this.startTime);
		txtParams.put("to", this.to);
		txtParams.put("trainNumber", this.trainNumber);
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

	public Class<TrainOrderCreateResponse> getResponseClass() {
		return TrainOrderCreateResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(contactName, "contactName");
		RequestCheckUtils.checkNotEmpty(contactTel, "contactTel");
		RequestCheckUtils.checkNotEmpty(date, "date");
		RequestCheckUtils.checkNotEmpty(from, "from");
		RequestCheckUtils.checkNotEmpty(itemIdTrain, "itemIdTrain");
		RequestCheckUtils.checkNotEmpty(passagers, "passagers");
		RequestCheckUtils.checkNotEmpty(startTime, "startTime");
		RequestCheckUtils.checkNotEmpty(to, "to");
		RequestCheckUtils.checkNotEmpty(trainNumber, "trainNumber");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}