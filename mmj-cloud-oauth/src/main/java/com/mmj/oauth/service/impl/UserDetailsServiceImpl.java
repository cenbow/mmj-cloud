package com.mmj.oauth.service.impl;

import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.LoginType;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.oauth.model.UserLogin;
import com.mmj.oauth.service.BaseUserService;
import com.mmj.oauth.service.Oauth2UserService;
import com.xiaoleilu.hutool.codec.Base64;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private static final String TEMPUSER = "TEMPUSER:";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private Oauth2UserService oauth2UserService;
    
    @Autowired
    private UserLoginServiceImpl userLoginService;
    
    @Autowired
    private BaseUserService baseUserService;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	
    	// 查询登录账号信息
        UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(username);
        if(userLogin == null) {
        	log.error("-->loadUserByUsername-->根据登录名{}未找到登录账号信息", username);
        	return null;
        }
        
        log.info("-->loadUserByUsername-->根据登录名{}找到的登录账号信息为：{}", username, JSONObject.toJSONString(userLogin));
        
        JwtUserDetails jwtUser = null;
        
        // 得到用户ID，此ID就是主用户ID
        long userId = userLogin.getUserId(); 
        
        // 查询用户基础信息
        BaseUser baseUser = baseUserService.getById(userId);
        if(baseUser != null) {
            /*
            List<Integer> roleIdList = oauth2UserService.findRoleIdByUserId(oauthUser.getUserId());
            if(CollUtil.isNotEmpty(roleIdList)) {
                String roldIds = StringUtils.join(roleIdList.toArray(), ",");
                permissionList = oauth2UserService.findPermissionByRoleId(roldIds);
            }
            */
        	String openId = null;
        	if(LoginType.openid.name().equalsIgnoreCase(userLogin.getLoginType())) {
        		openId = username;
        	}
            jwtUser = new JwtUserDetails(userLogin.getUserId(), openId, userLogin.getAppId(), username, baseUser.getUserStatus(), baseUser.getUserSex(), baseUser.getImagesUrl(), baseUser.getUserPassword(), baseUser.getUserSalt(), Base64.encode(baseUser.getUserFullName(), CommonConstant.UTF_8));
//            log.info("-->loadUserByUsername-->根据登录名{}返回的JwtUserDetails信息为：{}", username, JSONObject.toJSONString(jwtUser));

            // 将用户放到缓存,然后当前请求响应到zuul时,再从缓存中获取用户, 接着再把key换成令牌进行存储
            String cacheKey = TEMPUSER + username;
            this.redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(jwtUser, SerializerFeature.WriteMapNullValue), 3, TimeUnit.MINUTES);
        }
        
        return jwtUser;
    }

}
