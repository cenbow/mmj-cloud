package com.mmj.active.prizewheels.dto;

import java.io.Serializable;

public class MyPrizeDto implements Serializable {

	private static final long serialVersionUID = -7168171821945965204L;

	private String prizeName;
	
	private String prizeType;
	
	private String gotPrizeTime;
	
	private String amount;
	
	private String iconUrl;

	public String getPrizeName() {
		return prizeName;
	}

	public void setPrizeName(String prizeName) {
		this.prizeName = prizeName;
	}

	public String getPrizeType() {
		return prizeType;
	}

	public void setPrizeType(String prizeType) {
		this.prizeType = prizeType;
	}

	public String getGotPrizeTime() {
		return gotPrizeTime;
	}

	public void setGotPrizeTime(String gotPrizeTime) {
		this.gotPrizeTime = gotPrizeTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
}
