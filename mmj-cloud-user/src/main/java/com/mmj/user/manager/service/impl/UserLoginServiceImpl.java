package com.mmj.user.manager.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.user.manager.mapper.UserLoginMapper;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.UserLoginService;

/**
 * <p>
 * 登录关联表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
@Slf4j
@Service
public class UserLoginServiceImpl extends ServiceImpl<UserLoginMapper, UserLogin> implements UserLoginService {
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
    
	@Autowired
	private UserLoginMapper userLoginMapper;
	
	@Override
	public List<UserLogin> getUserAllLoginInfo(long userId) {
		List<UserLogin> userList = null;
		String cacheKey = UserCacheUtil.getUserAllLoginCacheKey(userId);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)) {
            userList = JSONArray.parseArray(cacheValue, UserLogin.class);
        } else {
        	Wrapper<UserLogin> wrapper = new EntityWrapper<UserLogin>();
    		wrapper.eq("USER_ID", userId);
    		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        	userList = this.selectList(wrapper);
        	if(!userList.isEmpty()) {
        		cacheValue = JSONObject.toJSONString(userList, SerializerFeature.WriteMapNullValue);
                redisTemplate.opsForValue().set(cacheKey, cacheValue, 7, TimeUnit.DAYS);
            }
        }
        log.info("-->根据用户ID：{}查询所有登录账号信息结果：{}", userId, cacheValue);
		return userList;
	}
	
	@Override
	public UserLogin getUserLoginInfoByUserName(String userName) {
		UserLogin userLogin = null;
        String cacheKey = UserCacheUtil.getUserLoginCacheKey(userName);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)) {
        	userLogin = JSONArray.parseObject(cacheValue, UserLogin.class);
        } else {
        	List<UserLogin> userList = userLoginMapper.getUserLoginInfoByUserName(userName);
        	if(!userList.isEmpty()) {
        		userLogin = userList.get(0);
                redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(userLogin, SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
            }
        }
        log.info("-->根据登录名{}查询登录账号信息结果：{}", userName, JSONObject.toJSONString(userLogin));
        return userLogin;
	}

}
