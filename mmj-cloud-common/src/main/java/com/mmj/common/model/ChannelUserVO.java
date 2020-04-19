package com.mmj.common.model;


/**
 * 渠道用户数据统计
 * @author shenfuding
 *
 */
public class ChannelUserVO implements Comparable<ChannelUserVO>{
	
	private Long userId;
	
	private String openId;
	
	private String unionId;
	
	private String createTime;
	
	private String channel;
	
	private String authorized;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getAuthorized() {
		return authorized;
	}

	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}

	@Override
	public int compareTo(ChannelUserVO o) {
		return this.createTime.compareTo(o.getCreateTime());
	}

}
