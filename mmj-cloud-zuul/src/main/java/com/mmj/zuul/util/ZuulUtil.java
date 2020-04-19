package com.mmj.zuul.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.CommonConstant;

public class ZuulUtil {
	
	private static final String STRING_EMPTY = " ";
	private static final String USERID_TOKEN = "USERID:TOKEN:";
	private static final String TOKENUSER = "TOKENUSER:";
	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=utf-8";
	private static final String STATUS = "status";
	private static final String DESC = "desc";
	private static final String CODE = "code";
	
	public static String getTokenUserCacheKey(String authorization) {
		return TOKENUSER + authorization.toLowerCase();
	}
	
	public static String getTokenUserCacheKey(String tokenType, String accessToken) {
		return TOKENUSER + (tokenType + STRING_EMPTY + accessToken).toLowerCase();
	}
	
	public static String getUserId2TokenUserCacheKey(Long userId) {
		return USERID_TOKEN + userId;
	}
	
	public static void responseMessage(HttpServletResponse response, String message, int status) {
		PrintWriter out;
		try {
			response.setCharacterEncoding(CommonConstant.UTF_8);
			response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
			response.setStatus(status);
			out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			if (out != null) {
				map.put(CODE, -1);
				map.put(DESC, message);
				if(status == 401) {
					map.put(STATUS, 401);
				}
				out.write(JSONObject.toJSONString(map));
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
