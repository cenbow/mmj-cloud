package com.mmj.user.manager.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import com.mmj.common.constants.LoginType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.user.manager.dto.BaseUserDto;
import com.mmj.user.manager.dto.SearchUserParamDto;
import com.mmj.user.manager.mapper.BaseUserMapper;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.manager.service.UserLoginService;

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
	private UserLoginService userLoginService;
	
	@Autowired
	private BaseUserMapper baseUserMapper;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	/**
	 * 搜索类型：搜索BOSS后台的用户
	 */
	private static final String SEARCH_TYPE_BOSS = "BOSS";
	
	/**
	 * 搜索类型：搜索微信用户
	 */
	private static final String SEARCH_TYPE_WECHAT = "WECHAT";
	
	private static final String LOGIN_TYPE_OPENID = "openid";
	
	private static final String MIN_APPID = "wx7a01aef90c714fe2";
	
	@Override
	public Set<BaseUserDto> search(SearchUserParamDto dto) {
		if(!SEARCH_TYPE_BOSS.equalsIgnoreCase(dto.getSearchType()) && !SEARCH_TYPE_WECHAT.equals(dto.getSearchType())) {
			throw new CustomException("查询参数缺失");
		}
		Set<BaseUserDto> resultSet = new HashSet<BaseUserDto>();
		BaseUser baseUser = null;
		BaseUserDto baseUserDto = null;
		if(SEARCH_TYPE_BOSS.equalsIgnoreCase(dto.getSearchType())) {
			log.info("-->搜索BOSS后台的用户");
			// 表示只查询BOSS后台的用户，查询条件只处理用户昵称即可，其它查询条件忽略
			String userFullName = dto.getUserFullName();
			if(StringUtils.isNotBlank(userFullName)) {
				List<BaseUser> baseUserList = baseUserMapper.getBaseUserByUserFullNameLike(userFullName, dto.getSearchType());
				if(!baseUserList.isEmpty()) {
					for(BaseUser user : baseUserList) {
						baseUserDto = new BaseUserDto();
						baseUserDto.setUserId(user.getUserId()); //boss后台的用户直接使用t_base_user_*表的userId
						baseUserDto.setUserFullName(user.getUserFullName());
						baseUserDto.setImagesUrl(user.getImagesUrl());
						resultSet.add(baseUserDto);
					}
				}
			}
			
		} else if(SEARCH_TYPE_WECHAT.equals(dto.getSearchType())) {
			log.info("-->搜索小程序用户");
			Set<Long> userIdSet = dto.getUserIdSet();
			if(userIdSet != null && userIdSet.size() > 0) {
				// 表示是根据用户ID集合查询，其它查询条件可忽略
				for(long userId : userIdSet) {
					baseUser = this.getById(userId);
					if(baseUser != null) {
						baseUserDto = new BaseUserDto();
						baseUserDto.setUserId(userId); // userId使用传来的，这是主用户ID
						baseUserDto.setUserFullName(baseUser.getUserFullName());
						baseUserDto.setImagesUrl(baseUser.getImagesUrl());
						resultSet.add(baseUserDto);
					}
				}
			} else {
				// 根据用户昵称模糊匹配
				String userFullName = dto.getUserFullName();
				if(StringUtils.isNotBlank(userFullName)) {
					List<BaseUser> baseUserList = baseUserMapper.getBaseUserByUserFullNameLike(userFullName, dto.getSearchType());
					if(!baseUserList.isEmpty()) {
						for(BaseUser user : baseUserList) {
							baseUserDto = new BaseUserDto();
							baseUserDto.setUserId(user.getUserId()); // 查询语句中已经加了用户状态(USER_STATUS=1即是主用户)的过滤，所以此处user.getUserId()就是主userId
							baseUserDto.setUserFullName(user.getUserFullName());
							baseUserDto.setImagesUrl(user.getImagesUrl());
							resultSet.add(baseUserDto);
						}
					}
				}
				// 根据openId全匹配
				String openId = dto.getOpenId();
				if(StringUtils.isNotBlank(openId)) {
					UserLogin userLoginInfo = userLoginService.getUserLoginInfoByUserName(openId);
					if(userLoginInfo != null) {
						if(LOGIN_TYPE_OPENID.equalsIgnoreCase(userLoginInfo.getLoginType())) {
							long userId = userLoginInfo.getUserId();
							baseUser = this.getById(userId);
							if(baseUser != null) {
								baseUserDto = new BaseUserDto();
								baseUserDto.setUserId(userId);
								baseUserDto.setUserFullName(baseUser.getUserFullName());
								baseUserDto.setImagesUrl(baseUser.getImagesUrl());
								resultSet.add(baseUserDto);
							}
						}
					}
				}
			}
		}
		log.info("-->搜索到的用户结果条数：{}", resultSet.size());
		return resultSet;
	}

	@Override
	public BaseUserDto queryUserInfoByUserId(Long userId, String appId) {
		log.info("-->根据userId和appId查询用户信息：{},{}", userId, appId);
		
		if(StringUtils.isBlank(appId)) {
			throw new CustomException("appId缺失");
		}
		
		List<UserLogin> list = userLoginService.getUserAllLoginInfo(userId);
		if(list.isEmpty()) {
			throw new CustomException("用户数据缺失");
		}
		
		String openId = null;
		for(UserLogin user: list) {
			if(appId.equalsIgnoreCase(user.getAppId()) && LOGIN_TYPE_OPENID.equals(user.getLoginType())) {
				openId = user.getUserName();
				log.info("-->用户{}在appId为{}端下的openId为:{}", userId, appId, openId);
				break;
			}
		}
		if(openId == null) {
			log.info("-->用户{}在appId为{}端下的openId没有找到", userId, appId);
			// 如果openId为空，则取小程序appId下对应的openId
			// 	这种场景理应不存在，但就担心出现这种神仙都想不到的意外情况
			for(UserLogin user: list) {
				if(MIN_APPID.equalsIgnoreCase(user.getAppId()) && LOGIN_TYPE_OPENID.equals(user.getLoginType())) {
					openId = user.getUserName();
					log.info("-->用户{}在appId为{}端下的openId没有找到，但找到了小程序端的openId:{}", userId, appId, openId);
					break;
				}
			}
		}
		// 极端场景，以防万一，如果以上两步均未取到，则随便取这个用户的一个openId即可
		if(openId == null) {
			for(UserLogin user: list) {
				if(LoginType.openid.name().equalsIgnoreCase(user.getLoginType())) {
					openId = user.getUserName();
					log.info("-->用户{}在appId为{}端下的openId没有找到，也没有小程序端的，此处取用户第一个openId:{}", userId, appId, openId);
					break;
				}
			}
		}
		
		BaseUserDto baseUserDto = new BaseUserDto();
		baseUserDto.setUserId(userId);
		baseUserDto.setOpenId(openId);
		
		BaseUser baseUser = this.getById(userId);
		if(baseUser != null) {
			baseUserDto.setUserFullName(baseUser.getUserFullName());
			baseUserDto.setImagesUrl(baseUser.getImagesUrl());
			
		}
		return baseUserDto;
	}
	
	@Override
	@Transactional
	public BaseUser getById(Long userId) {
		log.info("-->根据用户ID：{}查询基础信息 start...", userId);
		BaseUser baseUser = null;
		String cacheKey = UserCacheUtil.getBaseUserCacheKey(userId);
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			baseUser = JSONArray.parseObject(cacheValue, BaseUser.class);
		} else {
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
			baseUser = this.selectById(userId);
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, null);
			if(baseUser != null) {
				redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(baseUser, SerializerFeature.WriteMapNullValue), 7, TimeUnit.DAYS);
			}
		}
		log.info("-->根据用户ID：{}查询基础信息结果：{}", userId, JSONObject.toJSONString(baseUser));
        return baseUser;
	}

	@Override
	public List<ChannelUserVO> getChannelUsers(ChannelUserParam param) {
		return baseUserMapper.getChannelUsers(param);
	}

	@Override
	public BaseUserDto queryUserInfoByPhone(String phone) {
		UserLogin userLogin = this.userLoginService.getUserLoginInfoByUserName(phone);
		if(userLogin == null) {
			return null;
		}
		
		long userId = userLogin.getUserId();
		BaseUserDto baseUserDto = new BaseUserDto();
		baseUserDto.setUserId(userId);
		BaseUser baseUser = this.getById(userId);
		if(baseUser != null) {
			baseUserDto.setUserFullName(baseUser.getUserFullName());
			baseUserDto.setImagesUrl(baseUser.getImagesUrl());
		}
		return baseUserDto;
	}

}

