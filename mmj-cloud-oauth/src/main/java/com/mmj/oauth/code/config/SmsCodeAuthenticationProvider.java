package com.mmj.oauth.code.config;

import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.mmj.common.utils.CommonUtil;
import com.mmj.oauth.service.WxUserService;

@Slf4j
public class SmsCodeAuthenticationProvider implements AuthenticationProvider {

	UserDetailsService userDetailsService;
	
	WxUserService wxUserService;
	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        SmsCodeAuthenticationToken authenticationToken = (SmsCodeAuthenticationToken) authentication;
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());
        if(user==null){
        	if(CommonUtil.checkMobile((String) authenticationToken.getPrincipal())) {
        		String mobile = (String) authenticationToken.getPrincipal();
        		String appid = authenticationToken.getAppid();
        		String channel = authenticationToken.getChannel();
        		log.info("-->当前手机号登录：{},{},{}", mobile, appid, channel);
        		user = wxUserService.saveUserByMobile(mobile, appid, channel);
        	} else {
        		throw new InternalAuthenticationServiceException("无法获取用户信息");
        	}
        }
        SmsCodeAuthenticationToken authenticationResult = new SmsCodeAuthenticationToken(user,user.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());
        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public UserDetailsService getUserDetailsService() {
        return userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

	public WxUserService getWxUserService() {
		return wxUserService;
	}

	public void setWxUserService(WxUserService wxUserService) {
		this.wxUserService = wxUserService;
	}
}
