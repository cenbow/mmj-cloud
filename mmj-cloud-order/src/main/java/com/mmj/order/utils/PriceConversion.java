package com.mmj.order.utils;

import java.math.BigDecimal;

/**
 * @Description: 价格转换器
 * @Auther: KK
 * @Date: 2018/11/23
 */
public class PriceConversion {
    private final static BigDecimal INIT_VAL = BigDecimal.valueOf(100);

    /**
     * 例：33.3 -> 3330
     *
     * @param str
     * @return
     */
    public static int stringToInt(String str) {
        if (null == str) return 0;
        return new BigDecimal(str).multiply(INIT_VAL).intValue();
    }

    /**
     * 例： 33.3 -> 3330
     *
     * @param d
     * @return
     */
    public static int doubleToInt(Double d) {
        if (null == d) return 0;
        return BigDecimal.valueOf(d).multiply(INIT_VAL).intValue();
    }

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
        return bigDecimal.multiply(INIT_VAL).intValue();
    }

    /**
     * 例：3330 -> 33.3
     *
     * @param n
     * @return
     */
    public static String intToString(Integer n) {
        if (null == n) return "0.00";
        return BigDecimal.valueOf(n).divide(INIT_VAL).toString();
    }

    /**
     * 例：3330 -> 33.3
     *
     * @param n
     * @return
     */
    public static double longToDouble(Long n) {
        if (null == n) return 0.00;
        return BigDecimal.valueOf(n).divide(INIT_VAL).doubleValue();
    }

}
