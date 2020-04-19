package com.mmj.oauth.service.impl;

import java.util.Date;
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
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.BaseUser;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.oauth.mapper.BaseUserMapper;
import com.mmj.oauth.service.BaseUserService;
import com.mmj.oauth.service.Oauth2UserService;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-05-06
 */
@Slf4j
@Service
public class BaseUserServiceImpl extends ServiceImpl<BaseUserMapper, BaseUser> implements BaseUserService {
	
	@Autowired
	private BaseUserMapper baseUserMapper;
	
	@Autowired
	private Oauth2UserService oauth2UserService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Override
	@Transactional
	public boolean save(BaseUser baseUser) {
		Date now = new Date();
		baseUser.setCreaterId(baseUser.getUserId());
		baseUser.setCreaterTime(now);
		baseUser.setModifyId(baseUser.getUserId());
		baseUser.setModifyTime(now);
    	log.info("-->保存用户{}基础信息，参数：{}", baseUser.getUserId(), JSONObject.toJSONString(baseUser));
    	BaseContextHandler.set(SecurityConstants.SHARDING_KEY, baseUser.getUserId());
		return this.insert(baseUser);
	}

	@Override
	@Transactional
	public boolean update(BaseUser baseUser) {
    	baseUser.setModifyId(baseUser.getUserId());
    	baseUser.setModifyTime(new Date());
    	BaseContextHandler.set(SecurityConstants.SHARDING_KEY, baseUser.getUserId());
    	boolean result = this.updateById(baseUser);
    	redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(baseUser.getUserId()));
    	redisTemplate.delete(UserCacheUtil.getBaseUserCacheKey(baseUser.getOpenId()));
    	log.info("-->更新用户{}基础信息", baseUser.getUserId());
        return result;
	}

	@Override
	@Transactional
	public BaseUser getById(Long userId) {
		BaseUser baseUser = null;
		String cacheKey = UserCacheUtil.getBaseUserCacheKey(userId);
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			baseUser = JSONArray.parseObject(cacheValue, BaseUser.class);
		} else {
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
			baseUser = this.selectById(userId);
			if(baseUser != null) {
				redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(baseUser, SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
			}
		}
		log.error("-->根据用户ID:{}查询基础信息,结果:{}", userId, JSONObject.toJSONString(baseUser));
        return baseUser;
	}

	@Override
	public BaseUser getByOpenId(String openId) {
		String cacheKey = UserCacheUtil.getBaseUserCacheKey(openId);
        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
        if(StringUtils.isNotBlank(cacheValue)) {
        	return JSONObject.parseObject(cacheValue, BaseUser.class);
        } 
        BaseUser baseUser = baseUserMapper.getByOpenId(openId);
    	if(baseUser != null) {
    		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(baseUser, SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
    	}
		return baseUser;
	}

}
