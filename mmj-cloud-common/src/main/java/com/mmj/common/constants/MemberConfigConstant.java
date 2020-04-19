package com.mmj.common.constants;

public interface MemberConfigConstant {
	
	/**
	 * 庆祝买买家用户超过${200}万，该值单位：万
	 */
	String MMJ_USERS_COUNT_EXCEED = "mmj.users.count.exceed";
	
	/**
	 * 即可获得价值${188}元的会员，该值单位：元
	 */
	String MMJ_MEMBER_WORTH = "mmj.member.worth";
	
	/**
	 * 成为会员${3}天内首次下单，该值单位：天
	 */
	String MMJ_MEMBER_FIRST_ORDER_DAY_LIMIT = "mmj.member.first.order.day.limit";
	
	/**
	 * 每${周三}会员日领券，值依据java Calendar对象获取周几的数字，从周日为1开始，周一为2，...，周三为4，.... 周六为7
	 */
	String MMJ_MEMBER_DAY = "mmj.member.day";
	
	/**
	 * 限时累计消费满${50}元，该值单位：元
	 */
	String MMJ_MEMBER_CUMULATIVE_CONSUMPTION = "mmj.member.cumulative.consumption";
	
	/**
	 * 从哪一天开始进行会员活动30天的倒计时，30天的值对应key:mmj.member.activity.day.continue
	 */
	String MMJ_MEMBER_ACTIVITY_START_DATE = "mmj.member.activity.start.date";
	
	/**
	 * 活动${30}天后结束，该值单位：天
	 */
	String MMJ_MEMBER_ACTIVITY_DAY_CONTINUE = "mmj.member.activity.day.continue";
	
	/**
	 * 预留配置项，当取活动还有多少天时出现问题，立马在数据库配置此项，程序会以此配置项为准，不取计算出来的多少天
	 */
	String MMJ_MEMBER_ACTIVITY_FIXED_DAY_CONTINUE = "mmj.member.activity.fixed.day.continue";
	
}

