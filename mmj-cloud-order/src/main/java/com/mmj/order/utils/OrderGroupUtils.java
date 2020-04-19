package com.mmj.order.utils;

import java.util.Random;

public class OrderGroupUtils {

    public static String GROUP_INFO_KEY_PREFIX = "GROUP_NO:";

    /**
     * 生成拼团号
     *
     * @return String
     */
    public static String genGroupNo(Long userId) {
        Random random = new Random();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            result.append(random.nextInt(10));
        }
        String s = System.currentTimeMillis() + "";
        s = s.substring(s.length() - 5);
        s = s + result.toString();
        String str = num2Str(userId);
        System.out.println(Long.parseLong(str));
        return String.format("GM-%s%s", s, num2Str(userId));
    }


    private static String num2Str(Long userId) {
        long num = userId % 100;
        if (num < 10)
            return "00" + num;
        else
            return "0" + num;
    }

    /**
     * 生成免费送拼团号
     *
     * @return String
     */
    public static String genMFSGroupNo() {
        Random random = new Random();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            result.append(random.nextInt(10));
        }
        String s = System.currentTimeMillis() + "";
        s = s.substring(s.length() - 5);
        s = s + result.toString();
        return String.format("MFS-%s", s);
    }
}
