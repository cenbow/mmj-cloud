package com.mmj.pay.dto;

import java.util.Date;

/**
 * 优惠券/或者优惠券模版的生效时间段
 * 
 * @author shenfuding
 *
 */
public class CouponValidTimePeriod {

	/**
	 * 优惠券模版ID
	 */
	private Integer couponTemplateid;

	/**
	 * 优惠券编码
	 */
	private String couponCode;

	/**
	 * 用户微信标识
	 */
	private String openid;

	/**
	 * 优惠券生效时间
	 */
	private Date startTime;

	/**
	 * 优惠券失效时间
	 */
	private Date endTime;

	public Integer getCouponTemplateid() {
		return couponTemplateid;
	}

	public void setCouponTemplateid(Integer couponTemplateid) {
		this.couponTemplateid = couponTemplateid;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
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

}
