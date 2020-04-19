package com.mmj.active.prizewheels.dto;

import java.io.Serializable;
import java.util.Date;

public class WithdrawRecordDto implements Serializable, Comparable<WithdrawRecordDto> {
	
	private static final long serialVersionUID = -7554071332868604249L;

	/**
	 * 提现人的昵称
	 */
	private String nickname;
	
	/**
	 * 提现金额
	 */
	private String withdrawMoney;
	
	/**
	 * 提现时间
	 */
	private Date createTime;

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public int compareTo(WithdrawRecordDto o) {
		if(this.getCreateTime().before(o.getCreateTime())) {
			return 1;
		} else {
			return -1;
		}
	}
}
