package com.mmj.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 时间处理工具类
 *
 * @author shenfuding
 */
public class DateUtils {

    public static final String DATE_PATTERN_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN_2 = "yyyy/MM/dd HH:mm:ss";
    public static final String DATE_PATTERN_3 = "yyyy-MM-dd HH:mm";
    public static final String DATE_PATTERN_4 = "yyyy/MM/dd HH:mm";
    public static final String DATE_PATTERN_5 = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String DATE_PATTERN_6 = "yyyy年MM月dd日 HH时mm分";
    public static final String DATE_PATTERN_7 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DATE_PATTERN_8 = "yyyy.MM.dd";
    public static final String DATE_PATTERN_9 = "yyyy.MM.dd HH:mm:ss";
    public static final String DATE_PATTERN_10 = "yyyy-MM-dd";
    public static final String DATE_PATTERN_11 = "yyyy年MM月dd日";

    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public static final SimpleDateFormat SDF1 = new SimpleDateFormat(DATE_PATTERN_1);
    public static final SimpleDateFormat SDF2 = new SimpleDateFormat(DATE_PATTERN_2);
    public static final SimpleDateFormat SDF3 = new SimpleDateFormat(DATE_PATTERN_3);
    public static final SimpleDateFormat SDF4 = new SimpleDateFormat(DATE_PATTERN_4);
    public static final SimpleDateFormat SDF5 = new SimpleDateFormat(DATE_PATTERN_5);
    public static final SimpleDateFormat SDF6 = new SimpleDateFormat(DATE_PATTERN_6);
    public static final SimpleDateFormat SDF7 = new SimpleDateFormat(DATE_PATTERN_7);
    public static final SimpleDateFormat SDF8 = new SimpleDateFormat(DATE_PATTERN_8);
    public static final SimpleDateFormat SDF9 = new SimpleDateFormat(DATE_PATTERN_9);
    public static final SimpleDateFormat SDF10 = new SimpleDateFormat(DATE_PATTERN_10);
    public static final SimpleDateFormat SDF11 = new SimpleDateFormat(DATE_PATTERN_11);

    public static final SimpleDateFormat FORMATYYYYMMDDHHMMSSSSS = new SimpleDateFormat(yyyyMMddHHmmssSSS);

    private DateUtils() {
    }

    /**
     * 根据指定的时间格式获取当前时间，返回字符串类型格式化后的时间
     *
     * @param pattern
     * @return
     */
    public static String getNowDate(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 根据指定的时间格式将传入的时间进行格式化，返回字符串类型格式化后的时间
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String getDate(Date date, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }

    /**
     * 在给的时间基础上加分钟
     *
     * @param date
     * @param minute
     * @return
     */
    public static Date pushMinute(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        date.setTime(calendar.getTimeInMillis());
        return date;
    }

    /**
     * 获取startTime和endTime两者时间相差的秒数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long subInterval(Date startTime, Date endTime) {
        return ((endTime.getTime() - startTime.getTime()) / 1000);
    }

    /**
     * 根据时间格式获取SimpleDateFormat对象
     *
     * @param pattern
     * @return
     */
    public static SimpleDateFormat getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    /**
     * 根据指定的SimpleDateFormat格式化Date对象的时间
     *
     * @param sdf
     * @param date
     * @return
     */
    public static String getDate(SimpleDateFormat sdf, Date date) {
        if (sdf == null) {
            return null;
        }
        return sdf.format(date);
    }

    /**
     * 解析时间 2018-12-03 21:22:56->date
     *
     * @param date
     * @return
     */
    public static Date parse(String date) {
        try {
            return SDF1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * 将字符串时间转换为Date类型
     *
     * @param dateStr
     * @param sdf
     * @return
     */
    public static Date getDate(String dateStr, SimpleDateFormat sdf) {
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取今天0点时刻的时间
     *
     * @return
     */
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 获取多少分钟之前的时间
     *
     * @param minute
     * @return
     */
    public static Date getBeforeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, -minute);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 获取多少小时之后的时间
     *
     * @param hourse
     * @return
     */
    public static Date getAfterByHourse(int hourse) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, hourse);
        Date date = calendar.getTime();
        return date;
    }

    /**
     * 获取剩余时间
     *
     * @return
     */
    public static String getSurplusTime(Date startTime, Date endTime) {
        //计算剩余天数
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(startTime);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endTime);
        long l = cal2.getTimeInMillis() - cal1.getTimeInMillis();

        Integer ss = 1000;//秒
        Integer mi = ss * 60;//分
        Integer hh = mi * 60;//时
        Integer dd = hh * 24;//天

        long day = l / dd;
        long hour = (l - day * dd) / hh;
        long minute = (l - day * dd - hour * hh) / mi;

        Map<String, Object> keyword5 = new HashMap<String, Object>();// 温馨提示
        StringBuffer timeStr = new StringBuffer();
        if (day > 0) {
            timeStr.append(day + "天");
        }
        if (hour > 0 && day <= 0) {
            timeStr.append(hour + "小时");
        }
        if (minute > 0 && day <= 0) {
            timeStr.append(minute + "分钟");
        }
        return timeStr.toString();
    }
}
