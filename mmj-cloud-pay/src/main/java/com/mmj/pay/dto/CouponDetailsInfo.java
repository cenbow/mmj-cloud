package com.mmj.pay.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.io.Serializable;
import java.util.Date;

/**
 * 优惠券的详细信息
 * 
 * @author shenfuding
 *
 */
public class CouponDetailsInfo implements Serializable {

	private static final long serialVersionUID = 1311327290449951165L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long userid;

	private String openid;

	private String unionid;

	private Integer couponTemplateid;

	private String couponCode;

	private Date startTime;

	private Date endTime;

	private String startTimeStr;

	private String endTimeStr;

	private Integer isUsed;

	private Integer couponType;

	private Double couponMoney;

	private String businessRemark;

	private String couponName;

	private Integer preferential;

	private Integer couponRange;

	private Integer priceType;

	private Integer alarmclock;
	
	private Integer expirydateType;

	private Integer conditionName;

	private Double conditionValue;

	private Integer rangeType;

	private String goodsBaseid;

	private String goodsSpu;

	private String categoryids;

	/**
	 * 状态类型，排序用
	 */
	private Integer statusType;

	/**
	 * 优惠券领取时间/系统发放优惠券的时间
	 */
	private Date createTime;
	
	private Date updateTime;

	/**
	 * 优惠券的显示状态，包括：多少小时后过期，未生效，已使用，已失效
	 */
	private String showStatus;

	private String url;

	/**
	 * 是否可见
	 */
	private Boolean isVisible;

	/**
	 * 优惠券来源
	 */
	private String couponSource;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

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

	public Integer getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Integer isUsed) {
		this.isUsed = isUsed;
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

	public Integer getExpirydateType() {
		return expirydateType;
	}

	public void setExpirydateType(Integer expirydateType) {
		this.expirydateType = expirydateType;
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

	public String getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(String showStatus) {
		this.showStatus = showStatus;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getStatusType() {
		return statusType;
	}

	public void setStatusType(Integer statusType) {
		this.statusType = statusType;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Boolean getIsVisible() {
		return isVisible;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public String getCouponSource() {
		return couponSource;
	}

	public void setCouponSource(String couponSource) {
		this.couponSource = couponSource;
	}

}
