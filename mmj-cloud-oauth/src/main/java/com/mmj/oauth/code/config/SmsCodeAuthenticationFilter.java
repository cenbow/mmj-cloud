package com.mmj.oauth.code.config;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.mmj.common.exception.CustomException;

public class SmsCodeAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String TIHOM_FORM_MOBILE_KEY = "mobile";
    private static final String PARAM_APPID = "appid";
    private static final String PARAM_CHANNEL = "channel";
    private boolean postOnly = true;

    public SmsCodeAuthenticationFilter() {
        super(new AntPathRequestMatcher("/login/mobile", "POST"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String mobile = request.getParameter(TIHOM_FORM_MOBILE_KEY);
        if(StringUtils.isBlank(mobile)) {
        	throw new CustomException("手机号参数缺失");
        }

        mobile = mobile.trim();
        String appid = request.getParameter(PARAM_APPID);
        if(StringUtils.isBlank(appid)) {
        	throw new CustomException("appid缺失");
        }
        String channel = request.getParameter(PARAM_CHANNEL);
        SmsCodeAuthenticationToken authRequest = new SmsCodeAuthenticationToken(mobile, appid, channel);
        this.setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, SmsCodeAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }
    
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

}
