package com.mmj.common.interceptor;

import com.mmj.common.properties.SecurityConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final String FEIGN_REQUEST_STR = "Feign request url: {}";

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (null != attributes && null != attributes.getRequest())
            log.info(FEIGN_REQUEST_STR, attributes.getRequest().getRequestURI());

        if (null != attributes) {
            HttpServletRequest request = attributes.getRequest();
            template.header(SecurityConstants.TOKEN_USER, request.getHeader(SecurityConstants.TOKEN_USER));
            template.header(SecurityConstants.USER_ID, request.getHeader(SecurityConstants.USER_ID));
//            template.header(SecurityConstants.HEAD_GRAY, request.getHeader(SecurityConstants.HEAD_GRAY));
            template.header(SecurityConstants.APP_TYPE, request.getHeader(SecurityConstants.APP_TYPE));
        }
    }

}
