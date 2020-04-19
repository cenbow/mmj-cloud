package com.mmj.zuul.filter;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.mmj.common.model.UserLog;
import com.netflix.zuul.context.RequestContext;
import com.xiaoleilu.hutool.http.HttpUtil;
import com.xiaoleilu.hutool.util.URLUtil;

public class UserLogHelper {
    
	
    private static final String USER_ID = "userId";
	private static final String SERVICE_ID = "serviceId";
	private static final String START_TIME = "startTime";

	public static UserLog initUserLog(RequestContext requestContext) {
        HttpServletRequest request = requestContext.getRequest();
        UserLog userLog = new UserLog();
        userLog.setRequestHost(HttpUtil.getClientIP(request));
        userLog.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        userLog.setRequestMethod(request.getMethod());
        Long startTime = (Long) requestContext.get(START_TIME);
        userLog.setRequestTime(new Date(startTime));
        userLog.setRequestSpendTime(System.currentTimeMillis() - startTime);
        if (requestContext.get(SERVICE_ID) != null) {
            userLog.setServiceId(requestContext.get(SERVICE_ID).toString());
        }
        if(requestContext.get(USER_ID) != null) {
        	userLog.setUserId(Long.valueOf(requestContext.get(USER_ID).toString()));
        }
        return userLog;
    }

}
