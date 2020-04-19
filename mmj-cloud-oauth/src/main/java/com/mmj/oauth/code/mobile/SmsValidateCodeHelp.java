package com.mmj.oauth.code.mobile;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.exception.ValidateCodeException;
import com.mmj.common.properties.SecurityProperties;
import com.mmj.common.utils.CommonUtil;
import com.mmj.oauth.feign.NoticeFeignClient;
import com.xiaoleilu.hutool.codec.Base64;

@Slf4j
@Component
public class SmsValidateCodeHelp {
	
	private static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json;charset=utf-8";
	private static final String STATUS = "status";
	private static final String DESC = "desc";
	private static final String CODE = "code";
    
    @Autowired
    private SecurityProperties securityProperties;
    
    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;
    
    @Autowired
    private NoticeFeignClient noticeFeignClient;
    
    private static final String ON_MIN_CLLXCX = "ON_MIN_CLLXCX";
    
    public void send(HttpServletRequest request) {
        String mobile = request.getParameter("mobile");
        if(!CommonUtil.checkMobile(mobile)) {
        	throw new CustomException("手机号格式不正确");
        }
        String codeKey = buildKey(request);
        ValidateCode validateCode = generate(codeKey, request);
        redisTemplate.opsForValue().set(codeKey, validateCode, 5, TimeUnit.MINUTES);
        log.info("下面向手机{}发送短信验证码{}",mobile, validateCode.getCode());
        JSONObject json = new JSONObject();
        json.put("mobiles", mobile);
        json.put("msg", "【买买家】验证码："+validateCode.getCode()+",有效期五分钟，祝您购物愉快。");
        noticeFeignClient.sendSms(JSONObject.toJSONString(json));
        
    }
    
    public ValidateCode generate(String codeKey, HttpServletRequest request) {
        String code = RandomStringUtils.randomNumeric(securityProperties.getCode().getSmslength());
        log.debug("SmsValidateCodeHelp generate code to redis key {}", codeKey);
        return new ValidateCode(code, securityProperties.getCode().getExpireIn());
    }
    
    public ValidateCode get(HttpServletRequest request) {
        String codeKey = buildKey(request);
        log.debug("SmsValidateCodeHelp get code from redis key {}", codeKey);
        Object value = redisTemplate.opsForValue().get(codeKey);
        if(value==null){
            return null;
        }
        return (ValidateCode) value;
    }
    
    public void remove(HttpServletRequest request) {
        redisTemplate.delete(buildKey(request));
    }
    
    public void validate(HttpServletRequest request, HttpServletResponse response) {
    	String channel = request.getParameter("channel");
    	if(ON_MIN_CLLXCX.equalsIgnoreCase(channel)) {
    		// 如果是车来了的渠道，则不用验证
    		log.info("-->当前登录用户为车来了的渠道用户，直接验证通过");
    		return;
    	}
    	String refreshToken = request.getParameter("refresh_token");
    	if(StringUtils.isNotBlank(refreshToken)) {
    		log.info("-->刷新令牌操作：{}", refreshToken);
    		try {
    			String result = Base64.decodeStr(refreshToken.split("\\.")[1], CommonConstant.UTF_8);
    			JSONObject obj = JSONObject.parseObject(result);
    			String mobile = request.getParameter("mobile");
    			String userName = obj.getString("userName");
    			if(!mobile.equals(userName)) {
    				log.error("-->注意！！！令牌中的登录名和用户传来的手机号不相等，有可能为恶意请求，手机号：{}，登录名：{}", mobile, userName);
    				this.responseMessage(response, "令牌无效", 401);
    			}
    			long exp = obj.getLongValue("exp");
    			long timestamp = exp * 1000;
    			long currentTimestamp = System.currentTimeMillis();
    			if(currentTimestamp >= timestamp) {
    				this.responseMessage(response, "令牌已过期", 401);
    			}
    			return;
			} catch (Exception e) {
				this.responseMessage(response, "令牌无效", 401);
			}
    	}
        String codeInRequest = request.getParameter("code");
        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码的值不能为空");
        }
        ValidateCode codeInRedis = get(request);
        if (codeInRedis == null) {
            throw new ValidateCodeException("验证码不存在");
        }
        if (codeInRedis.isExpried()) {
            remove(request);
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equals(codeInRedis.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码不匹配");
        }
    }
    
    private String buildKey(HttpServletRequest request){
        String mobile = request.getParameter("mobile");
        if(StringUtils.isBlank(mobile)){
            throw new ValidateCodeException("请填写手机号");
        }
        return "code:mobile:" + mobile;
    }

    public void responseMessage(HttpServletResponse response, String message, int status) {
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
