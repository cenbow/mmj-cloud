package com.mmj.order.utils.http;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class StringUtils {

	/**
	 * 补位，字符串前面补零
	 * @param num 位数
	 * @param outNum 输出数字
	 * @return
	 */
	public static String fillin(int num, int outNum) {
		return String.format("%0" + num + "d", outNum);
	}

	/**
	 * 正在表达式判断是不是数字类型
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric1(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 用JAVA自带 判断是不是数字类型
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isBlank(String str) {
		return str == null || "".equals(str.trim());
	}

	/**
	 * 返回对象的字符串，若该对象为空时返回空字符串
	 */
	public static String getValue(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}

	public static Integer getInteger(Object obj) {
		return obj == null ? 0 : Integer.parseInt(getValue(obj.toString().trim()));
	}

	public static String getStr(Object obj) {

		if (obj instanceof String) {
			String res = (String) obj;
			return res;
		} else if (obj instanceof Integer) {
			int res = ((Integer) obj).intValue();
			return String.valueOf(res);
		} else if (obj instanceof Double) {
			double res = ((Double) obj).doubleValue();
			return String.valueOf(res);
		} else if (obj instanceof Float) {
			float res = ((Float) obj).floatValue();
			return String.valueOf(res);
		} else if (obj instanceof Long) {
			long res = ((Long) obj).longValue();
			return String.valueOf(res);
		} else if (obj instanceof Boolean) {
			boolean res = ((Boolean) obj).booleanValue();
			return String.valueOf(res);
		} else if (obj instanceof Date) {
			Date res = (Date) obj;
			return String.valueOf(res);
		} else if (obj instanceof BigDecimal) {
			BigDecimal res = (BigDecimal) obj;
			return String.valueOf(res);
		} else if ("".equals(StringUtils.getValue(obj)) || null == obj) {
			return "";
		} else {
			return "getStr";
		}
	}

	/**
	 * Java 生成
	 * 
	 * @return String ：4028888e358a31cd01358a31cded0000
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString().replaceAll("-","");
	}


	/**
	 * 获取指定长度的随机字符串
	 * @param length
	 * @return
	 */
	public static  String getRandomUUid(int length){
		return getUUID().substring(0,length);
	}

	/**
	 * Java 生成
	 * 
	 * @return String 类型 9位的随机ID
	 */
	public static String getRandomId() {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HHmmss");

		String str = simpleDateFormat.format(new Date());

		Random random = new Random();

		int rannum = (int) (random.nextDouble() * (999) + 1000);

		return (rannum + str).trim().toString();
	}

	/**
	 * Java 生成
	 * 
	 * @return Integer类型 9位随机数
	 */
	public static Integer getIRandomId() {

		int rannum = Integer.parseInt(getRandomId());

		return rannum;
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @return true/false
	 */
	public static boolean isEmpty(String str) {
		return (null == str || "".equals(str.trim()) || "undefinded".equals(str.trim()) || "null".equals(str.trim()));
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @return true/false
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static String getDateStr(String obj) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(obj);

		String dateStr = simpleDateFormat.format(new Date());
		return dateStr;
	}

	/**
	 * 转义正则特殊字符 （$()*+.[]?\^{} \\需要第一个替换，否则replace方法替换时会有逻辑bug
	 */
	public static String makeAllRegExp(String str) {
		if (isBlank(str)) {
			return str;
		}

		return str.replace("\\", "\\\\").replace("*", "\\*").replace("+", "\\+").replace("|", "\\|").replace("{", "\\{")
				.replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace("^", "\\^").replace("$", "\\$")
				.replace("[", "\\[").replace("]", "\\]").replace("?", "\\?").replace(",", "\\,").replace(".", "\\.")
				.replace("&", "\\&");
	}

	public static int countStr(String source, String str) {
		if (source.indexOf(str) == -1) {
			return 0;
		}
		return countStr(source.substring(source.indexOf(str) + str.length()), str, 1);
	}

	private static int countStr(String source, String str, Integer counter) {
		if (source.indexOf(str) == -1) {
			return counter;
		}
		counter++;
		return countStr(source.substring(source.indexOf(str) + str.length()), str, counter);
	}

	/**
	 * 字符转int
	 * 
	 * @param input
	 * @return
	 */
	public static Integer toInterger(String input) {
		Integer result = 0;
		try {
			result = Integer.parseInt(input);
		} catch (NumberFormatException err) {

		}
		return result;
	}

	/**
	 * 判断字符是否是浮点类型
	 * 
	 * @param input
	 * @return
	 */
	public static Boolean isDouble(String input) {
		Pattern pattern = Pattern.compile("[0-9]*(.[0-9]*)?");
		Matcher isNum = pattern.matcher(input);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 字符转浮点类型
	 * 
	 * @param input
	 * @return
	 */
	public static Double toDouble(String input) {
		Double result = 0.0;
		try {
			result = Double.parseDouble(input);
		} catch (NumberFormatException err) {

		}
		return result;
	}

	/**
	 * String左对齐
	 * 
	 * @param src
	 * @param len
	 * @param ch
	 * @return
	 */
	public static String padLeft(String src, int len, char ch) {
		int diff = len - src.length();
		if (diff <= 0) {
			return src;
		}

		char[] charr = new char[len];
		System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
		for (int i = src.length(); i < len; i++) {
			charr[i] = ch;
		}
		return new String(charr);
	}

	/**
	 * String右对齐
	 * 
	 * @param src
	 * @param len
	 * @param ch
	 * @return
	 */
	public static String padRight(String src, int len, char ch) {
		int diff = len - src.length();
		if (diff <= 0) {
			return src;
		}

		char[] charr = new char[len];
		System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
		for (int i = 0; i < diff; i++) {
			charr[i] = ch;
		}
		return new String(charr);
	}

	public static Boolean verifySku(String sku) {
		if (sku.contains("×"))
			return false;
		if (sku.contains("["))
			return false;
		if (sku.contains("]"))
			return false;
		if (sku.contains("^"))
			return false;
		if (sku.contains("`"))
			return false;
		if (sku.contains("{"))
			return false;
		if (sku.contains("|"))
			return false;
		if (sku.contains("}"))
			return false;
		if (sku.contains("~"))
			return false;

		return !sku.matches("[^\\x00-\\xff]");
	}

	public static boolean isStartWithChinese(String input) {
		if (isBlank(input))
			return false;
		if (isEmpty(input))
			return false;
		char firstChar = input.charAt(0);
		return isChinese(firstChar);
	}

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

			return true;

		}

		return false;

	}
	public static String getnow()
	  {
	    StringBuffer value = new StringBuffer();
	    
	    Calendar cal = Calendar.getInstance();
	    int year = cal.get(1);
	    value.append(String.valueOf(year).subSequence(2,3));
	    int month = cal.get(2) + 1;
	    if (month < 10) {
	      value.append("0" + String.valueOf(month));
	    } else {
	      value.append(String.valueOf(month));
	    }
	    int day = cal.get(5);
	    if (day < 10) {
	      value.append("0" + String.valueOf(day));
	    } else {
	      value.append(String.valueOf(day));
	    }
	    int hour = cal.get(10);
	    if (hour < 10) {
	      value.append("0" + String.valueOf(hour));
	    } else {
	      value.append(String.valueOf(hour));
	    }
	    int minute = cal.get(12);
	    if (minute < 10) {
	      value.append("0" + String.valueOf(minute));
	    } else {
	      value.append(String.valueOf(minute));
	    }
	    int second = cal.get(13);
	    if (second < 10) {
	      value.append("0" + String.valueOf(second));
	    } else {
	      value.append(String.valueOf(second));
	    }
	    int max=999999999;
        int min=100000000;
        Random random = new Random();

        int s = random.nextInt(max)%(max-min+1) + min;
        value.append(String.valueOf(s));
	    return value.toString();
	  }
}
