package com.mmj.pay.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class StringUtils {

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
     * Java 生成
     *
     * @return String ：4028888e358a31cd01358a31cded0000
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    /**
     * 判断字符串为空
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }


    /**
     * 判断字符串不为空
     * @param str
     * @return
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
