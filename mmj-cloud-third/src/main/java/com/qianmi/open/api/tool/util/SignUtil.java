package com.qianmi.open.api.tool.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 签名工具类
 */
public class SignUtil {

    private static List<String> ignoreList;
    static {
        ignoreList = new ArrayList<String>();
        ignoreList.add("file");
        ignoreList.add("image");
        ignoreList.add("logo");
    }

    public static String sha1(String str) throws IOException {
        return byte2hex(getSHA1Digest(str));
    }

    private static byte[] getSHA1Digest(String data) throws IOException {
        byte[] bytes;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            bytes = md.digest(data.getBytes("utf-8"));
        } catch (GeneralSecurityException gse) {
            throw new IOException(gse);
        }
        return bytes;
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toUpperCase());
        }
        return sign.toString();
    }

    public static String sign(RequestParametersHolder requestHolder, String secret) throws IOException {
        Map<String, String> params = requestHolder.getAllParams();
        return sign(params, ignoreList, secret);
    }

    public static String sign(Map<String, String> paramValues, String secret) throws IOException {
        return sign(paramValues, ignoreList, secret);
    }

    /**
     * 对paramValues进行签名，其中ignoreParamNames这些参数不参与签名
     * @param paramValues
     * @param ignoreParamNames
     * @param secret
     * @return
     */
    public static String sign(Map<String, String> paramValues, List<String> ignoreParamNames, String secret) throws IOException {
        StringBuilder sb = new StringBuilder();
        List<String> paramNames = new ArrayList<String>(paramValues.size());
        paramNames.addAll(paramValues.keySet());
        if(ignoreParamNames != null && ignoreParamNames.size() > 0){
            for (String ignoreParamName : ignoreParamNames) {
                paramNames.remove(ignoreParamName);
            }
        }
        Collections.sort(paramNames);
        sb.append(secret);
        // 忽略参数值为空的参数
        for (String paramName : paramNames) {
            if (StringUtils.areNotEmpty(paramValues.get(paramName))) {
                sb.append(paramName).append(paramValues.get(paramName));
            }
        }
        sb.append(secret);
        byte[] sha1Digest = getSHA1Digest(sb.toString());
        return byte2hex(sha1Digest);
    }
}
