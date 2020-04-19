package com.qianmi.open.api.domain.elife;

import com.qianmi.open.api.QianmiObject;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * 交罚详情
 *
 * @author auto
 * @since 2.0
 */
public class TrafficFineInfo extends QianmiObject {

	private static final long serialVersionUID = 1L;

	/**
	 * 是否可缴费
	 */
	@ApiField("canPay")
	private String canPay;

	/**
	 * 不可缴费原因
	 */
	@ApiField("canPayMsg")
	private String canPayMsg;

	/**
	 * 滞纳金
	 */
	@ApiField("delayFee")
	private String delayFee;

	/**
	 * 违章城市
	 */
	@ApiField("fineCity")
	private String fineCity;

	/**
	 * 违章代码
	 */
	@ApiField("fineCode")
	private String fineCode;

	/**
	 * 扣分
	 */
	@ApiField("fineDeductPoints")
	private String fineDeductPoints;

	/**
	 * 违章详情
	 */
	@ApiField("fineDetail")
	private String fineDetail;

	/**
	 * 罚金
	 */
	@ApiField("fineFee")
	private String fineFee;

	/**
	 * 违章地点
	 */
	@ApiField("fineLocation")
	private String fineLocation;

	/**
	 * 违章项文书编号
	 */
	@ApiField("fineNo")
	private String fineNo;

	/**
	 * 违章时间
	 */
	@ApiField("fineTime")
	private String fineTime;

	/**
	 * 第三方平台代办费
	 */
	@ApiField("proxyFee")
	private String proxyFee;

	/**
	 * 查询唯一识别码，下单必传
	 */
	@ApiField("unique")
	private String unique;

	public String getCanPay() {
		return this.canPay;
	}
	public void setCanPay(String canPay) {
		this.canPay = canPay;
	}

	public String getCanPayMsg() {
		return this.canPayMsg;
	}
	public void setCanPayMsg(String canPayMsg) {
		this.canPayMsg = canPayMsg;
	}

	public String getDelayFee() {
		return this.delayFee;
	}
	public void setDelayFee(String delayFee) {
		this.delayFee = delayFee;
	}

	public String getFineCity() {
		return this.fineCity;
	}
	public void setFineCity(String fineCity) {
		this.fineCity = fineCity;
	}

	public String getFineCode() {
		return this.fineCode;
	}
	public void setFineCode(String fineCode) {
		this.fineCode = fineCode;
	}

	public String getFineDeductPoints() {
		return this.fineDeductPoints;
	}
	public void setFineDeductPoints(String fineDeductPoints) {
		this.fineDeductPoints = fineDeductPoints;
	}

	public String getFineDetail() {
		return this.fineDetail;
	}
	public void setFineDetail(String fineDetail) {
		this.fineDetail = fineDetail;
	}

	public String getFineFee() {
		return this.fineFee;
	}
	public void setFineFee(String fineFee) {
		this.fineFee = fineFee;
	}

	public String getFineLocation() {
		return this.fineLocation;
	}
	public void setFineLocation(String fineLocation) {
		this.fineLocation = fineLocation;
	}

	public String getFineNo() {
		return this.fineNo;
	}
	public void setFineNo(String fineNo) {
		this.fineNo = fineNo;
	}

	public String getFineTime() {
		return this.fineTime;
	}
	public void setFineTime(String fineTime) {
		this.fineTime = fineTime;
	}

	public String getProxyFee() {
		return this.proxyFee;
	}
	public void setProxyFee(String proxyFee) {
		this.proxyFee = proxyFee;
	}

	public String getUnique() {
		return this.unique;
	}
	public void setUnique(String unique) {
		this.unique = unique;
	}

}