package com.mmj.common.interceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mmj.common.annotation.ApiPreventFrequently;
import com.mmj.common.utils.SecurityUserUtil;

/**
 * 接口安全检查：指定时间内，对于同一用户的多个并发请求，只能处理一个，其它的都被认为是频繁操作，程序阻止其执行
 * @author shenfuding
 *
 */
@Component
public class ApiPreventFrequentlyInterceptor extends HandlerInterceptorAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(ApiPreventFrequentlyInterceptor.class);
	
	private static final String AUTHENTICATE_FREQUENTLY = "请勿频繁操作";
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
			throws Exception {
		try {
			if(!(handler instanceof HandlerMethod)) {
				return true;
			}
			HandlerMethod handlerMethod = (HandlerMethod) handler;
			ApiPreventFrequently security = handlerMethod.getMethodAnnotation(ApiPreventFrequently.class);
			if (security == null) {
				return true;
			}
			
			if(security.timeInterval() < 100 || security.timeInterval() > 30000) {
				throw new RuntimeException("timeInterval最好在100毫秒到30秒之间");
			}
			
			long userId = SecurityUserUtil.getUserId();
			String repCacheKey = getRepeatCacheKey(userId, request.getRequestURI());
			long t = redisTemplate.opsForValue().increment(repCacheKey, 1);
			redisTemplate.expire(repCacheKey, security.timeInterval(), TimeUnit.MILLISECONDS);
			if(t > 1) {
				logger.info("-->接口防刷检查-->用户{}高频请求：{}", userId, request.getRequestURI());
				responseMessage(AUTHENTICATE_FREQUENTLY, response);
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			logger.info("-->接口防刷检查发生异常：", e);
		}
		return false;
	}
	
	private String getRepeatCacheKey(Long userId, String path) {
		return new StringBuilder("REP:").append(userId).append(":").append(path).toString();
	}

	private void responseMessage(String message, HttpServletResponse response) {
		PrintWriter out;
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json;charset=utf-8");
			out = response.getWriter();
			Map<String, Object> map = new HashMap<String, Object>();
			if (out != null) {
				map.put("code", -1);
				map.put("desc", message);
				out.write(JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue));
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
