package com.mmj.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class StringUtils {

    /**
     * 获取uuid
     * @return
     */
    public static String getUUid(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 获取uuid
     * @return
     */
    public static String getUUid(int length){
        return getUUid().substring(0,length);
    }

    /**
     * 获取服务端ip
     * @return
     */
    public static String getServerIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        }catch (UnknownHostException e){
            return "127.0.0.1";
        }
    }

    /**
     * 字符串长度 英文算一个 中文算两个
     * @param value
     * @return
     */
    public static int length(String value) {
        int valueLength = 0;
        String chinese = "[\u4e00-\u9fa5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    public static boolean isEmpty(String str){
        return null == str || str.length() == 0;
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
}
