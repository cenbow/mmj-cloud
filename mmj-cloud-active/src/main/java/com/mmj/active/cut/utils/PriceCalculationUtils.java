package com.mmj.active.cut.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @description: 价格计算
 * @auther: KK
 * @date: 2019/6/14
 */
public class PriceCalculationUtils {
    public final static BigDecimal INIT_VAL = BigDecimal.valueOf(100);
    private final static int SCALE = 2;

    /**
     * 例 33.3 -> 3330
     *
     * @param bigDecimal
     * @return
     */
    public static int bigDecimalToInt(BigDecimal bigDecimal) {
        if (bigDecimal == null) {
            return 0;
        }
        return multiply(bigDecimal, INIT_VAL).intValue();
    }

    /**
     * 例 3330 -> 33.3
     *
     * @param num
     * @return
     */
    public static BigDecimal intToBigDecimal(int num) {
        return divide(BigDecimal.valueOf(num), INIT_VAL);
    }

    /**
     * 相乘
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal multiply(BigDecimal v1, BigDecimal v2) {
        return v1.multiply(v2).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 相除
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal divide(BigDecimal v1, BigDecimal v2) {
        return v1.divide(v2, SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 相减
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal subtract(BigDecimal v1, BigDecimal v2) {
        return v1.subtract(v2).setScale(SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 相加
     *
     * @param v1
     * @param v2
     * @return
     */
    public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
        return v1.add(v2).setScale(SCALE, RoundingMode.HALF_UP);
    }
}
