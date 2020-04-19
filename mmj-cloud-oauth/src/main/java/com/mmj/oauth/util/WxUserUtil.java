package com.mmj.oauth.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.codec.digest.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.AppTypeConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.utils.HttpURLConnectionUtil;
import com.mmj.oauth.dto.Jscode2Session;
import com.xiaoleilu.hutool.map.MapUtil;

@Slf4j
public class WxUserUtil {
	
	/**
	 * 获取session_key的接口地址
	 */
	private static final String URL_JSCODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session";
	
	/**
     * 获取用户openid
     */
    public static String URL_GET_ACCESSTOKEN_USER = "https://api.weixin.qq.com/sns/oauth2/access_token";
    
    /**
     * 获取已关注的用户信息
     */
    public static String URL_GET_USERINFO_FOLLOW = "https://api.weixin.qq.com/cgi-bin/user/info";
    
    /**
     * 获取api_token
     */
    public static String URL_GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    
    /**
     * 获取未关注的用户信息
     */
    public static String URL_GET_USERINFO_NO_FOLLOW = "https://api.weixin.qq.com/sns/userinfo";
    
    /**
     * token过期
     */
    public static String CODE_INVALID_TOKEN = "40001";
    
    private static final String FIELD_SIGN = "sign";
	
	public static Jscode2Session invokeJscode2Session(JSONObject jsonParamObject) {
		Jscode2Session jscode2Session = null;
		try {
			String requestResultJsonString = HttpURLConnectionUtil.doGet(URL_JSCODE2SESSION, jsonParamObject);
			jscode2Session = JSONObject.parseObject(requestResultJsonString, Jscode2Session.class);
		} catch (Exception e) {
			log.error("获取openid失败：", e);
			throw new CustomException("微信服务异常，获取openid失败");
		}
		return jscode2Session;
	}

	public static String getUserFrom(String appType) {
		String userFrom;
		if (AppTypeConstant.APPTYPE_MIN.equalsIgnoreCase(appType) || AppTypeConstant.APPTYPE_LOTTERY.equalsIgnoreCase(appType)) {
			userFrom = AppTypeConstant.APPTYPE_MIN;
		} else {
			userFrom = appType;
		}
		return userFrom;
	}
	
	public static String generateSignature(final Map<String, String> data){
        try {
            Set<String> keySet = data.keySet();
            String[] keyArray = keySet.toArray(new String[keySet.size()]);
            Arrays.sort(keyArray);
            StringBuilder sb = new StringBuilder();
            for (String k : keyArray) {
                if (k.equals(FIELD_SIGN)) {
                    continue;
                }
                if (data.get(k) != null && data.get(k).trim().length() > 0) {
                	sb.append(k).append("=").append(data.get(k).trim()).append("&");
                }
            }
            return sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }
	
	public static String getSignature(String jsapi_ticket, String noncestr, String timestamp, String url) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("jsapi_ticket", jsapi_ticket);
        params.put("noncestr", noncestr);
        params.put("timestamp", timestamp);
        params.put("url", url);
        Map<String, String> sortParams = sortAsc(params);
        String string1 = mapJoin(sortParams, false);
        try {
            return DigestUtils.sha1Hex(string1.getBytes("UTF-8"));
        } catch (IOException var8) {
            return "";
        }
    }
	
	public static String mapJoin(Map<String, String> map, boolean valueUrlEncode) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = map.keySet().iterator();
        while(true) {
            String key;
            do {
                do {
                    if (!iter.hasNext()) {
                        if (sb.length() > 0) {
                            sb.deleteCharAt(sb.length() - 1);
                        }
                        return sb.toString();
                    }
                    key = (String)iter.next();
                } while(map.get(key) == null);
            } while("".equals(map.get(key)));

            try {
                String temp = key.endsWith("_") && key.length() > 1 ? key.substring(0, key.length() - 1) : key;
                sb.append(temp);
                sb.append("=");
                String value = (String)map.get(key);
                if (valueUrlEncode) {
                    value = URLEncoder.encode((String)map.get(key), "utf-8").replace("+", "%20");
                }
                sb.append(value);
                sb.append("&");
            } catch (UnsupportedEncodingException var7) {
                var7.printStackTrace();
            }
        }
    }
	
	public static Map<String, String> sortAsc(Map<String, String> map) {
        HashMap<String, String> tempMap = new LinkedHashMap<String, String>();
        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return ((String)o1.getKey()).compareTo((String)o2.getKey());
            }
        });

        for(int i = 0; i < infoIds.size(); ++i) {
            Map.Entry<String, String> item = (Map.Entry<String, String>)infoIds.get(i);
            tempMap.put(item.getKey(), item.getValue());
        }

        return tempMap;
    }
}
