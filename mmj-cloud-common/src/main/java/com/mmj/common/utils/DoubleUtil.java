package com.mmj.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtil {

	private DoubleUtil() {
	}

	/**
	 * 运算精度：1
	 */
	public static final Integer SCALE_1 = 1;

	/**
	 * 运算精度：2
	 */
	public static final Integer SCALE_2 = 2;

	/**
	 * 运算精度：3
	 */
	public static final Integer SCALE_3 = 3;

	/**
	 * 加法操作，结果使用默认精度：2
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double add(Double value1, Double value2) {
		return add(value1, value2, SCALE_2);
	}

	/**
	 * 加法操作，需传入精度
	 * 
	 * 可使用DoubleUtil.SCALE_1,DoubleUtil.SCALE_2,DoubleUtil.SCALE_3
	 * 
	 * @param value1
	 * @param value2
	 * @param scale
	 * @return
	 */
	public static Double add(Double value1, Double value2, Integer scale) {
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.add(b2).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 减法操作，结果使用默认精度：2
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double sub(Double value1, Double value2) {
		return sub(value1, value2, SCALE_2);
	}

	/**
	 * 减法操作，需传入精度
	 * 
	 * 可使用DoubleUtil.SCALE_1,DoubleUtil.SCALE_2,DoubleUtil.SCALE_3
	 * 
	 * @param value1
	 * @param value2
	 * @param scale
	 * @return
	 */
	public static Double sub(Double value1, Double value2, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("-->The scale must be a positive integer or zero.");
		}
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.subtract(b2).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 乘法操作，结果使用默认精度：2
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static Double mul(Double value1, Double value2) {
		return mul(value1, value2, SCALE_2);
	}

	/**
	 * 乘法操作，需传入精度
	 * 
	 * 可使用DoubleUtil.SCALE_1,DoubleUtil.SCALE_2,DoubleUtil.SCALE_3
	 * 
	 * @param value1
	 * @param value2
	 * @param scale
	 * @return
	 */
	public static Double mul(Double value1, Double value2, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("-->The scale must be a positive integer or zero.");
		}
		BigDecimal b1 = new BigDecimal(value1);
		BigDecimal b2 = new BigDecimal(value2);
		return b1.multiply(b2).setScale(scale, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * 除法操作，结果使用默认精度：2
	 * 
	 * @param dividend
	 * @param divisor
	 * @return
	 */
	public static Double divide(Double dividend, Double divisor) {
		return divide(dividend, divisor, SCALE_2);
	}

	/**
	 * 除法操作，需传入精度
	 * 
	 * 可使用DoubleUtil.SCALE_1,DoubleUtil.SCALE_2,DoubleUtil.SCALE_3
	 * 
	 * @param dividend
	 * @param divisor
	 * @param scale
	 * @return
	 */
	public static Double divide(Double dividend, Double divisor, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("-->The scale must be a positive integer or zero.");
		}
		BigDecimal b1 = new BigDecimal(dividend);
		BigDecimal b2 = new BigDecimal(divisor);
		return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
	}
	
	/**
	 * 除法操作，需传入精度，最终向上取整
	 * 
	 * 可使用DoubleUtil.SCALE_1,DoubleUtil.SCALE_2,DoubleUtil.SCALE_3
	 * 
	 * @param dividend
	 * @param divisor
	 * @param scale
	 * @return
	 */
	public static Double divideUp(Double dividend, Double divisor, Integer scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("-->The scale must be a positive integer or zero.");
		}
		BigDecimal b1 = new BigDecimal(dividend);
		BigDecimal b2 = new BigDecimal(divisor);
		return b1.divide(b2, scale, RoundingMode.UP).doubleValue();
	}

}
