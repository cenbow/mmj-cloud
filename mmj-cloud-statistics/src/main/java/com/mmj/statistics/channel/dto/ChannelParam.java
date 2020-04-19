package com.mmj.statistics.channel.dto;

/**
 * 渠道查询参数类
 * @author shenfuding
 *
 */
public class ChannelParam {

	private String access;
	
	private String channel;
	
	private String startDate;
	
	private String endDate;

	public String getAccess() {
		return access;
	}

	public void setAccess(String access) {
		this.access = access;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
