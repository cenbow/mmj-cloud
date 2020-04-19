package com.mmj.zuul.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserLog;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ZuulErrorFilter extends ZuulFilter {
    
    private static final String ZUUL_ERROR_FILTER_USER_LOG_STR = "Zuul forward request error: {}";

	@Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        UserLog userLog = UserLogHelper.initUserLog(requestContext);
        log.info(ZUUL_ERROR_FILTER_USER_LOG_STR, JSON.toJSONString(userLog));
        return new ReturnData<String>(0, requestContext.getThrowable().getMessage());
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getThrowable() != null;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER + 1;
    }

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

}
