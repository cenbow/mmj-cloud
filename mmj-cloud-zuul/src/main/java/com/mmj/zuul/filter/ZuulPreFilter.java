package com.mmj.zuul.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.zuul.util.ZuulUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * 本过滤器在请求被路由之前调用<br/>
 * 可进行身份验证、记录调试信息
 * @author shenfuding
 *
 */
@Slf4j
@Component
public class ZuulPreFilter extends ZuulFilter {
    
	private static final String GRANT_TYPE = "grant_type";
	private static final String USER_ID = "userId";
	private static final String TOKEN_INVALID = "令牌无效";
	private static final String TOKEN_ILLEGAL = "令牌非法";
	private static final String BASIC = "basic";
	private static final String BEARER = "bearer";
	private static final String CAN_NOT_REFRESH_TOKEN = "不支持刷新令牌操作";
	private static final String REQUEST_TAKES_INVALID_PARAM = "请求中带有非法参数";
	private static final String REFRESH_TOKEN = "refresh_token";
	private static final String TRUE = "true";
	private static final String ALL = "*";
	private static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
	private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
	private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
	private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
	private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	private static final String START_TIME = "startTime";
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	/**
     * 指定过当前过滤器的类型：pre - 路由之前
     * 其它类型包括：
     * 	routing：路由之时
     *  post： 路由之后
     *  error：发送错误调用
     */
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
    
    /**
     * 指定当前过滤器的过滤顺序
     */
    @Override
    public int filterOrder() {
        return FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER - 1;
    }

	@Override
    public Object run() throws ZuulException {
		
		// 获取当前请求的上下文，此上下文贯穿整个请求
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        
        // 此处记录请求的开始时间，便于在ZuulPostFilter中记录请求的时间花费，不论是OPTIONS请求还是POST、GET请求
        requestContext.set(START_TIME, System.currentTimeMillis());
        
        /* 灰度发布暂不使用，故先注释
        String grayflag = requestContext.getRequest().getHeader(SecurityConstants.HEAD_GRAY);
        if(StringUtils.isNotEmpty(grayflag)) {
            BaseContextHandler.set(SecurityConstants.THREAD_LOCAL_GRAY_KEY, grayflag);
            requestContext.addZuulRequestHeader(SecurityConstants.HEAD_GRAY, grayflag);
        }
        */
        
        if(RequestMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
        	// 对OPTIONS请求不进行路由
            requestContext.setSendZuulResponse(false);
            
            // 对OPTIONS请求直接返回HTTP状态码200
            requestContext.setResponseStatusCode(200);
            
            // 对OPTIONS请求设置响应头，防止跨域
    		response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, ALL);
    		response.setHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS, TRUE);
    		response.setHeader(ACCESS_CONTROL_ALLOW_HEADERS, ALL);
    		response.setHeader(ACCESS_CONTROL_ALLOW_METHODS, ALL);
    		response.setHeader(ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        } else {
        	
        	// 判断是否带有非法请求
        	if(request.getHeader(SecurityConstants.TOKEN_USER) != null ) {
        		requestContext.setSendZuulResponse(false);
        		ZuulUtil.responseMessage(requestContext.getResponse(), REQUEST_TAKES_INVALID_PARAM, 200);
            	return null;
        	}
        	
        	// 判断是否为刷新令牌操作，是则zuul不进行转发，直接返回提示给前端
        	//     为何不准刷新令牌? 因为当前是将用户信息放到redis缓存，令牌为key，并给定了2小时有效期，而刷新令牌不会查询用户，即不会进到UserDetailsServiceImpl
        	//     而此时已过期的令牌在缓存中的信息已失效，所以此时即使拿到了新的令牌，但没法指定其对应的用户信息是什么，所以不能支持刷新令牌操作
        	//     前端发现令牌过期的话，直接通过登录名再获取一次令牌即可
        	String grantType = request.getParameter(GRANT_TYPE);
        	boolean isRequestRefreshAccessToken = StringUtils.isNotBlank(grantType) && REFRESH_TOKEN.equalsIgnoreCase(grantType);
        	if(isRequestRefreshAccessToken) {
        		requestContext.setSendZuulResponse(false);
        		log.error(CAN_NOT_REFRESH_TOKEN);
        		ZuulUtil.responseMessage(requestContext.getResponse(), CAN_NOT_REFRESH_TOKEN, 200);
            	return null;
        	}
        	
        	// 不管当前URL是否被配置了放开校验，只要发现请求头中带了令牌，则就进行身份校验，如果不合法，则Zuul就不进行转发，直接返回提示给前端，达到快速响应
        	// 如果请求头没有带令牌，则此处不管，因为本过滤器是针对所有请求，不知道是否需要校验用户身份，所以由zuul进行转发请求到具体的服务，<br/>
        	//    后续的校验交由CustomOauth2AuthorizationInterceptor来处理，该拦截器配置的时候有指定哪些URL不进行令牌校验,只要进到该拦截器的URL都要进行身份校验
        	String authorizationToken = request.getHeader(SecurityConstants.HEAD_AUTH);
        	if(authorizationToken == null) {
        		return null;
        	}
        	authorizationToken = authorizationToken.toLowerCase();
    		if(!authorizationToken.startsWith(BEARER) && !authorizationToken.startsWith(BASIC)) {
    			requestContext.setSendZuulResponse(false);
        		log.error("-->令牌非法：{}, 值：{}", request.getRequestURI(), request.getHeader(SecurityConstants.HEAD_AUTH));
        		ZuulUtil.responseMessage(response, TOKEN_ILLEGAL, 200);
    			return null;
        	}
    		if(authorizationToken.startsWith(BASIC)) {
    			// 说明该请求是需要进行获取access_token的，此时需要将请求头传递给为Oauth模块
    			requestContext.addZuulRequestHeader(SecurityConstants.HEAD_AUTH, request.getHeader(SecurityConstants.HEAD_AUTH));
    		} else if (authorizationToken.startsWith(BEARER)) {
    			String cacheKey = ZuulUtil.getTokenUserCacheKey(authorizationToken);
                String cacheValue = redisTemplate.opsForValue().get(cacheKey);
                if(StringUtils.isBlank(cacheValue)) {
                	requestContext.setSendZuulResponse(false);
                	log.error("-->令牌无效，可能已过期");
                	ZuulUtil.responseMessage(response, TOKEN_INVALID, 401);
                	return null;
                } 
                JwtUserDetails user = JSONObject.parseObject(cacheValue, JwtUserDetails.class);
                // 设置userId到上下文，以在日志上记录用户ID
                requestContext.set(USER_ID, user.getUserId());
                // 将用户JSON数据放到请求头，便于其它微服务获取该用户
                requestContext.addZuulRequestHeader(SecurityConstants.TOKEN_USER, cacheValue);
                // 将userId也放到请求头，便于其它微服务获取用户ID，因取ID的地方较多，如果每次取用户的JSON数据再转换成对象后获取ID会影响性能
                requestContext.addZuulRequestHeader(SecurityConstants.USER_ID, user.getUserId().toString());
    		}
        }
        
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }
    
}
