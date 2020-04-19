package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmTrafficFinePayBillResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.trafficFine.payBill request
 *
 * @author auto
 * @since 1.0
 */
public class BmTrafficFinePayBillRequest implements QianmiRequest<BmTrafficFinePayBillResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 回调地址
	 */
	private String callback;

	/** 
	 * 车牌号码
	 */
	private String carNo;

	/** 
	 * 车主姓名
	 */
	private String carOwnerName;

	/** 
	 * 手机号
	 */
	private String carOwnerTel;

	/** 
	 * 车辆类型
	 */
	private String carType;

	/** 
	 * 验证码
	 */
	private String checkCode;

	/** 
	 * 滞纳金
	 */
	private String delayFee;

	/** 
	 * 发动机号
	 */
	private String engineId;

	/** 
	 * 罚金
	 */
	private String fineFee;

	/** 
	 * 罚单编号
	 */
	private String fineNo;

	/** 
	 * 处罚时间
	 */
	private String fineTime;

	/** 
	 * 罚款单号
	 */
	private String fineUnique;

	/** 
	 * 车架号
	 */
	private String frameId;

	/** 
	 * 标准商品编号
	 */
	private String itemId;

	/** 
	 * 外部订单号
	 */
	private String outerTid;

	/** 
	 * 项目单号
	 */
	private String projectNum;

	/** 
	 * 代办费
	 */
	private String proxyFee;

	/** 
	 * 总金额
	 */
	private String totalAmount;

	/** 
	 * 主键
	 */
	private String unique;

	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getCallback() {
		return this.callback;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}
	public String getCarNo() {
		return this.carNo;
	}

	public void setCarOwnerName(String carOwnerName) {
		this.carOwnerName = carOwnerName;
	}
	public String getCarOwnerName() {
		return this.carOwnerName;
	}

	public void setCarOwnerTel(String carOwnerTel) {
		this.carOwnerTel = carOwnerTel;
	}
	public String getCarOwnerTel() {
		return this.carOwnerTel;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}
	public String getCarType() {
		return this.carType;
	}

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}
	public String getCheckCode() {
		return this.checkCode;
	}

	public void setDelayFee(String delayFee) {
		this.delayFee = delayFee;
	}
	public String getDelayFee() {
		return this.delayFee;
	}

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}
	public String getEngineId() {
		return this.engineId;
	}

	public void setFineFee(String fineFee) {
		this.fineFee = fineFee;
	}
	public String getFineFee() {
		return this.fineFee;
	}

	public void setFineNo(String fineNo) {
		this.fineNo = fineNo;
	}
	public String getFineNo() {
		return this.fineNo;
	}

	public void setFineTime(String fineTime) {
		this.fineTime = fineTime;
	}
	public String getFineTime() {
		return this.fineTime;
	}

	public void setFineUnique(String fineUnique) {
		this.fineUnique = fineUnique;
	}
	public String getFineUnique() {
		return this.fineUnique;
	}

	public void setFrameId(String frameId) {
		this.frameId = frameId;
	}
	public String getFrameId() {
		return this.frameId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getItemId() {
		return this.itemId;
	}

	public void setOuterTid(String outerTid) {
		this.outerTid = outerTid;
	}
	public String getOuterTid() {
		return this.outerTid;
	}

	public void setProjectNum(String projectNum) {
		this.projectNum = projectNum;
	}
	public String getProjectNum() {
		return this.projectNum;
	}

	public void setProxyFee(String proxyFee) {
		this.proxyFee = proxyFee;
	}
	public String getProxyFee() {
		return this.proxyFee;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getTotalAmount() {
		return this.totalAmount;
	}

	public void setUnique(String unique) {
		this.unique = unique;
	}
	public String getUnique() {
		return this.unique;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.trafficFine.payBill";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("callback", this.callback);
		txtParams.put("carNo", this.carNo);
		txtParams.put("carOwnerName", this.carOwnerName);
		txtParams.put("carOwnerTel", this.carOwnerTel);
		txtParams.put("carType", this.carType);
		txtParams.put("checkCode", this.checkCode);
		txtParams.put("delayFee", this.delayFee);
		txtParams.put("engineId", this.engineId);
		txtParams.put("fineFee", this.fineFee);
		txtParams.put("fineNo", this.fineNo);
		txtParams.put("fineTime", this.fineTime);
		txtParams.put("fineUnique", this.fineUnique);
		txtParams.put("frameId", this.frameId);
		txtParams.put("itemId", this.itemId);
		txtParams.put("outerTid", this.outerTid);
		txtParams.put("projectNum", this.projectNum);
		txtParams.put("proxyFee", this.proxyFee);
		txtParams.put("totalAmount", this.totalAmount);
		txtParams.put("unique", this.unique);
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

	public Class<BmTrafficFinePayBillResponse> getResponseClass() {
		return BmTrafficFinePayBillResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(carNo, "carNo");
		RequestCheckUtils.checkNotEmpty(carType, "carType");
		RequestCheckUtils.checkNotEmpty(delayFee, "delayFee");
		RequestCheckUtils.checkNotEmpty(engineId, "engineId");
		RequestCheckUtils.checkNotEmpty(fineFee, "fineFee");
		RequestCheckUtils.checkNotEmpty(itemId, "itemId");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}