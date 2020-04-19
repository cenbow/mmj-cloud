package com.mmj.oauth.service;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

import com.mmj.common.model.BaseUser;
import com.mmj.common.model.WxConfig;
import com.mmj.oauth.dto.OfficialAccountUser;
import com.mmj.oauth.dto.WebUser;
import com.mmj.oauth.dto.WxUserParamDto;
import com.mmj.oauth.model.UserLogin;

/**
 * 本接口类提供从微信端获取用户信息的服务
 * @author shenfuding
 *
 */
public interface WxUserService {
	
	/**
	 * 通过CODE从微信获取openid信息以及授权登录返回用户信息
	 * @param paramDto
	 * @return
	 */
	WebUser getUserInfo(WxUserParamDto paramDto);
	
	/**
	 * 小程序登录
	 * @param paramDto
	 * @return
	 */
	WebUser minLogin(WxUserParamDto paramDto);
	
	/**
	 * 公众号登录
	 * @param paramDto
	 * @param baseUser
	 * @param request
	 * @return
	 */
	WebUser officialAccountLogin(WxUserParamDto paramDto, BaseUser baseUser);
	
	/**
	 * APP登录
	 * @param code
	 * @return
	 */
	WebUser appLogin(String code);
	
	/**
	 * 公众号直接保存用户
	 * @param user
	 * @return webUser
	 */
	WebUser publicSave(OfficialAccountUser user);
	
	/**
	 * 取消关注
	 * @param openId
	 * @return
	 */
	boolean unfollow(String openId);
	
	/**
	 * 检查appId是否为空或者错误
	 * @param openId
	 * @param userLogin
	 * @param wxConfig
	 */
	void checkAppId(String openId, UserLogin userLogin, WxConfig wxConfig);
	
	/**
	 * 检查unionId登录信息是否缺失
	 * @param unionId
	 * @param openId
	 * @param userId
	 */
	void checkUnionIdLoginInfo(String unionId, String openId, long userId);
	
	/**
	 * 获取config参数，for H5
	 * @param appid
	 * @param url
	 * @return
	 */
	Map<String, Object> getConfig(String appid, String url);
	
	/**
	 * 手机号授权
	 * @param paramDto
	 * @return
	 */
	Map<String, Object> phoneAuth(WxUserParamDto paramDto);
	
	/**
	 * 合并用户
	 * @param oldUserId
	 * @param newUserId
	 */
	void merge(long oldUserId, long newUserId);
	
	/**
	 * 根据手机号保存用户
	 * @param mobile
	 * @param appid
	 * @param channel
	 * @return UserDetails
	 */
	UserDetails saveUserByMobile(String mobile, String appid, String channel);
	
}
