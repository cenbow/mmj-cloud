package com.mmj.pay.utils;


import com.mmj.common.constants.CommonConstant;
import com.mmj.pay.dto.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 优惠券相关功能的工具类
 * 
 * @author shenfuding
 *
 */
public class CouponUtil {

	private static Logger logger = LoggerFactory.getLogger(CouponUtil.class);

	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final String DATE_FORMAT2 = "yyyy年MM月dd日 HH:mm:ss";

	public static final SimpleDateFormat DATEFORMAT = new SimpleDateFormat(
			CouponUtil.DATE_FORMAT);

	public static final SimpleDateFormat DATEFORMAT2 = new SimpleDateFormat(
			CouponUtil.DATE_FORMAT2);

	/**
	 * 计算优惠券模版的生效时间和结束时间
	 * 
	 * @param couponTemplate
	 * @return
	 */
	public static CouponValidTimePeriod calcCouponValidTimePeriod(
			CouponTemplate couponTemplate) {

		CouponValidTimePeriod cvtp = new CouponValidTimePeriod();
		cvtp.setCouponTemplateid(couponTemplate.getCouponTemplateid());

		Integer expiryDateType = couponTemplate.getExpirydateType();
		if (CouponConstants.PeriodOfValidity.FIXED_VALIDITY_PERIOD
				.equals(expiryDateType)) {
			// 如果是固定有效期, 则couponTemplate的startTime和endTime字段起作用
			cvtp.setStartTime(couponTemplate.getStartTime());
			cvtp.setEndTime(couponTemplate.getEndTime());
			logger.info("-->calcCouponValidTimePeriod-->计算优惠券模版："
					+ couponTemplate.getCouponTemplateid()
					+ "的生效时间和结束时间-->固定有效期-->startTime: " + cvtp.getStartTime()
					+ ", endTime: " + cvtp.getEndTime());
		} else {
			// 领取后设置的其它选项，需根据不同条件计算生效时间和结束时间
			Date startTime = null;
			Date endTime = null;

			// 1.领取后多少天开始生效, 以此计算生效时间
			Integer dayQuantity = couponTemplate.getDayQuantity();
			Date now = new Date();
			startTime = DateUtils.addDays(now, dayQuantity);
			cvtp.setStartTime(startTime);

			// 2. 什么日期内有效, 或者多少分钟/小时/天内有效, 以此计算结束时间
			String timeType = couponTemplate.getTimeType();
			Integer timeQuantity = couponTemplate.getTimeQuantity();
			if (CouponConstants.PeriodOfValidity.TimeUnitsType.DATE
					.equalsIgnoreCase(timeType)) {

				// 当时间单位为"DAYS"，则根据deadlineTime计算
				endTime = couponTemplate.getDeadlineTime();

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.MINUTES
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addMinutes(startTime, timeQuantity);

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.HOURS
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addHours(startTime, timeQuantity);

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.DAYS
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addDays(startTime, timeQuantity);

			}
			cvtp.setEndTime(endTime);
		}

		return cvtp;
	}

	/**
	 * 计算优惠券的生效时间和结束时间
	 * 
	 * @param couponTemplate
	 * @return
	 */
	public static CouponValidTimePeriod calcCouponValidTimePeriod(
			CouponTemplateDetailsInfo couponTemplate) {

		if (couponTemplate == null) {
			return null;
		}
		CouponValidTimePeriod cvtp = new CouponValidTimePeriod();
		Integer expiryDateType = couponTemplate.getExpirydateType();
		if (CouponConstants.PeriodOfValidity.FIXED_VALIDITY_PERIOD
				.equals(expiryDateType)) {
			// 如果是固定有效期, 则couponTemplate的startTime和endTime字段起作用
			cvtp.setStartTime(couponTemplate.getStartTime());
			cvtp.setEndTime(couponTemplate.getEndTime());
		} else {
			// 领取后设置的其它选项，需根据不同条件计算生效时间和结束时间
			Date startTime = null;
			Date endTime = null;

			// 1.领取后多少天开始生效, 以此计算生效时间
			Integer dayQuantity = couponTemplate.getDayQuantity();
			Date now = new Date();
			startTime = DateUtils.addDays(now, dayQuantity);
			cvtp.setStartTime(startTime);

			// 2. 什么日期内有效, 或者多少分钟/小时/天内有效, 以此计算结束时间
			String timeType = couponTemplate.getTimeType();
			Integer timeQuantity = couponTemplate.getTimeQuantity();
			if (CouponConstants.PeriodOfValidity.TimeUnitsType.DATE
					.equalsIgnoreCase(timeType)) {

				// 当时间单位为"DAYS"，则根据deadlineTime计算
				endTime = couponTemplate.getDeadlineTime();

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.MINUTES
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addMinutes(startTime, timeQuantity);

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.HOURS
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addHours(startTime, timeQuantity);

			} else if (CouponConstants.PeriodOfValidity.TimeUnitsType.DAYS
					.equalsIgnoreCase(timeType)) {

				endTime = DateUtils.addDays(startTime, timeQuantity);

			}
			cvtp.setEndTime(endTime);

		}

		return cvtp;
	}

	/**
	 * 判断优惠券是否已失效(已过期) coupon信息里存的生效时间和失效时间是经过计算之后的
	 * 
	 * @param coupon
	 *            优惠券详细信息
	 * @return
	 */
	public static boolean couponIsInvalid(Date endTime) {
		return endTime.before(new Date()) ? true : false;
	}

	/**
	 * 判断优惠券是否还未生效，即未到生效时间，注意并不是指失效; MyCoupon信息里存的生效时间和失效时间是经过计算之后的
	 * 
	 * @param coupon
	 *            优惠券详细信息
	 * @return
	 */
	public static boolean couponIsNotValid(Date startTime) {
		return startTime.after(new Date()) ? true : false;
	}

