package com.mmj.pay.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class CouponTemplate implements Serializable {

	private static final long serialVersionUID = 2800897307262183605L;

	private Integer couponTemplateid;

	private String businessRemark;

	private String couponName;

	private Integer preferential;

	private Integer couponRange;

	private Integer priceType;

	private Integer totalCount;

	private Integer overplusCount;

	private Integer expirydateType;

	private Integer dayTotal;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date startTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date endTime;

	private Integer dayQuantity;

	private String timeType;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date deadlineTime;

	private Integer timeQuantity;

	private Integer alarmclock;

	private Boolean isShowGoodsdetail;

    private String activity;

    private Boolean isEnable;

    private Date calcTime;

    private Boolean isForMember;

	private Date createTime;

	private Integer createBy;

	private Date updateTime;

	private Integer updateBy;

	private String preferentialRemark;

	private String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDayTotal() {
		return dayTotal;
	}

	public void setDayTotal(Integer dayTotal) {
		this.dayTotal = dayTotal;
	}

	public String getPreferentialRemark() {
		return preferentialRemark;
	}

	public void setPreferentialRemark(String preferentialRemark) {
		this.preferentialRemark = preferentialRemark;
	}

	public Integer getCouponTemplateid() {
		return couponTemplateid;
	}

	public void setCouponTemplateid(Integer couponTemplateid) {
		this.couponTemplateid = couponTemplateid;
	}

	public String getBusinessRemark() {
		return businessRemark;
	}

	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark == null ? null : businessRemark
				.trim();
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName == null ? null : couponName.trim();
	}

	public Integer getPreferential() {
		return preferential;
	}

	public void setPreferential(Integer preferential) {
		this.preferential = preferential;
	}

	public Integer getCouponRange() {
		return couponRange;
	}

	public void setCouponRange(Integer couponRange) {
		this.couponRange = couponRange;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getOverplusCount() {
		return overplusCount;
	}

	public void setOverplusCount(Integer overplusCount) {
		this.overplusCount = overplusCount;
	}

	public Integer getExpirydateType() {
		return expirydateType==null?0:expirydateType;
	}

	public void setExpirydateType(Integer expirydateType) {
		this.expirydateType = expirydateType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getDayQuantity() {
		return dayQuantity;
	}

	public void setDayQuantity(Integer dayQuantity) {
		this.dayQuantity = dayQuantity;
	}

	public String getTimeType() {
		return timeType;
	}

	public void setTimeType(String timeType) {
		this.timeType = timeType == null ? null : timeType.trim();
	}

	public Date getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(Date deadlineTime) {
		this.deadlineTime = deadlineTime;
	}

	public Integer getTimeQuantity() {
		return timeQuantity;
	}

	public void setTimeQuantity(Integer timeQuantity) {
		this.timeQuantity = timeQuantity;
	}

	public Integer getAlarmclock() {
		return alarmclock;
	}

	public void setAlarmclock(Integer alarmclock) {
		this.alarmclock = alarmclock;
	}

	public Boolean getIsShowGoodsdetail() {
		return isShowGoodsdetail;
	}

	public void setIsShowGoodsdetail(Boolean isShowGoodsdetail) {
		this.isShowGoodsdetail = isShowGoodsdetail;
	}

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public Boolean getIsEnable() {
		return isEnable;
	}

	public void setIsEnable(Boolean isEnable) {
		this.isEnable = isEnable;
	}

	public Boolean getIsForMember() {
		return isForMember;
	}

	public void setIsForMember(Boolean isForMember) {
		this.isForMember = isForMember;
	}

	public Date getCalcTime() {
		return calcTime;
	}

	public void setCalcTime(Date calcTime) {
		this.calcTime = calcTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Integer createBy) {
		this.createBy = createBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Integer updateBy) {
		this.updateBy = updateBy;
	}
}