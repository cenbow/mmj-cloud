package com.qianmi.open.api.request;

import com.qianmi.open.api.ApiRuleException;
import com.qianmi.open.api.QianmiRequest;
import com.qianmi.open.api.response.BmRechargeMobileCorrectListResponse;
import com.qianmi.open.api.tool.util.QianmiHashMap;
import com.qianmi.open.api.tool.util.RequestCheckUtils;

import java.util.Map;

/**
 * API: bm.elife.recharge.mobile.correct.list request
 *
 * @author auto
 * @since 1.0
 */
public class BmRechargeMobileCorrectListRequest implements QianmiRequest<BmRechargeMobileCorrectListResponse> {

    private Map<String, String> headerMap = new QianmiHashMap();
	private QianmiHashMap udfParams; // add user-defined text parameters
	private Long timestamp;

	/** 
	 * 关联订单编号
	 */
	private String billId;

	/** 
	 * 冲正状态 1-待冲正 4-冲正失败 5-冲正成功
	 */
	private String correctStatus;

	/** 
	 * 按冲正订单发起时间排序方式 0-升序 1-降序(默认)
	 */
	private String createdSort;

	/** 
	 * 订单结束时间
	 */
	private String endTime;

	/** 
	 * 冲正手机号码
	 */
	private String mobileNo;

	/** 
	 * 页码,0开始
	 */
	private Integer pageNo;

	/** 
	 * 返回条数
	 */
	private Integer pageSize;

	/** 
	 * 订单开始时间
	 */
	private String startTime;

	public void setBillId(String billId) {
		this.billId = billId;
	}
	public String getBillId() {
		return this.billId;
	}

	public void setCorrectStatus(String correctStatus) {
		this.correctStatus = correctStatus;
	}
	public String getCorrectStatus() {
		return this.correctStatus;
	}

	public void setCreatedSort(String createdSort) {
		this.createdSort = createdSort;
	}
	public String getCreatedSort() {
		return this.createdSort;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getEndTime() {
		return this.endTime;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public String getMobileNo() {
		return this.mobileNo;
	}

	public void setPageNo(Integer pageNo) {
		this.pageNo = pageNo;
	}
	public Integer getPageNo() {
		return this.pageNo;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getPageSize() {
		return this.pageSize;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStartTime() {
		return this.startTime;
	}

    public Long getTimestamp() {
    	return this.timestamp;
    }
    public void setTimestamp(Long timestamp) {
    	this.timestamp = timestamp;
    }

	public String getApiMethodName() {
		return "bm.elife.recharge.mobile.correct.list";
	}

	public Map<String, String> getTextParams() {
		QianmiHashMap txtParams = new QianmiHashMap();
		txtParams.put("billId", this.billId);
		txtParams.put("correctStatus", this.correctStatus);
		txtParams.put("createdSort", this.createdSort);
		txtParams.put("endTime", this.endTime);
		txtParams.put("mobileNo", this.mobileNo);
		txtParams.put("pageNo", this.pageNo);
		txtParams.put("pageSize", this.pageSize);
		txtParams.put("startTime", this.startTime);
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

	public Class<BmRechargeMobileCorrectListResponse> getResponseClass() {
		return BmRechargeMobileCorrectListResponse.class;
	}

	public void check() throws ApiRuleException {
		RequestCheckUtils.checkNotEmpty(billId, "billId");
		RequestCheckUtils.checkNotEmpty(mobileNo, "mobileNo");
    }

	public Map<String, String> getHeaderMap() {
        return headerMap;
    }
}