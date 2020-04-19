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
import com.mmj.common.annotation.ApiWaitForCompletion;
import com.mmj.common.utils.SecurityUserUtil;

/**
 * 接口安全检查：如果一个用户有多个并发请求同时到达，可保证第一个请求处理完毕后，才能处理后续的相同请求<br/>
 * 如果第一个请求还未处理完，则其它的并发请求都被认为是频繁操作，程序阻止其执行
 * @author shenfuding
 *
 */
@Component
public class ApiWaitForCompletionInterceptor extends HandlerInterceptorAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(ApiWaitForCompletionInterceptor.class);
	
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
			ApiWaitForCompletion security = handlerMethod.getMethodAnnotation(ApiWaitForCompletion.class);
			if (security == null) {
				return true;
			}
			
			long userId = SecurityUserUtil.getUserId();
			String reqCacheKey = getReqCacheKey(userId, request.getRequestURI());
			long t = redisTemplate.opsForValue().increment(reqCacheKey, 1);
			// 给一个过期时间60s，这里的值和ribbon配置的请求超时时间需要保持一致，防止接口处理超时导致该用户对同一接口无法再次请求，保证重试的请求被转发到其它实例时可正常访问
			redisTemplate.expire(reqCacheKey, 60, TimeUnit.SECONDS);
			if(t > 1) {
				logger.info("-->接口安全检查-->用户{}高频请求：{}", userId, request.getRequestURI());
				responseMessage(AUTHENTICATE_FREQUENTLY, response);
				return false;
			}
			
			return true;
			
		} catch (Exception e) {
			logger.info("-->接口安全检查发生异常：", e);
		}
		return false;
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		ApiWaitForCompletion security = handlerMethod.getMethodAnnotation(ApiWaitForCompletion.class);
		if (security != null) {
			logger.info("-->接口:{}执行完毕", request.getRequestURI());
			long userId = SecurityUserUtil.getUserId();
			// 删除缓存
			redisTemplate.delete(getReqCacheKey(userId, request.getRequestURI()));
		}
	}

	private String getReqCacheKey(Long userId, String path) {
		return new StringBuilder("REQ:").append(userId).append(":").append(path).toString();
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
