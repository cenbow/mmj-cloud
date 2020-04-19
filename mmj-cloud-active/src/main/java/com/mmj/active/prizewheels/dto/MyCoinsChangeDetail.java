package com.mmj.active.prizewheels.dto;

/**
 * 我的买买币明细
 * @author shenfuding
 *
 */
public class MyCoinsChangeDetail {
	
	/**
	 * 获得方式
	 */
	private String gotWay;
	
	/**
	 * 获得时间
	 */
	private String gotTime;
	
	/**
	 * 变动数量
	 */
	private String increaseAmount;
	
	/**
	 * 实时余额
	 */
	private Integer coinsBalance;

	public String getGotWay() {
		return gotWay;
	}

	public void setGotWay(String gotWay) {
		this.gotWay = gotWay;
	}

	public String getGotTime() {
		return gotTime;
	}

	public void setGotTime(String gotTime) {
		this.gotTime = gotTime;
	}

	public String getIncreaseAmount() {
		return increaseAmount;
	}

	public void setIncreaseAmount(String increaseAmount) {
		this.increaseAmount = increaseAmount;
	}

	public Integer getCoinsBalance() {
		return coinsBalance;
	}

	public void setCoinsBalance(Integer coinsBalance) {
		this.coinsBalance = coinsBalance;
	}

}
