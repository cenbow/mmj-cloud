package com.mmj.zuul.filter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.UserLog;
import com.mmj.zuul.util.ZuulUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.xiaoleilu.hutool.util.URLUtil;

/**
 * 本过滤器在请求被路由之后调用，即获取到响应之后<br/>
 * 可用来做指标统计、请求的响应日志记录
 * @author shenfuding
 *
 */
@Slf4j
@Component
public class ZuulPostFilter extends ZuulFilter {
    
    private static final String TOKEN_TYPE = "token_type";
	private static final String USER_ID = "userId";
	private static final String TEMPUSER = "TEMPUSER:";
	private static final String MOBILE = "mobile";
	private static final String OPEN_ID = "openId";
	private static final String USERNAME = "username";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String DATA = "data";
	private static final String UTF_8 = "UTF-8";
	private static final String MMJ_AUTH_LOGIN_MOBILE = "/mmj/auth/login/mobile";
	private static final String MMJ_AUTH_LOGIN_OPENID = "/mmj/auth/login/openid";
	private static final String MMJ_AUTH_OAUTH_TOKEN = "/mmj/auth/oauth/token";
	private static final String MMJ_AUTH = "/mmj/auth/";
	private static final String TRUE = "true";
	private static final String ALL = "*";
	private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String ZUUL_FORWARD_REQUEST_STR = "Zuul forward request: {}";
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	/**
     * 指定过当前过滤器的类型：post - 路由之后，即响应后
     * 其它类型包括：
     *  pre - 路由之前
     * 	routing：路由之时
     *  error：发送错误调用
     */
	@Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
    	RequestContext ctx = RequestContext.getCurrentContext();
    	setTokenUserCache(ctx);
        UserLog userLog = UserLogHelper.initUserLog(ctx);
        log.info(ZUUL_FORWARD_REQUEST_STR, JSON.toJSONString(userLog));
		HttpServletResponse response = ctx.getResponse();
        response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
		response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
		response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, ALL);
		response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ALL);
		response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        return null;
    }
    
    private void setTokenUserCache(RequestContext ctx) {
    	HttpServletRequest request = ctx.getRequest();
    	String url = URLUtil.getPath(request.getRequestURI());
    	if(!url.startsWith(MMJ_AUTH)) {
    		return;
    	}
    	boolean isPasswordLogin = url.contains(MMJ_AUTH_OAUTH_TOKEN);
    	boolean isOpenIdLogin = url.contains(MMJ_AUTH_LOGIN_OPENID);
    	boolean isMobileLogin = url.contains(MMJ_AUTH_LOGIN_MOBILE);
    	if(!(isPasswordLogin || isOpenIdLogin || isMobileLogin)) {
    		return;
    	}
    	InputStream stream = ctx.getResponseDataStream();
		String body = null;
		try {
			body = StreamUtils.copyToString(stream, Charset.forName(UTF_8));
			if (StringUtils.isBlank(body)) {
				ctx.setResponseBody(body);
				return;
			}
			JSONObject resultJson = null;
			if(isPasswordLogin) {
				resultJson = JSONObject.parseObject(body);
			} else {
				// openid登录和手机号登录返回的json经过一层包装，需要取data节点的数据
				resultJson = JSONObject.parseObject(body).getJSONObject(DATA);
			}
			if(resultJson == null) {
        		log.error("-->登录异常！！！未取到响应，URL：{}", request.getRequestURL());
        		ctx.setResponseBody(body);
        		return;
        	}
        	String accessToken = resultJson.getString(ACCESS_TOKEN);
        	String tokenType = resultJson.getString(TOKEN_TYPE);
        	String username = null;
        	if(isPasswordLogin) {
        		username = request.getParameter(USERNAME);
        	} else if(isOpenIdLogin) {
        		username = request.getParameter(OPEN_ID);
        	} else if(isMobileLogin) {
        		username = request.getParameter(MOBILE);
        	}
        	if(StringUtils.isBlank(username)) {
        		log.error("-->登录异常！！！未取到登录名，URL：{}", request.getRequestURL());
        		ctx.setResponseBody(body);
        		return;
        	}
        	String cacheKey = TEMPUSER + username;
        	String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        	if(StringUtils.isBlank(cacheValue)) {
        		log.error("-->登录异常！！！未从缓存中获取到临时用户信息:{}, URL:{}", username, request.getRequestURL());
        		ctx.setResponseBody(body);
        		return;
        	}
        	log.info("-->用户登录成功返回，用户名：{}", username);
    		JwtUserDetails user = JSONObject.parseObject(cacheValue, JwtUserDetails.class);
    		if(user.getUserStatus() == UserConstant.STATUS_LOCK) {
    			ZuulUtil.responseMessage(ctx.getResponse(), "您的账户已被锁定，如果这是系统误判，请联系客服！", 200);
            	return;
            }
        	// 指定用户缓存有效期，注意用户缓存有效期请和生成令牌时指定的有效期一致，虽然可以不一致，但不建议你这么做，不然在oauth中生成令牌和此处缓存令牌用户信息两处指定时间不一致，总会让人摸不着头脑
        	int minutes = 120; 
        	// 缓存用户信息
        	cacheKey = ZuulUtil.getTokenUserCacheKey(tokenType, accessToken);
        	redisTemplate.opsForValue().set(cacheKey, cacheValue, minutes, TimeUnit.MINUTES);
        	// 缓存用户ID和该上一步缓存KEY的关系，主要是为了用户黑名单功能（即锁定用户禁止访问），一旦设置某个用户锁定，
        	// 		则可根据userID找到缓存的KEY，进而删除缓存中对应的用户信息,这样用户就必须要再次进行授权, 然后就会发现被禁用了无法访问，达到实时禁用用户的效果
        	redisTemplate.opsForValue().set(ZuulUtil.getUserId2TokenUserCacheKey(user.getUserId()), cacheKey, minutes, TimeUnit.MINUTES);
        	log.info("-->set user into redis cache:{}", username);
        	
        	// 设置userId到上下文，以在日志上记录用户ID
        	ctx.set(USER_ID, user.getUserId());
		} catch (Exception e) {
			log.error("-->登录异常！！！错误信息:", e);
		}
        ctx.setResponseBody(body);
    }
}
