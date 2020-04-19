package com.mmj.common.model;

import java.util.Set;

import lombok.Data;

/**
 * 渠道数据统计专用
 * @author shenfuding
 *
 */
@Data
public class UserOrderStatisticsParam {
	
	/**
	 * 用户ID集合
	 */
	private Set<Long> userIdSet;
	
	/**
	 * 开始时间
	 */
	private String startTime;
	
	/**
	 * 截止时间
	 */
	private String endTime;

}
