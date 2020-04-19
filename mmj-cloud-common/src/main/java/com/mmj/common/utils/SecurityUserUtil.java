package com.mmj.common.utils;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.properties.SecurityConstants;
import com.xiaoleilu.hutool.codec.Base64;

@Slf4j
public class SecurityUserUtil {

    public static long getUserId() {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    	String obj = request.getHeader(SecurityConstants.USER_ID);
        if(obj == null) {
        	log.error("-->请求头中未携带用户ID，请确定请求头中是否带有合法的令牌");
        	throw new CustomException("令牌缺失");
        }
        log.info("-->从请求头中取到用户ID：{}", obj);
    	return Long.valueOf(obj);
    }
    
    public static JwtUserDetails getUserDetails() {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    	String obj = request.getHeader(SecurityConstants.TOKEN_USER);
        if(obj == null) {
        	log.error("-->请求头中未携带用户信息，请确定请求头中是否带有合法的令牌");
        	throw new CustomException("令牌缺失");
        }
        JwtUserDetails user = JSONObject.parseObject(obj, JwtUserDetails.class);
        if(user.getUserFullName() != null) {
        	// okhttp请求头中参数不能带有中文，所以在UserDetailsService.impl中设置用户信息时已将用户昵称进行了base64 encode，所以此处要进行decode
        	String userFullName = Base64.decodeStr(user.getUserFullName(), CommonConstant.UTF_8);
        	user.setUserFullName(userFullName);
        }
        log.info("-->从请求头中取到用户{}的详细信息", user.getUserId());
        return user;
        
//      jwtUser = new JwtUserDetails(217781299002347520L,
//      		"oZ6m94sLUjaEbryEK5hcPI1dhclQ",
//      		"wxca855317c075f407",
//              "写代码的小祖宗",
//              1,
//              1,
//       		 "http://thirdwx.qlogo.cn/mmopen/ajNVdqHZLLCKzJHGv1RnnuhLGoJqxAPjtcw3FR6vGZvdpNtHMRTexrKNHBjDXrf3mm50oEO96S4V4CMADCZHChwqKice5JANb2HDzFfjbFAw/132",
//              null,
//              null,
//             "写代码的小祖宗");
//      return jwtUser;
    }

}