package com.mmj.user.member.service.impl;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MemberConfigConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.BaseDict;
import com.mmj.common.utils.DateUtils;
import com.mmj.user.common.feigin.NoticeFeignClient;
import com.mmj.user.member.service.MemberConfigService;

@Service
public class MemberConfigServiceImpl implements MemberConfigService {
	
	private static final String[] arr = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
	
	private static final String CACHE_PREFIX = "DICT:TYPE&CODE:GLOBAL_CONFIG:";
	
	private static final String TIME_SUFFIX = " 00:00:00";
	
	private static final String NONE = "NONE";
	
	@Autowired
	private NoticeFeignClient noticeFeignClient;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	private int getIntValue(BaseDict baseDict, Integer defaultValue) {
		if(baseDict == null || StringUtils.isBlank(baseDict.getDictValue())) {
			return defaultValue;
		}
		return Integer.valueOf(baseDict.getDictValue());
	}
	
	private String getCacheKey(String dictCode) {
		return new StringBuilder(CACHE_PREFIX).append(dictCode).toString();
	}
	
	private BaseDict getBaseDict(String dictCode) {
		BaseDict baseDict = null;
		String cacheKey = this.getCacheKey(dictCode);
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			baseDict = JSONObject.parseObject(cacheValue, BaseDict.class);
		} else {
			baseDict = noticeFeignClient.queryGlobalConfigByDictCode(dictCode).getData();
		}
		if(baseDict != null) {
			if(MemberConfigConstant.MMJ_MEMBER_ACTIVITY_FIXED_DAY_CONTINUE.equalsIgnoreCase(dictCode) &&
					NONE.equalsIgnoreCase(baseDict.getDictValue())) {
				return null;
			} 
		}
		return baseDict;
	}
	
	public int getMmjUsersCountExceed() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_USERS_COUNT_EXCEED), 200);
	}

	public int getMmjMemberWorth() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_MEMBER_WORTH), 356);
	}

	public int getMmjMemberFirstOrderDayLimit() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_MEMBER_FIRST_ORDER_DAY_LIMIT), 3);
	}

	public int getMmjMemberDay() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_MEMBER_DAY), 4);
	}

	public String getMmjMemberDayStr() {
		int day = getMmjMemberDay();
		return arr[day-1];
	}

	public int getMmjMemberCumulativeConsumption() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_MEMBER_CUMULATIVE_CONSUMPTION), 50);
	}

	public Date getMmjMemberActivityStartDate() {
		BaseDict baseDict = getBaseDict(MemberConfigConstant.MMJ_MEMBER_ACTIVITY_START_DATE);
		if(baseDict == null) {
			throw new CustomException("会员配置缺失");
		}
		try {
			return DateUtils.SDF1.parse(baseDict.getDictValue());
		} catch (ParseException e) {
			throw new CustomException("会员配置有误"); 
		}
	}

	public int getMmjMemberActivityDayContinue() {
		return getIntValue(getBaseDict(MemberConfigConstant.MMJ_MEMBER_ACTIVITY_DAY_CONTINUE), 30);
	}

	public int getMemberActivityHowManyDaysToEnd() {
		BaseDict baseDict = getBaseDict(MemberConfigConstant.MMJ_MEMBER_ACTIVITY_FIXED_DAY_CONTINUE);
		if(baseDict != null) {
			// 说明程序启动了预留配置，页面上显示的会员活动还有多少天结束，该天数会以此值为准
			return getIntValue(baseDict, 30);
		}
		Date start = getMmjMemberActivityStartDate();
		int continueDay = getMmjMemberActivityDayContinue();
		Date end = org.apache.commons.lang.time.DateUtils.addDays(start, continueDay);
		Date now = new Date();
		int days = getDiffDays(now, end);
		if(days < 0) {
			return continueDay;
		}
		return days;
	}
	
	private static int getDiffDays(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		int day1 = cal1.get(Calendar.DAY_OF_YEAR);
		int day2 = cal2.get(Calendar.DAY_OF_YEAR);

		int year1 = cal1.get(Calendar.YEAR);
		int year2 = cal2.get(Calendar.YEAR);
		if (year1 != year2) {
			int timeDistance = 0;
			for (int i = year1; i < year2; i++) {
				if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
					timeDistance += 366;
				} else {// 不是闰年
					timeDistance += 365;
				}
			}
			return timeDistance + (day2 - day1);
		} else {
			return day2 - day1;
		}
	}

	public boolean isMemberDay() {
		int memberDay = getMmjMemberDay();
		Calendar calendar = Calendar.getInstance();
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		return weekDay == memberDay;
	}

	public long getNextMemberDayIntervalMilliseconds() {
		Calendar calendar = Calendar.getInstance();
		int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
		int memberDay = getMmjMemberDay();
		int addDay;
		if(weekDay < memberDay) {
			addDay = memberDay - weekDay;
		} else {
			addDay = 7 - weekDay + getMmjMemberDay();
		}
		
		Date now = new Date();
		Date date = org.apache.commons.lang.time.DateUtils.addDays(now, addDay);
		String nextMemberDayStr = DateUtils.SDF10.format(date) + TIME_SUFFIX;
		Date nextMemberDay = DateUtils.parse(nextMemberDayStr);
		return nextMemberDay.getTime() - now.getTime();
	}

}
