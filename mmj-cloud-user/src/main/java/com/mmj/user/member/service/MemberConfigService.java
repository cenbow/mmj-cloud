package com.mmj.user.member.service;

import java.text.ParseException;
import java.util.Date;

/**
 * 会员全局配置服务
 * @author george
 *
 */
public interface MemberConfigService {
	
	/**
	 * 获取买买家用户超过的数量<br/>
	 * 如：庆祝买买家用户超过${200}万，该值单位：万<br/>
	 * 对应sys_config表中的key: mmj.users.count.exceed
	 * @return
	 */
	int getMmjUsersCountExceed();
	
	/**
	 * 获取会员价值多少钱<br/>
	 * 如：即可获得价值${188}元的会员，该值单位：元<br/>
	 * 对应sys_config表中的key: mmj.member.worth
	 * @return
	 */
	int getMmjMemberWorth();
	
	/**
	 * 获取成为会员首次下单的天数限制<br/>
	 * 如：成为会员${3}天内首次下单，该值单位：天
	 * 对应sys_config表中的key: mmj.member.first.order.day.limit
	 * @return
	 */
	int getMmjMemberFirstOrderDayLimit();
	
	/**
	 * 获取会员日<br/>
	 * 如：每${周三}会员日领券，值依据java Calendar对象获取周几的数字，从周日为1开始，周一为2，...，周三为4，.... 周六为7<br/>
	 * 对应sys_config表中的key: mmj.member.day
	 * @return 如周三就返回4
	 */
	int getMmjMemberDay();
	
	/**
	 * 获取会员日是周几<br/>
	 * 返回周一/周二/周三/周四/周五/周六/周日中的一天<br/>
	 * 对应sys_config表中的key: mmj.member.day
	 * @return 默认返回周三
	 */
	String getMmjMemberDayStr();
	
	/**
	 * 获取成为会员需要累计消费多少元
	 * 如: 限时累计消费满${50}元，该值单位：元
	 * 对应sys_config表中的key: mmj.member.cumulative.consumption
	 * @return
	 */
	int getMmjMemberCumulativeConsumption();
	
	/**
	 * 获取从哪一天开始进行会员活动30天的倒计时<br/>
	 * 30天的值对应key:mmj.member.activity.day.continue<br/>
	 * @return  返回日期 yyyy-MM-dd
	 * @throws ParseException 
	 */
	Date getMmjMemberActivityStartDate();
	
	/**
	 * 获取活动的持续天数
	 * 如：活动${30}天后结束，该值单位：天，是固定值，如果要动态取还剩多少天结束请调用方法getMemberActivityHowManyDaysToEnd()
	 * @return
	 */
	int getMmjMemberActivityDayContinue();
	
	/**
	 * 获取会员活动还有多少天结束
	 * @return
	 */
	int getMemberActivityHowManyDaysToEnd();
	
	/**
	 * 判断今天是否是会员日
	 * @return
	 */
	boolean isMemberDay();
	
	/**
	 * 获取当前时间距离下个会员日的毫秒数
	 * @return
	 */
	long getNextMemberDayIntervalMilliseconds();
	
}
