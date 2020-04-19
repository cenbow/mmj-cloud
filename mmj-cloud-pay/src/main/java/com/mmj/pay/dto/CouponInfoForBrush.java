package com.mmj.pay.dto;

import java.io.Serializable;

public class CouponInfoForBrush implements Serializable {

	private static final long serialVersionUID = -1804896556886185083L;

	/**
	 * 优惠券编码
	 */
	String couponCode;

	/**
	 * 优惠券名称
	 */
	private String couponName;

	/**
	 * 优惠金额，单位：元
	 */
	private double money;

	/**
	 * 多长时间后过期
	 */
	private String invalidTime;

	private String endTime;

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getInvalidTime() {
		return invalidTime;
	}

	public void setInvalidTime(String invalidTime) {
		this.invalidTime = invalidTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
