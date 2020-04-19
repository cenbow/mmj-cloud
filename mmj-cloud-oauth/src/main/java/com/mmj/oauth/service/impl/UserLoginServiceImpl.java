package com.mmj.oauth.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.LoginType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.CustomException;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.oauth.mapper.UserLoginMapper;
import com.mmj.oauth.model.UserLogin;
import com.mmj.oauth.service.UserLoginService;
import com.mmj.oauth.supper.PhoneBindedException;

/**
 * <p>
 * 登陆关联表 服务实现类
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
	
	private static final String USER_ID = "USER_ID";

	@Override
	public List<UserLogin> getUserAllLoginInfo(long userId) {
		List<UserLogin> userList = null;
		String cacheKey = UserCacheUtil.getUserAllLoginCacheKey(userId);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)) {
            userList = JSONArray.parseArray(cacheValue, UserLogin.class);
        } else {
        	Wrapper<UserLogin> wrapper = new EntityWrapper<UserLogin>();
    		wrapper.eq(USER_ID, userId);
    		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        	userList = this.selectList(wrapper);
        	BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
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
        log.info("-->根据登录名{}查询登录信息结果：{}", userName, JSONObject.toJSONString(userLogin));
        return userLogin;
	}

	@Override
	public UserLogin getUserPhoneLoginInfoByUserId(long userId) {
		List<UserLogin> userList = this.getUserAllLoginInfo(userId);
		if(userList.isEmpty()) {
			return null;
		}
		UserLogin userLogin = null;
		for(UserLogin ul : userList) {
			if(LoginType.mobile.name().equalsIgnoreCase(ul.getLoginType())) {
				userLogin = ul;
				break;
			}
		}
		log.info("-->根据用户ID：{}查询手机号登录账号信息结果：{}", userId, JSONObject.toJSONString(userLogin));
		return userLogin;
	}

	@Override
	public boolean savePhoneLoginInfo(UserLogin userLogin) {
		boolean result = false;
		if(!LoginType.mobile.name().equalsIgnoreCase(userLogin.getLoginType())) {
			throw new CustomException("参数错误");
		}
		// 判断要绑定的手机号是否已被其他人绑定
		UserLogin phoneLoginInfo = this.getUserLoginInfoByUserName(userLogin.getUserName()); 
		if(phoneLoginInfo != null) {
			// 再检查userId是否不一致
			if(!phoneLoginInfo.getUserId().equals(userLogin.getUserId())) {
				// 不一致，则说明该手机号已被其它人绑定，可能是在H5绑定的
				log.error("-->用户{}授权获取到的手机号{}已被其他人绑定", userLogin.getUserId(), userLogin.getUserName());
				throw new PhoneBindedException("该手机号已被其他人绑定，请联系客服");
			} 
		} else {
			// 说明授权获取的手机号没有被任何人绑定过-->直接添加手机号登录信息
			log.info("-->用户{}授权获取到的手机号{}没有没有被任何人绑定过，此次直接绑定", userLogin.getUserId(), userLogin.getUserName());
			/** 保存用户登录信息 **/
			this.saveLoginInfo(userLogin);
			result = true;
		}
		return result;
	}

	@Override
	public String getUserPhone(long userId) {
		UserLogin userLogin = this.getUserPhoneLoginInfoByUserId(userId);
		return userLogin != null ? userLogin.getUserName() : null;
	}

	@Override
	public void saveLoginInfo(UserLogin userLogin) {
		log.info("-->插入登录信息，userId:{}, userName:{}, appId:{}, loginType:{}", 
				userLogin.getUserId(), userLogin.getUserName(), userLogin.getAppId(), userLogin.getLoginType());
		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userLogin.getUserId());
		this.insert(userLogin);
		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
		redisTemplate.delete(UserCacheUtil.getUserAllLoginCacheKey(userLogin.getUserId()));
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public void updateUserId(Long oldUserId, Long newUserId) {
		
		// 判断是否需要切换表
		int oldTableIndex = (int) (oldUserId % 10);
		int newTableIndex = (int) (newUserId % 10);
		log.info("-->oldUserId:{}所在表t_user_login_{}，newUserId:{}所在表t_user_login_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
		if(oldTableIndex != newTableIndex) {
			// 需要切换表
			// 分两步：先查出登录表中oldUserId对应的所有数据，更换为newUserId，插入到新表
			List<UserLogin> userLoginList = this.getUserAllLoginInfo(oldUserId);
			for(UserLogin userLogin : userLoginList) {
				userLogin.setUserId(newUserId);
			}
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
			this.insertBatch(userLoginList);
			log.info("-->将用户{}的数据迁移到新表t_user_login_{}，新的userId为：{}", oldUserId, newTableIndex, newUserId);
			// 然后再删除旧数据
			this.deleteUserAllLoginInfo(oldUserId);
		} else {
			// 两个userId在同一张表，直接修改userId
			log.info("-->新旧ID都在同一张表：t_user_login_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
			userLoginMapper.updateUserId(oldUserId, newUserId);
		}
		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
	}

	@Override
	public void deleteUserAllLoginInfo(long userId) {
		
		// 获取用户的所有登录账号
		List<UserLogin> userLoginList = this.getUserAllLoginInfo(userId);
		
		Wrapper<UserLogin> wrapper = new EntityWrapper<UserLogin>();
		wrapper.eq("USER_ID", userId);
		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
		this.delete(wrapper);
		BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
		this.deleteUserLoginCache(userId, userLoginList);
		log.info("-->删除用户{}的登录账号信息", userId);
	}
	
	private void deleteUserLoginCache(long userId, List<UserLogin> userLoginList) {
		this.redisTemplate.delete(UserCacheUtil.getUserAllLoginCacheKey(userId));
		for(UserLogin user : userLoginList) {
			this.redisTemplate.delete(UserCacheUtil.getUserLoginCacheKey(user.getUserName()));
			log.info("-->删除用户缓存：{}", UserCacheUtil.getUserLoginCacheKey(user.getUserName()));
		}
	}
}
