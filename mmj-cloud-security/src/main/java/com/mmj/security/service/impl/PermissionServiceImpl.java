package com.mmj.security.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityProperties;
import com.mmj.common.utils.PermitAllUrl;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.security.service.PermissionService;
import com.xiaoleilu.hutool.collection.CollUtil;
import com.xiaoleilu.hutool.util.StrUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("permissionService")
public class PermissionServiceImpl implements PermissionService {
    
    @Autowired
    SecurityProperties securityProperties;
    
//    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
    	log.info("==>hasPermission");
    	/*
    	String requestUrl = request.getRequestURI();
    	if(!requestUrl.startsWith("/mmj/")) {
    		log.info("服务间内部调用，直接放行");
    		return true;
    	}
    	
    	
        if(RequestMethod.OPTIONS.name().equals(request.getMethod())){
            return true;
        }
        
        AtomicBoolean hasPermission = new AtomicBoolean(false);
        List<String> ignoreurls = securityProperties.getIgnoreurls();
        String[] ignoreurlArray = PermitAllUrl.permitAllUrl(ignoreurls);
        for (String url : ignoreurlArray) {
            if(StringUtils.isNotEmpty(url) && antPathMatcher.match(url, request.getRequestURI())) {
                hasPermission.set(true);
                break;
            }
        }
//        log.info("-->request.getRequestURI():{}，是否放行:{}", request.getRequestURI(), hasPermission.get());
//        if(!hasPermission.get()) {
//            List<String> permissionList = SecurityUserUtil.getUserDetails().getPermissionList();
//            if(CollUtil.isNotEmpty(permissionList)) {
//                permissionList.stream().filter(url -> StrUtil.isNotEmpty(url)
//                        && antPathMatcher.match(url, request.getRequestURI()))
//                        .findFirst().ifPresent(url -> hasPermission.set(true));
//            }
//        }
        
        log.info("-->request url {}, hasPermission result: {}", request.getRequestURI(), hasPermission.get());
        
        return hasPermission.get();
        */
    	
    	// 当前BOSS后台没有配置用户权限的功能，故此处暂不用校验权限
    	return true;
    }
    
}
