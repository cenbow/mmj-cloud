package com.mmj.pay.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券的详细信息
 * 
 * @author shenfuding
 *
 */
public class CouponTemplateDetailsInfo implements Serializable {

	private static final long serialVersionUID = 1311327290449951165L;

	private Integer couponTemplateid;

	private String couponCode;

	private Date startTime;

	private Date endTime;

	private String startTimeStr;

	private String endTimeStr;

	private Integer couponType;

	private Double couponMoney;

	private String businessRemark;

	private String couponName;

	private Integer preferential;

	private Integer couponRange;

	private Integer priceType;
	
	private Integer dayTotal;

	private Integer totalCount;

	private Integer overplusCount;

	private Integer alarmclock;

	private Integer conditionName;

	private Double conditionValue;

	private Integer rangeType;

	private String goodsBaseid;

	private String goodsSpu;

	private String categoryids;

	private Date createTime;

	private Date updateTime;

	private Integer expirydateType;
	private Integer dayQuantity;

	private String timeType;

	private Date deadlineTime;

	private Integer timeQuantity;

	private Boolean isShowGoodsdetail;

    private String activity;

    private Boolean isEnable;

    private Date calcTime;

    private Boolean isForMember;

    /**
     * 使用描述：如满50可用、无使用门槛等
     */
    private String useDesc;

    /**
     * 有效期描述
     */
    private String validTimeDesc;

    /**
     * 是否已领取
     */
    private boolean hasCollected;

    /**
     * 剩余数量所占的百分比
     */
    private String percent;

    /**
     * 剩余数量所占的百分比描述
     */
    private String percentStr;

	public Integer getCouponTemplateid() {
		return couponTemplateid;
	}

	public void setCouponTemplateid(Integer couponTemplateid) {
		this.couponTemplateid = couponTemplateid;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
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

	public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Double getCouponMoney() {
		return couponMoney;
	}

	public void setCouponMoney(Double couponMoney) {
		this.couponMoney = couponMoney;
	}

	public String getBusinessRemark() {
		return businessRemark;
	}

	public void setBusinessRemark(String businessRemark) {
		this.businessRemark = businessRemark;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
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

	public Integer getDayTotal() {
		return dayTotal;
	}

	public void setDayTotal(Integer dayTotal) {
		this.dayTotal = dayTotal;
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

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getAlarmclock() {
		return alarmclock;
	}

	public void setAlarmclock(Integer alarmclock) {
		this.alarmclock = alarmclock;
	}

	public Integer getConditionName() {
		return conditionName;
	}

	public void setConditionName(Integer conditionName) {
		this.conditionName = conditionName;
	}

	public Double getConditionValue() {
		return conditionValue;
	}

	public void setConditionValue(Double conditionValue) {
		this.conditionValue = conditionValue;
	}

	public Integer getRangeType() {
		return rangeType;
	}

	public void setRangeType(Integer rangeType) {
		this.rangeType = rangeType;
	}

	public String getGoodsBaseid() {
		return goodsBaseid;
	}

	public void setGoodsBaseid(String goodsBaseid) {
		this.goodsBaseid = goodsBaseid;
	}

	public String getGoodsSpu() {
		return goodsSpu;
	}

	public void setGoodsSpu(String goodsSpu) {
		this.goodsSpu = goodsSpu;
	}

	public String getCategoryids() {
		return categoryids;
	}

	public void setCategoryids(String categoryids) {
		this.categoryids = categoryids;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getExpirydateType() {
		return expirydateType;
	}

	public void setExpirydateType(Integer expirydateType) {
		this.expirydateType = expirydateType;
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
		this.timeType = timeType;
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

	public String getStartTimeStr() {
		return startTimeStr;
	}

	public void setStartTimeStr(String startTimeStr) {
		this.startTimeStr = startTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
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

	public Date getCalcTime() {
		return calcTime;
	}

	public void setCalcTime(Date calcTime) {
		this.calcTime = calcTime;
	}

	public Boolean getIsForMember() {
		return isForMember;
	}

	public void setIsForMember(Boolean isForMember) {
		this.isForMember = isForMember;
	}

	public String getUseDesc() {
		return useDesc;
	}

	public void setUseDesc(String useDesc) {
		this.useDesc = useDesc;
	}

	public String getValidTimeDesc() {
		return validTimeDesc;
	}

	public void setValidTimeDesc(String validTimeDesc) {
		this.validTimeDesc = validTimeDesc;
	}

	public boolean isHasCollected() {
		return hasCollected;
	}

	public void setHasCollected(boolean hasCollected) {
		this.hasCollected = hasCollected;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getPercentStr() {
		return percentStr;
	}

	public void setPercentStr(String percentStr) {
		this.percentStr = percentStr;
	}

}