	/**
	 * 判断优惠券模版是否还未生效，即未到生效时间，注意并不是指失效
	 * CouponValidTimePeriod信息里存的生效时间和失效时间是经过计算之后的
	 * 
	 * @param CouponValidTimePeriod
	 *            优惠券模版详细信息
	 * @return
	 */
	public static boolean couponTemplateIsNotValid(CouponValidTimePeriod cvtp,
			String method) {
		return cvtp.getStartTime().after(new Date()) ? true : false;
	}

	/**
	 * 判断优惠券模版是否已失效； CouponValidTimePeriod信息里存的生效时间和失效时间是经过计算之后的
	 * 
	 * @param CouponValidTimePeriod
	 *            优惠券模版
	 * @param method
	 *            调用此方法的方法名，方便日志记录，不具业务逻辑意义
	 * @return
	 */
	public static boolean couponTemplateIsInvalid(CouponValidTimePeriod cvtp,
			String method) {
		return cvtp.getEndTime().before(new Date()) ? true : false;
	}

	public static String getTimeAlarm(CouponDetailsInfo coupon) {

		long diff = coupon.getEndTime().getTime() - new Date().getTime();

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		long ns = 1000;
		long hour = diff % nd / nh;
		long min = diff % nd % nh / nm;
		long sec = diff % nd % nh % nm / ns;
		// 计算差多少天
		long day = diff / nd;
		hour = day * 24 + hour;
		// 距离失效时间还有多少分钟
		long total = hour * 60 + min;

		// 在后台配置的提醒分钟数之内则给出提醒
		if (coupon.getAlarmclock() >= total) {
			StringBuilder sb = new StringBuilder();
			sb.append(hour);
			sb.append("小时");
			sb.append(min);
			sb.append("分");
			sb.append(sec);
			sb.append("秒后过期");
			logger.info("-->getTimeAlarm:" + sb.toString());
			return sb.toString();
		}

		return null;
	}

	public static CouponInfoForBrush getCouponInfoForBrush(
			CouponDetailsInfo coupon) {
		CouponInfoForBrush info = new CouponInfoForBrush();
		info.setCouponCode(coupon.getCouponCode());
		// 优惠券名称
		info.setCouponName(coupon.getCouponName());
		// 优惠金额，单位：元
		double preferentialPrice = coupon.getCouponMoney();
		// // 最终价格由元换算成分
		// BigDecimal a = new BigDecimal(preferentialPrice);
		// BigDecimal b = new BigDecimal(Double.toString(100));
		// BigDecimal res = a.multiply(b);
		// int totalFee = res.setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
		info.setMoney(preferentialPrice);
		// info.setInvalidTime();

		long diff = coupon.getEndTime().getTime() - new Date().getTime();
		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		long ns = 1000;
		long hour = diff % nd / nh;
		long min = diff % nd % nh / nm;
		long sec = diff % nd % nh % nm / ns;
		// 计算差多少天
		long day = diff / nd;
		hour = day * 24 + hour;

		if (hour >= 24) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
			info.setInvalidTime(sdf.format(coupon.getEndTime()));
		} else {
			String hourStr = hour >= 10 ? String.valueOf(hour) : String
					.valueOf("0" + hour);
			String minuteStr = min >= 10 ? String.valueOf(min) : String
					.valueOf("0" + min);
			String seconds = sec >= 10 ? String.valueOf(sec) : String
					.valueOf("0" + sec);
			StringBuilder sb = new StringBuilder();
			sb.append(hourStr);
			sb.append(":");
			sb.append(minuteStr);
			sb.append(":");
			sb.append(seconds);
			info.setInvalidTime(sb.toString());
		}

		info.setEndTime(DATEFORMAT2.format(coupon.getEndTime()));
		return info;
	}
	
	public static String getApplyRangeDesc(CouponTemplateDetailsInfo template, String categoryName) {
		Integer couponRange = template.getCouponRange();
		if(CouponConstants.UseRange.UNLIMITED.equals(couponRange)) {
			return "所有商品可用";
		} else if(CouponConstants.UseRange.PART_GOODS_CAN_USE.equals(couponRange)) {
			return "部分商品可用";
		} else if(CouponConstants.UseRange.PART_GOODS_CANNOT_USE.equals(couponRange)) {
			return "部分商品不可用";
		} else if(CouponConstants.UseRange.SPECIFY_CATEGORY_CAN_USE.equals(couponRange)) {
			if(StringUtils.isNotEmpty(categoryName)) {
				return categoryName + "品类可用";
			}
			return "指定分类可用";
		} 
		return null;
	}
	
	public static Set<String> getGoodsBaseidSet(String goodsBaseids) {
		Set<String> set = new HashSet<String>();
		if (StringUtils.isNotEmpty(goodsBaseids)) {
			String[] arr = goodsBaseids.split(CommonConstant.Symbol.COMMA);
			for (String gb : arr) {
				set.add(gb);
			}
		}
		return set;
	}








	
	public static Set<String> getCategoryIdSet(String categoryids) {
		Set<String> set = new HashSet<String>();
		if (StringUtils.isNotEmpty(categoryids)) {
			String[] arr = categoryids.split(CommonConstant.Symbol.COMMA);
			for (String ci : arr) {
				set.add(ci);
			}
		}
		return set;
	}

	public static void main(String[] args) throws ParseException {
		CouponDetailsInfo details = new CouponDetailsInfo();
		details.setCouponName("买买家5元优惠券");
		details.setCouponMoney(5.0d);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		details.setEndTime(sdf.parse("2018-10-10 23:19:59"));
		CouponInfoForBrush info = getCouponInfoForBrush(details);
		System.out.println(info.getInvalidTime());
	}
	
}
