package com.mmj.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.properties.SecurityConstants;

/**
 * 根据请求头中的令牌从缓存中取用户，然后放到线程本地变量，方便线程各处读取用户用户信息，减少用户对redis和DB的查询次数
 * @author shenfuding
 *
 */
@Slf4j
@Component
public class CustomOauth2AuthorizationInterceptor extends HandlerInterceptorAdapter{
	
	private static final String STATUS2 = "status";
	private static final String DESC = "desc";
	private static final String CODE = "code";
	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=utf-8";
	private static final String UTF_8 = "UTF-8";
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 只要进到此拦器器，即说明需要校验用户是否合法，取请求头中的用户信息验证即可
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws Exception {
		if(RequestMethod.OPTIONS.name().equals(request.getMethod())){
            return true;
        }
		String userJson = request.getHeader(SecurityConstants.TOKEN_USER);
		if(StringUtils.isBlank(userJson)) {
			// 只要有传合法令牌，程序走到此处一定可以从请求头中取到用户信息，在ZUUL层已经处理了，如果没有取到用户，那只能说明没有携带令牌
			// 但如果用户请求未带有令牌，但是传了TOKEN_USER的请求头，以此来进行假造身份来访问来怎么办? 放心，在ZUUL层已经做了此参数的校验，要是请求头带了此参数，直接提示参数非法
			// 也就是只准ZUUL层在请求头中添加此用户参数，仿止伪造
			log.error("-->请求未携带令牌：{}", request.getRequestURI());
			responseMessage(response, "令牌缺失", 401);
			return false;
		}
		return true;
	}
	
	private void responseMessage(HttpServletResponse response, String message, int status) {
		PrintWriter out;
		try {
			response.setCharacterEncoding(UTF_8);
			response.setContentType(APPLICATION_JSON_CHARSET_UTF_8);
			response.setStatus(status);
			out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			if (out != null) {
				map.put(CODE, -1);
				map.put(DESC, message);
				if(status == 401) {
					map.put(STATUS2, 401);
				}
				out.write(JSONObject.toJSONString(map));
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
