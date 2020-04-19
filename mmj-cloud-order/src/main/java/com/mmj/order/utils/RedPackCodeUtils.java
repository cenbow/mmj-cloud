package com.mmj.order.utils;

import java.util.Random;

public class RedPackCodeUtils {

    private static String prefix = "MFS";

    /**
     * 通过JDK 的Math.random()函数生成一个[0,1]范围内的随机数，将这个随机数乘以 9，
     * 然后再加1 就构成了[1,10]范围的一个随机数了(1位随机数)。
     * 依次类推，要产生2位的随机数只需要将Math.random()乘以 90,然后加上10就可以了。
     *
     * @return
     */
    public static String genRedPackCode() {
        Long rm = (long) (Math.random() * 9 * Math.pow(10, 8 - 1)) + (long) Math.pow(10, 8 - 1);
        return prefix + rm.toString();
    }

    public static String genLotteryRedPackCode() {
        Long rm = (long) (Math.random() * 6 * Math.pow(10, 6 - 1)) + (long) Math.pow(10, 6 - 1);
        return "LCJ" + rm.toString();
    }

    public static int genRandom(int min, int max) {
        if (min >= max)
            return max;
        int beforemax = 10;
        int beforemin = 1;
        Random random = new Random();
        int befores = random.nextInt(beforemax) % (beforemax - beforemin + 1) + beforemin;
        if (befores == 1) {
            int s = random.nextInt(max) % (max - min + 1) + min;
            return s;
        } else {
            return min;
        }
    }
}
