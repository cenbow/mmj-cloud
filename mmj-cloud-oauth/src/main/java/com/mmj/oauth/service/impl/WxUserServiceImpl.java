package com.mmj.oauth.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.mmj.common.constants.AppTypeConstant;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.LoginType;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.exception.CustomException;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.UserMerge;
import com.mmj.common.model.WxConfig;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.CommonUtil;
import com.mmj.common.utils.DecryptUtils;
import com.mmj.common.utils.HttpURLConnectionUtil;
import com.mmj.common.utils.MD5Util;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.common.utils.UserCacheUtil;
import com.mmj.oauth.channel.model.ChannelConfig;
import com.mmj.oauth.channel.service.ChannelConfigService;
import com.mmj.oauth.dto.Jscode2Session;
import com.mmj.oauth.dto.OfficialAccountUser;
import com.mmj.oauth.dto.WebUser;
import com.mmj.oauth.dto.WxUserParamDto;
import com.mmj.oauth.merge.model.MergeInfo;
import com.mmj.oauth.merge.service.MergeInfoService;
import com.mmj.oauth.model.UserLogin;
import com.mmj.oauth.service.BaseUserService;
import com.mmj.oauth.service.Oauth2UserService;
import com.mmj.oauth.service.UserLoginService;
import com.mmj.oauth.service.WxConfigService;
import com.mmj.oauth.service.WxUserService;
import com.mmj.oauth.supper.PhoneBindedException;
import com.mmj.oauth.util.AESUtil;
import com.mmj.oauth.util.WxUserUtil;

@Slf4j
@Service
public class WxUserServiceImpl implements WxUserService {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
    private Oauth2UserService oauth2UserService;
	
	@Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
	
	@Autowired
    private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private UserLoginService userLoginService;
	
	@Autowired
	private ChannelConfigService channelConfigService;
	
	@Autowired
	private BaseUserService baseUserService;
	
	@Autowired
	private WxConfigService wxConfigService;
	
	@Autowired
	private MergeInfoService mergeInfoService;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	/**
	 * 授权码模式
	 */
	private static final String GRANT_TYPE_AUTHORIZATION_CODE = "authorization_code";
	
	/**
	 * 客户端模式
	 */
	private static final String GRANT_TYPE_CLIENT_CREDENTIAL = "client_credential";
	
	private static final String KEY_JSCODE2SESSION = "JSCODE2SESSION";
	private static final String KEY_APPID = "appid";
	private static final String KEY_SECRET = "secret";
	private static final String KEY_JS_CODE = "js_code";
	private static final String KEY_GRANT_TYPE = "grant_type";
	private static final String KEY_CODE = "code";
	
	/**
     * 获取页面凭证
     */
	private static String URL_GET_TICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
    /**
     * 获取api_token
     */
    private static String URL_GET_ACCESSTOKEN = "https://api.weixin.qq.com/cgi-bin/token";
    
    private static final String KEY_ACCESS_TOKEN = "access_token";
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public WebUser getUserInfo(WxUserParamDto paramDto) {
		String appType = paramDto.getAppType();
		if(StringUtils.isBlank(appType)) {
			throw new CustomException("缺失appType");
		}
		if(StringUtils.isBlank(paramDto.getAppid())) {
			throw new CustomException("缺失appid");
		}
		if(AppTypeConstant.APPTYPE_MIN.equalsIgnoreCase(appType) || AppTypeConstant.APPTYPE_LOTTERY.equalsIgnoreCase(appType)) { 
			// 小程序 登录
			return minLogin(paramDto);
		} else if(AppTypeConstant.APPTYPE_APP.equalsIgnoreCase(appType)) {
			// APP 登录
			return appLogin(paramDto.getCode());
		} else {
			// 微信内H5即公众号 登录
			return officialAccountLogin(paramDto, null);
		}
	}
	
	@Override
	public Map<String, Object> phoneAuth(WxUserParamDto paramDto) {
		log.info("-->用户{}进行手机号授权", paramDto.getOpenId());
		
		// APPID&SECRET配置
		WxConfig wxConfig = wxConfigService.queryByAppId(paramDto.getAppid());
		
		String sessionKey = getSessionKey(paramDto, wxConfig);
		if(StringUtils.isBlank(paramDto.getEncryptedData()) || 
				StringUtils.isBlank(paramDto.getOpenId()) || 
				StringUtils.isBlank(paramDto.getIv()) ||
				StringUtils.isBlank(paramDto.getAppid())) { 
			throw new CustomException("授权参数非法");
		}
		
		String result = null;
        try {
            result = AESUtil.wxDecrypt(paramDto.getEncryptedData(), sessionKey, paramDto.getIv());
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("-->用户{}解密授权数据：{}", paramDto.getOpenId(), result);
        if (StringUtils.isBlank(result)) {
        	throw new CustomException("处理授权发生错误");
        }
        JSONObject phoneJson = JSONObject.parseObject(result);
        String phoneNumber = phoneJson.getString("phoneNumber"); // 用户微信绑定的手机号（国外手机号会有区号）
        String purePhoneNumber = phoneJson.getString("purePhoneNumber"); // 没有区号的手机号
        String countryCode = phoneJson.getString("countryCode"); // // 区号
        
        // 根据openId查询出userId
        String openId = paramDto.getOpenId();
        UserLogin openIdLoginInfo = userLoginService.getUserLoginInfoByUserName(openId);
        if(openIdLoginInfo == null) {
        	log.error("-->用户数据异常，通过openId:{}没有在登录表中找到用户信息", openId);
        	throw new CustomException("用户数据异常，请联系客服");
        }
        
        // 要返回的map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        long userId = openIdLoginInfo.getUserId();
        // 根据userId查询是否有绑定手机号
        UserLogin phoneLoginInfo = userLoginService.getUserPhoneLoginInfoByUserId(userId);
		if(phoneLoginInfo != null) {
			/** 该用户已绑定过手机号 **/
			
			// 判断绑定的手机号是否等于当前授权获取到的手机号
			if(purePhoneNumber.equalsIgnoreCase(phoneLoginInfo.getUserName())) {
				 // 绑定的手机号和当前授权获取到的一致-->不做什么处理
				log.info("-->用户{}之前已经绑定过此手机号：{}，程序不用再做处理", userId, purePhoneNumber);
			} else {
				// 不等于此次授权获取到的手机号，抛出异常
				throw new CustomException("检查到您之前已绑定手机号"+phoneLoginInfo.getUserName()+"，请联系客服");
			}
		} else {
			log.info("-->用户{}之前未绑定过手机号，开始保存手机号登录信息");
			UserLogin userLogin = new UserLogin(purePhoneNumber, LoginType.mobile.name(), userId, paramDto.getAppid());
			try {
				userLoginService.savePhoneLoginInfo(userLogin);
			} catch (PhoneBindedException e) {
				resultMap.put("remark", e.getMessage());
			}
		}
        
        resultMap.put("phoneNumber", phoneNumber); 
        resultMap.put("purePhoneNumber", purePhoneNumber); 
        resultMap.put("countryCode", countryCode);
		return resultMap;
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public WebUser minLogin(WxUserParamDto paramDto) {
		
		log.info("-->小程序登录");
		
		// 要返回的对象
		WebUser webUser = new WebUser(); 
		
		// 要保存的用户登录信息
		UserLogin userLogin;
		
		// 要保存的用户基本信息
		BaseUser baseUser; 
		
		// APPID&SECRET配置
		WxConfig wxConfig = wxConfigService.queryByAppId(paramDto.getAppid());
		
		// 前端传过来的用户授权数据
		String encryptedData = paramDto.getEncryptedData();
		
		if(StringUtils.isNotBlank(encryptedData)) { 
			
			/******************* 表示用户授权登录，此前openId已保存到用户表，此次拿授权信息来更新用户信息 *****************/
			
			log.info("-->小程序授权登录");
			String openId = paramDto.getOpenId();
			if(StringUtils.isBlank(openId)) {
				log.error("-->小程序授权登录-->缺失openId");
				throw new CustomException("缺失openId");
			}
			String sessionKey = getSessionKey(paramDto, wxConfig);
		    String userJsonString = DecryptUtils.decryptData(encryptedData, sessionKey, paramDto.getIv());
		    log.info("-->通过密文解密得到的用户{}的授权数据为:{}", openId, userJsonString);
		    JSONObject userJsonObject = JSONObject.parseObject(userJsonString);
		    
		    String unionId = userJsonObject.getString("unionId");
		    log.info("-->用户{}通过授权得到的unionId为:{}", openId, unionId);
		    if(StringUtils.isBlank(unionId)) {
		    	log.error("-->小程序授权登录-->获取unionId失败");
		    	throw new CustomException("获取unionId失败");
		    }
			
			//////////////////测试代码开始///////////////////
			/*
			String openId = "minopenid";
			String unionId = "shenunionid";
			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("avatarUrl", "http://xxx.com/file/test.jpg");
			userJsonObject.put("nickName", "写代码的小祖宗");
			userJsonObject.put("openId", openId);
			userJsonObject.put("unionId", "shenunionid");
			*/
			//////////////////测试代码结束///////////////////
		    
		    long userId;
		    
		    // 先判断unionId在登录信息中是否存在
		    UserLogin unionIdLoginInfo = userLoginService.getUserLoginInfoByUserName(unionId);
		    boolean merge = false;
		    if(unionIdLoginInfo == null) {
		    	
		    	/** unionId在登录表中不存在，说明此用户未在任何一端进行过授权，故先保存unionId登录信息，使用openId对应的userId，再更新t_base_user信息 **/
		    	
		    	log.info("-->小程序授权登录-->用户{}的unionId:{}在登录表中不存在，此处进行保存", openId, unionId);
		    	
		    	// 保存unionId的登录信息前，需要先知道对应的userId是什么，故此处获取openId在t_user_login对应的userId
		    	UserLogin openIdLoginInfo = userLoginService.getUserLoginInfoByUserName(openId);
			    if(openIdLoginInfo == null) {
			    	// 正常情况下不会进入此分支，因为授权登录前，openId一定是先保存在登录表中存储了， 版本上线前，用户登录小程序一定会先获取openId并保存的
			    	log.error("-->小程序授权登录-->数据异常，用户openId:{}在登录表中不存在", openId);
			    	throw new CustomException("用户数据异常，请联系客服");
			    }
			    userId = openIdLoginInfo.getUserId();
			    log.info("-->小程序授权登录-->查询出openId:{}对应的userId为:{}", openId, userId);
		    	userLogin = new UserLogin(unionId, LoginType.unionid.toString(), userId, "unionid");
				userLoginService.saveLoginInfo(userLogin);
				 /*handelChannelData(paramDto, userId);//处理第三方渠道（如返利小程序）引流-->当前已未合作*/
		    } else {
		    	
		    	/** unionId在登录表中存在，则要判断是否需要合并用户 **/
		    	
		    	userId = unionIdLoginInfo.getUserId(); // 注意，这是unionId对应的userId
		    	log.info("-->小程序授权登录-->用户{}的unioinId:{}在登录表中存在，userId为{}", openId, unionId, userId);
		    	
		    	// 然后取出当前传来的openId对应的userId
		    	// 	      如果两个userId不一致，则表示之前已经在其它端授权过了，此次要修改当前openId对应的userId
		    	UserLogin openIdLoginInfo = userLoginService.getUserLoginInfoByUserName(openId);
			    if(openIdLoginInfo == null) {
			    	// 正常情况下不会进入此分支，因为授权登录前，openId一定是先保存在登录表中存储了， 版本上线前，用户登录小程序一定会先获取openId并保存的
			    	log.error("-->小程序授权登录-->数据异常，用户openId:{}在登录表中不存在", openId);
			    	throw new CustomException("用户数据异常，请联系客服");
			    }
			    long tmpUserId = openIdLoginInfo.getUserId();
	    		log.info("-->用户openId:{}对应的userId为:{}，unionid:{}对应的userId为:{}", openId, tmpUserId, unionId, userId);
	    		if(userId != tmpUserId) {
	    			this.merge(tmpUserId, userId);
	    			merge = true;
	    		}
		    }
		    /** unionId不管在登录表是否存在，都要把授权获取到的最新授权信息更新到t_base_user，因为用户有可能更新头像或者昵称等信息 **/
		    /** 注意，走到此步时openId肯定在数据库有对应的记录，所以只作授权信息更新即可 **/
		    /** 先根据openId获取到在基础表对应的userId，再根据此userId进行更新 **/
			BaseUser bu = baseUserService.getByOpenId(openId);
			if(bu != null) {
				this.updateBaseUser(bu, userJsonObject, merge);
				
				if(!bu.getUserId().equals(userId)) {
					// openid在基础表对应的userId和登录表的不一致，则还需要更新主用户信息
					bu = baseUserService.getById(userId);
					this.updateBaseUser(bu, userJsonObject, false);
				}
			}
			
			// 设置返回给前端需要的信息
			webUser.setUserId(userId);
			webUser.setOpenId(userJsonObject.getString("openId"));
			webUser.setUserName(userJsonObject.getString("nickName"));
			webUser.setAvatarUrl(userJsonObject.getString("avatarUrl"));
			webUser.setGrant(this.isGranted(userJsonObject.getString("nickName"), userJsonObject.getString("avatarUrl")));
			webUser.setPhone(userLoginService.getUserPhone(userId));
			webUser.setUnionId(unionId);
		} else { 
			
			/********************还未授权，仅通过code获取openId********************/

			log.info("-->用户从微信获取openId");
			
			Jscode2Session jscode2Session = getJscode2Session(paramDto, wxConfig);
			if(jscode2Session == null) {
				log.error("-->用户微信获取openId-->获取openid失败");
				throw new CustomException("微信服务异常，获取openid失败"); 
			}
			String openId = jscode2Session.getOpenid();
			log.info("-->用户通过code:{}从微信获取到的openId为:{}", paramDto.getCode(), openId);
			
			/*************判断openId在数据库是否有记录，有则返回用户信息，没有则创建*******************/
			userLogin = userLoginService.getUserLoginInfoByUserName(openId);
			long userId;
			if(userLogin == null) {
				
				/** 说明用户是首次访问当前端，则其在登录表和基础表中应该是都不存在数据的，故先保存登录信息，再保存详细信息***************************/
				
				log.info("-->用户从微信获取的openId:{}在数据库中不存在，下面进行保存", openId);
				
				/** 检查项：判断openId在t_base_user中是否存在，如果存在，则使用基础表中的userId，防止openId被多次保存，考虑到数据异常的场景，实际情况可能只是杞人忧天 **/
				BaseUser bu = baseUserService.getByOpenId(openId);
				long baseUserId = 0;
				if(bu != null) {
					userId = bu.getUserId();
					log.info("-->保存openId:{}的登录信息，使用openId在基础表中的userId:{}", openId, userId);
				} else {
					userId = snowflakeIdWorker.nextId(); // 两张表中都存此userId
					baseUserId = userId;
					log.info("-->保存openId:{}的登录信息，使用新生成的userId:{}", openId, userId);
				}
				
				/** 保存用户登录信息 **/
				userLogin = new UserLogin(openId, LoginType.openid.toString(), userId, wxConfig.getAppId());
				userLoginService.saveLoginInfo(userLogin);
				
				/** 如果openId在t_base_user表中是否存在，不存在才保存，避免openId重复保存了 **/
				if(bu == null) {
					/** 保存用户基础信息 **/
					if(baseUserId > 0) {
						// 因为是一条新的用户数据，所以此处直接使用在登录表中的userId，后面授权后再判断是否需要修改userId
						this.saveBaseUserByOpenIdForMinLogin(openId, baseUserId, paramDto);
					}
				}
				
				/** 设置返回给前端需要的信息 **/
				webUser.setUserId(userId);
				webUser.setOpenId(openId);
				webUser.setUserName(null); // 未授权无法获取用户昵称
				webUser.setAvatarUrl(null); // 未授权无法获取用户头像
				webUser.setPhone(null); // 还未授权无法获取手机号
				webUser.setGrant(false);
			} else {
				
				/****************************** 用户在登录表中存在，查询前端需要的用户信息返回**************************/
				userId = userLogin.getUserId(); // 此userId一定是主用户ID
				
				checkAppId(openId, userLogin, wxConfig);
				
				/** 从t_base_user表中查询昵称和头像 **/
				log.info("-->用户openId:{}在数据库中存在，userId:{}，loginType:{}，下面直接查询用户信息返回", openId, userId, userLogin.getLoginType());
				baseUser = baseUserService.getById(userId); // 注意：此处必须通过userId获取基础信息，要和UserDetailsServiceImpl中查询用户基础信息的调用一致
				if(baseUser == null) {
					log.error("-->用户openId:{}在t_base_user表中数据异常，因为登录表中的userId:{}在基础表未找到", openId, userId);
					throw new CustomException("用户数据异常，请联系客服");
				}
				
				// 设置返回给前端需要的信息
				webUser.setUserId(userId);
				webUser.setOpenId(openId);
				webUser.setUserName(baseUser.getUserFullName());
				webUser.setAvatarUrl(baseUser.getImagesUrl());
				webUser.setGrant(this.isGranted(baseUser.getUserFullName(),baseUser.getImagesUrl()));
				webUser.setPhone(userLoginService.getUserPhone(userId));
				webUser.setUnionId(baseUser.getUnionId());
			}
		}
		log.info("-->小程序登录-->用户{}通过小程序登录返回用户信息{}", webUser.getOpenId(), JSONObject.toJSONString(webUser));
		return webUser;
	}
	
	private void updateBaseUser(BaseUser bu, JSONObject userJsonObject, boolean merge) {
		if(bu != null) {
			BaseUser baseUser = new BaseUser();
			baseUser.setUserId(bu.getUserId()); // 通过userId更新
			baseUser.setUnionId(userJsonObject.getString("unionId")); // 一定要更新unionId
			baseUser.setImagesUrl(userJsonObject.getString("avatarUrl"));
			baseUser.setUserFullName(userJsonObject.getString("nickName"));
			baseUser.setUserSex(userJsonObject.getInteger("gender"));
			baseUser.setUserCity(userJsonObject.getString("city"));
			baseUser.setUserProvince(userJsonObject.getString("province"));
			baseUser.setUserCountry(userJsonObject.getString("country"));
			if(merge) {
				baseUser.setUserStatus(UserConstant.STATUS_DEL);
			}
			baseUserService.update(baseUser);
			log.info("-->小程序授权登录-->更新用户基本信息, userId:{}, nickName:{}", bu.getUserId(), userJsonObject.getString("nickName"));
		}
	}
	
	/**
	 * 检查项：检查openId在t_base_user表中是否存在，如果不存在，则补上，避免数据异常场景，主要针对历史数据 
	 * @param openId
	 * @param userId
	 * @param paramDto
	 */
	public void saveBaseUserByOpenIdForMinLogin(String openId, Long userId, WxUserParamDto paramDto) {
		BaseUser baseUser = new BaseUser();
		baseUser.setUserId(userId); 
		baseUser.setOpenId(openId);
		baseUser.setUserStatus(UserConstant.STATUS_NORMAL);
		baseUser.setUserChannel(paramDto.getChannel());
		baseUser.setUserSource(paramDto.getAppType());
		String userFrom = WxUserUtil.getUserFrom(paramDto.getAppType());
		baseUser.setUserFrom(userFrom);
		baseUserService.save(baseUser);
		log.info("-->保存用户openId:{}基本信息, userId:{}, channel:{}, userSource:{}, userFrom:{}",
				openId, userId, paramDto.getChannel(), paramDto.getAppType(), userFrom);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public WebUser officialAccountLogin(WxUserParamDto paramDto, BaseUser baseUser) {
		
		// 要返回的对象
		WebUser webUser = new WebUser();
		
		// 要保存的用户登录信息
		UserLogin userLogin; 
		
		// APPID&SECRET配置
		WxConfig wxConfig;
		
		String openId;
		String unionId;
		if(paramDto != null && baseUser == null) {
			log.info("-->微信内H5登录");
			wxConfig = wxConfigService.queryByAppId(paramDto.getAppid());
			JSONObject userInfoJsonObject = getUserInfoJsonObject(paramDto, wxConfig);
			
			// 根据openId判断该用户在数据库中是否存在，是则更新，否则保存
			openId = userInfoJsonObject.getString("openid");
			unionId = userInfoJsonObject.getString("unionid");
			log.info("-->微信内H5登录-->从微信获取到的openId:{}, unionId:{}", openId, unionId);
			if(StringUtils.isBlank(openId) || StringUtils.isBlank(unionId)) {
				log.error("-->微信内H5登录-->微信服务异常，获取openid失败");
				throw new CustomException("微信服务异常，获取openid失败");
			}
			
			baseUser = new BaseUser();
			baseUser.setOpenId(openId);
			baseUser.setUnionId(unionId);
			
			baseUser.setUserFullName(userInfoJsonObject.getString("nickname"));
			baseUser.setUserSex(userInfoJsonObject.getInteger("sex"));
			baseUser.setImagesUrl(userInfoJsonObject.getString("headimgurl"));
			baseUser.setUserChannel(paramDto.getChannel());
			baseUser.setUserSource(paramDto.getAppType());
			baseUser.setUserFrom(WxUserUtil.getUserFrom(paramDto.getAppType()));
			baseUser.setUserCountry(userInfoJsonObject.getString("country"));
			baseUser.setUserProvince(userInfoJsonObject.getString("province"));
			baseUser.setUserCity(userInfoJsonObject.getString("city"));
			baseUser.setUserArea(userInfoJsonObject.getString("area"));
			baseUser.setSubscribe(userInfoJsonObject.getInteger("subscribe"));
			baseUser.setSubscribeTime(new Date());
			baseUser.setTagidList(userInfoJsonObject.getString("tagid_list"));
			baseUser.setTagName(userInfoJsonObject.getString("tag_name"));
			baseUser.setSubscribeScene(userInfoJsonObject.getString("subscribe_scene"));
			baseUser.setQrScene(userInfoJsonObject.getString("qr_scene"));
			baseUser.setQrSceneStr(userInfoJsonObject.getString("qr_scene_str"));
			baseUser.setGroupid(userInfoJsonObject.getInteger("groupid"));
			
		} else {
			openId = baseUser.getOpenId();
			unionId = baseUser.getUnionId();
			log.info("-->公众号保存用户:{}", openId);
			wxConfig = wxConfigService.queryByAppId(baseUser.getAppId());
		}
		
		long userId; //返回给前端的userId
		userLogin = userLoginService.getUserLoginInfoByUserName(openId);
		if(userLogin == null) {

			/** 此处说明openId在登录表中不存在，则要保存openId登录信息，以及用户基础信息 **/
			
			/** 但要先取出unionId对应的userId才可保存 **/
			
			/** 检查项 ：判断unionId在登录表中是否存在， 正常情况下，公众号登录时openId和unionId是一起保存的 **/
			UserLogin unionIdLoginInfo = userLoginService.getUserLoginInfoByUserName(unionId);
			if(unionIdLoginInfo == null) {
				
				/** 保存unionId登录信息 **/
				
				/** 此处说明openId和unionId在登录表都不存在，表示这个用户从来没有在任何端上登录过，是一个全新的用户，需要保存openid和unionid两条登录信息 **/
				
				log.info("-->用户{}是个新用户，之前也未在其它端上进行过访问", openId);
				userId = snowflakeIdWorker.nextId();
				userLogin = new UserLogin(unionId, LoginType.unionid.toString(), userId, "unionid");
				userLoginService.saveLoginInfo(userLogin);
			} else {
				/** 此处说明openId在登录表中不存在，但unionId在登录表存在，表示此用户之前在其它端上登录过，但当前端是第一次登录 **/
				userId = unionIdLoginInfo.getUserId(); // 使用unionid对应的userId
			}
			
			/** 保存openId登录信息 **/
			userLogin = new UserLogin(openId, LoginType.openid.toString(), userId, wxConfig.getAppId());
			userLoginService.saveLoginInfo(userLogin);
			
			/** 保存用户基础信息 **/
			
			/** 检查项： 判断openId在t_base_user是否存在，避免重复保存 **/
			BaseUser bu = baseUserService.getByOpenId(openId);
			if(bu == null) {
				// openId在t_base_user不存在，则可进行保存，但要确定baseUserId
				long baseUserId;
				if(unionIdLoginInfo == null) {
					// 如果是一个全新的用户，则使用上面生成的userId
					baseUserId = userId;
					log.info("-->{}使用登录表中的userId作为t_base_user中的userId:{}", openId, baseUserId);
					baseUser.setUserStatus(UserConstant.STATUS_NORMAL);
				} else {
					// 否则生成一个新的userId
					baseUserId = snowflakeIdWorker.nextId();
					log.info("-->保存基础信息时对{}生成的userId为{}", openId, baseUserId);
					baseUser.setUserStatus(UserConstant.STATUS_DEL);
				}
				
				baseUser.setUserId(baseUserId);
				baseUserService.save(baseUser);
			} else {
				/** 注意，此处要根据openId修改，但根据openId无法确定用户在哪张表，所以需要取出openId对应的userId **/
				baseUser.setUserId(bu.getUserId()); // 根据主键修改
				this.baseUserService.update(baseUser);
			}
			
			webUser.setUserId(userId);
			webUser.setOpenId(openId);
			webUser.setUserName(baseUser.getUserFullName());
			webUser.setAvatarUrl(baseUser.getImagesUrl());
			
		} else {
			
			/** 此处说明openId在登录表中存在，那么则unionId肯定也是一起存在的，因为公众号登录时，openId和unionId是一起保存的，不用考虑用户合并，下面更新t_base_user信息 **/
			
			log.info("-->openId:{}在登录表中存在，开始更新用户信息");
			
			userId = userLogin.getUserId();
			
			checkUnionIdLoginInfo(unionId, openId, userId);
			checkAppId(openId, userLogin, wxConfig);
			
			/** 下面更新该openId在t_base_user中的信息，正常来说，openId在登录表中存在，那么在t_base_user中也应该存在 **/
			
			BaseUser bu = baseUserService.getByOpenId(openId);
			if(bu == null) {
				log.error("-->用户{}的openId:{}在t_base_user表中数据异常，未找到记录", userId, openId);
				throw new CustomException("用户数据异常，请联系客服");
			} 
			
			/** 注意，此处要根据openId修改，但根据openId无法确定用户在哪张表，所以需要取出openId对应的userId **/
			baseUser.setUserId(bu.getUserId()); 
			this.baseUserService.update(baseUser);
			
			if(!bu.getUserId().equals(userId)) {
				bu = baseUserService.getById(userId);
				if(bu != null) {
					bu.setUserFullName(baseUser.getUserFullName());
					bu.setImagesUrl(baseUser.getImagesUrl());
					bu.setUserCountry(baseUser.getUserCountry());
					bu.setUserProvince(baseUser.getUserProvince());
					bu.setUserCity(baseUser.getUserCity());
					bu.setUserArea(baseUser.getUserArea());
					this.baseUserService.update(bu);
				}
			}
			
			webUser.setUserId(userLogin.getUserId());
			webUser.setOpenId(openId);
			webUser.setUserName(baseUser.getUserFullName());
			webUser.setAvatarUrl(baseUser.getImagesUrl());
			log.info("-->用户{}之前已经登录过，此次直接返回用户信息", openId);
		}
		log.info("-->用户{}通过公众号登录返回用户信息{}", openId, JSONObject.toJSONString(webUser));
		webUser.setGrant(true);
		return webUser;
	}
	
	/**
	 * 检查项：为了防止微服务版本上线时数据抽取有问题，导致unionId在登录表数据缺失，此处做下数据校验，没有则补上
	 * @param unionId
	 * @param openId
	 * @param userId
	 */
	@Override
	public void checkUnionIdLoginInfo(String unionId, String openId, long userId) {
		UserLogin unionIdLoginInfo = userLoginService.getUserLoginInfoByUserName(unionId);
		if(unionIdLoginInfo == null) {
			log.error("-->检查发现用户{}对应的unionId:{}在登录表中不存在，此处进行保存", openId);
			UserLogin userLoginForInsert = new UserLogin(unionId, LoginType.unionid.toString(), userId, "unionid");
			userLoginService.saveLoginInfo(userLoginForInsert);
		}
	}
	
	/**
	 * 检查项：如果openId在登录表中对应的appId为空，或者不正确，则此时要将appId补上或更正，主要针对历史数据
	 */
	@Override
	public void checkAppId(String openId, UserLogin userLogin, WxConfig wxConfig) {
		if(StringUtils.isBlank(userLogin.getAppId()) || !userLogin.getAppId().equalsIgnoreCase(wxConfig.getAppId())) {
			log.info("用户{}的appId为{}，非法，开始填补数据，appId:{}", openId, userLogin.getAppId(), wxConfig.getAppId());
			UserLogin userLoginForUpdate = new UserLogin();
			userLoginForUpdate.setLoginId(userLogin.getLoginId());
			userLoginForUpdate.setAppId(wxConfig.getAppId());
			BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userLogin.getUserId());
			userLoginService.updateById(userLoginForUpdate);
			log.info("-->给用户{}填补appId:{}完毕", openId, wxConfig.getAppId());
			this.redisTemplate.delete(UserCacheUtil.getUserLoginCacheKey(openId));
			this.redisTemplate.delete(UserCacheUtil.getUserAllLoginCacheKey(userLogin.getUserId()));
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public WebUser appLogin(String code) {
		
		log.info("-->APP登录");
		
		// 要返回的对象
		WebUser webUser = new WebUser(); 
		
		// 要保存的用户登录信息
		UserLogin userLogin;
				
		// 要保存的用户基本信息
		BaseUser baseUser; 
				
		String appType = "weixin_app";
		String userFrom = "APP";
		String type = "app";
		List<WxConfig> wxConfigList = wxConfigService.queryByType(type);
		WxConfig wxConfig = wxConfigList.get(0);
		String appId = wxConfig.getAppId();
		JSONObject paramJsonObject = new JSONObject();
		paramJsonObject.put(KEY_APPID, appId);
		paramJsonObject.put(KEY_SECRET, wxConfig.getSecret());
		paramJsonObject.put(KEY_GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
		paramJsonObject.put(KEY_CODE, code);
		String jsonResultString = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_ACCESSTOKEN_USER, paramJsonObject);
		JSONObject userInfoJsonObject = JSONObject.parseObject(jsonResultString);
		String openId = userInfoJsonObject.getString("openid");
		String unionId = userInfoJsonObject.getString("unionid");
		log.info("-->APP登录-->从微信获取到的openId:{}, unionId:{}", openId, unionId);
		if(StringUtils.isBlank(openId) || StringUtils.isBlank(unionId)) {
			log.error("-->APP登录-->微信服务异常，获取openid失败");
			throw new CustomException("微信服务异常，获取openid失败");
		}
		String accessToken = userInfoJsonObject.getString(KEY_ACCESS_TOKEN);
		paramJsonObject = new JSONObject();
		paramJsonObject.put(KEY_ACCESS_TOKEN, accessToken);
		paramJsonObject.put("openid", openId);
		jsonResultString = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_USERINFO_NO_FOLLOW, paramJsonObject);
		userInfoJsonObject = JSONObject.parseObject(jsonResultString);
		
		baseUser = new BaseUser();
		baseUser.setOpenId(userInfoJsonObject.getString("openid"));
		baseUser.setUserFullName(userInfoJsonObject.getString("nickname"));
		baseUser.setUserStatus(UserConstant.STATUS_NORMAL);
		baseUser.setUserSex(userInfoJsonObject.getInteger("sex"));
		baseUser.setImagesUrl(userInfoJsonObject.getString("headimgurl"));
		baseUser.setUserSource(appType);
		baseUser.setUserFrom(userFrom);
		baseUser.setUserCountry(userInfoJsonObject.getString("country"));
		baseUser.setUserProvince(userInfoJsonObject.getString("province"));
		baseUser.setUserCity(userInfoJsonObject.getString("city"));
		baseUser.setUnionId(unionId);
		
		// 根据openId判断该用户在数据库中是否存在，是则更新，否则保存
		userLogin = userLoginService.getUserLoginInfoByUserName(openId);
		
		long userId;
		if(userLogin == null) {
			/** 此处说明openId在登录表中不存在，则要保存openId登录信息，以及用户基础信息 **/
			
			/** 但要先取出unionId对应的userId才可保存 **/
			
			/** 检查项 ：判断unionId在登录表中是否存在， 正常情况下，公众号登录时openId和unionId是一起保存的 **/
			UserLogin unionIdLoginInfo = userLoginService.getUserLoginInfoByUserName(unionId);
			if(unionIdLoginInfo == null) {
				
				/** 保存unionId登录信息 **/
				
				/** 此处说明openId和unionId在登录表都不存在，表示这个用户从来没有在任何端上登录过，是一个全新的用户，需要保存openid和unionid两条登录信息 **/
				
				log.info("-->APP登录-->用户{}是个新用户，之前也未在其它端上进行过访问", openId);
				userId = snowflakeIdWorker.nextId();
				userLogin = new UserLogin(unionId, LoginType.unionid.toString(), userId, "unionid");
				userLoginService.saveLoginInfo(userLogin);
			} else {
				/** 此处说明openId在登录表中不存在，但unionId在登录表存在，表示此用户之前在其它端上登录过，但当前端是第一次登录 **/
				userId = unionIdLoginInfo.getUserId();
			}
			
			/** 保存openId登录信息 **/
			userLogin = new UserLogin(openId, LoginType.openid.toString(), userId, appId);
			userLoginService.saveLoginInfo(userLogin);;
			
			/** 保存用户基础信息 **/
			
			/** 检查项： 判断openId在t_base_user是否存在，避免重复保存 **/
			BaseUser bu = baseUserService.getByOpenId(openId);
			if(bu == null) {
				// openId在t_base_user不存在，则可进行保存，但要确定baseUserId
				long baseUserId;
				if(unionIdLoginInfo == null) {
					// 如果是一个全新的用户，则使用上面生成的userId
					baseUserId = userId;
					log.info("-->{}使用登录表中的userId作为t_base_user中的userId:{}", openId, baseUserId);
					baseUser.setUserStatus(UserConstant.STATUS_NORMAL);
				} else {
					// 否则生成一个新的userId
					baseUserId = snowflakeIdWorker.nextId();
					log.info("-->保存基础信息时对{}生成的userId为{}", openId, baseUserId);
					baseUser.setUserStatus(UserConstant.STATUS_DEL);
				}
				
				baseUser.setUserId(baseUserId);
				baseUserService.save(baseUser);
			} else {
				/** 注意，此处要根据openId修改，但根据openId无法确定用户在哪张表，所以需要取出openId对应的userId **/
				baseUser.setUserId(bu.getUserId()); // 根据主键修改
				this.baseUserService.update(baseUser);
			}
			
		} else {
			/** 此处说明openId在登录表中存在，那么则unionId肯定也是一起存在的，因为公众号登录时，openId和unionId是一起保存的，不用考虑用户合并，下面更新t_base_user信息 **/
			log.info("-->APP登录-->openId:{}在登录表中存在，开始更新用户信息");
			
			userId = userLogin.getUserId();
			
			checkUnionIdLoginInfo(unionId, openId, userId);
			checkAppId(openId, userLogin, wxConfig);
			
			/** 下面更新该openId在t_base_user中的信息，正常来说，openId在登录表中存在，那么在t_base_user中也应该存在 **/
			
			BaseUser bu = baseUserService.getByOpenId(openId);
			if(bu == null) {
				log.error("-->用户{}的openId:{}在t_base_user表中数据异常，未找到记录", userId, openId);
				throw new CustomException("用户数据异常，请联系客服");
			} 
			
			/** 注意，此处要根据openId修改，但根据openId无法确定用户在哪张表，所以需要取出openId对应的userId **/
			baseUser.setUserId(bu.getUserId()); 
			this.baseUserService.update(baseUser);
			
			if(!bu.getUserId().equals(userId)) {
				bu = baseUserService.getById(userId);
				if(bu != null) {
					bu.setUserFullName(baseUser.getUserFullName());
					bu.setImagesUrl(baseUser.getImagesUrl());
					bu.setUserCountry(baseUser.getUserCountry());
					bu.setUserProvince(baseUser.getUserProvince());
					bu.setUserCity(baseUser.getUserCity());
					bu.setUserArea(baseUser.getUserArea());
					this.baseUserService.update(bu);
				}
			}
			log.info("-->用户{}之前已经登录过，此次直接返回用户信息", openId);
		}
		webUser.setUserId(userId);
		webUser.setOpenId(openId); 
		webUser.setUserName(userInfoJsonObject.getString("nickname")); 
		webUser.setAvatarUrl(userInfoJsonObject.getString("headimgurl"));
		webUser.setGrant(true);
		webUser.setUnionId(unionId);
		log.info("-->APP登录-->用户{}通过APP登录返回用户信息{}", webUser.getOpenId(), JSONObject.toJSONString(webUser));
		return webUser;
	}

	private Jscode2Session getJscode2Session(WxUserParamDto paramDto, WxConfig wxConfig) {
		JSONObject jsonParamObject = new JSONObject();
		jsonParamObject.put(KEY_APPID, wxConfig.getAppId());
		jsonParamObject.put(KEY_SECRET, wxConfig.getSecret());
		jsonParamObject.put(KEY_JS_CODE, paramDto.getCode());
		jsonParamObject.put(KEY_GRANT_TYPE, GRANT_TYPE_AUTHORIZATION_CODE);
    	Jscode2Session jscode2Session = WxUserUtil.invokeJscode2Session(jsonParamObject);
    	if(jscode2Session == null) {
			throw new CustomException("微信服务异常，获取授权信息失败"); 
		}
    	String cacheKey = getCacheKeyOfSessionKey(jscode2Session.getOpenid());
		redisTemplate.opsForValue().set(cacheKey, jscode2Session.getSession_key());
    	return jscode2Session;
	}
	
	/**
	 * 获取openId时通过code调用接口获取sessionKey<br/>
	 * 授权登录时直接从缓存中获取sessionKey
	 * @param paramDto
	 * @param wxConfig
	 * @return
	 */
	private String getSessionKey(WxUserParamDto paramDto, WxConfig wxConfig) {
		String sessionKey;
		String cacheKey = getCacheKeyOfSessionKey(paramDto.getOpenId()); // 此时前端会传来openId
	    if(StringUtils.isBlank(paramDto.getCode())) { 
	    	// 说明是授权登录，且说明前端已判定sessionKey未失效
	        sessionKey  = redisTemplate.opsForValue().get(cacheKey);
	    } else { 
	    	// 携带有code，说明是通过code获取openId，需要调用接口去获取sessionKey
	    	Jscode2Session jscode2Session = getJscode2Session(paramDto, wxConfig);
	        sessionKey = jscode2Session.getSession_key();
	    }
	    return sessionKey;
	}
	
	private String getCacheKeyOfSessionKey(String openId) {
		StringBuilder sb = new StringBuilder();
		sb.append(KEY_JSCODE2SESSION);
		sb.append(CommonConstant.Symbol.COLON);
		sb.append(openId);
		return sb.toString();
	}
	
	private JSONObject getUserInfoJsonObject(WxUserParamDto paramDto, WxConfig wxConfig) {
		String openId = getOpenId(paramDto.getCode(), wxConfig);
		if(StringUtils.isBlank(openId)) {
			throw new CustomException("获取openid失败");
		}
		String accessToken = reloadOfficialAccountToken(wxConfig);   
		log.info("重新加载公众号token， accessToken:{}, openId:{}", accessToken, openId);
		
		JSONObject paramJsonObject = new JSONObject();
		paramJsonObject.put(KEY_ACCESS_TOKEN, accessToken);
		paramJsonObject.put("openid", openId);
		paramJsonObject.put("lang", "zh_CN");
		String userInfoJsonData = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_USERINFO_FOLLOW, paramJsonObject);
		if(userInfoJsonData.contains("40001")) {
			String cacheKey = "access_token_" + wxConfig.getAppId();
			redisTemplate.delete(cacheKey);
			accessToken = reloadOfficialAccountToken(wxConfig);
			paramJsonObject.put(KEY_ACCESS_TOKEN, accessToken);
			userInfoJsonData = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_USERINFO_FOLLOW, paramJsonObject);
		}
		log.info("用户信息返回:{}", userInfoJsonData);
		JSONObject userInfoJsonObject = JSONObject.parseObject(userInfoJsonData);
		if(userInfoJsonObject.getInteger("subscribe") == 0) { // 用户未关注
			userInfoJsonObject = getUserInfoNoFollowWithOpenId(openId);
		}
		return userInfoJsonObject;
	}
	
	/**
	 * 获取未关注的用户信息
	 * @param openId
	 * @return
	 */
	private JSONObject getUserInfoNoFollowWithOpenId(String openId) {
        String accessTokenUser = redisTemplate.opsForValue().get("accessTokenUser");
        JSONObject paramJsonObject = new JSONObject();
        paramJsonObject.put(KEY_ACCESS_TOKEN, accessTokenUser);
        paramJsonObject.put("openid", openId);
        paramJsonObject.put("lang", "zh_CN");
        String userInfoJsonString = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_USERINFO_NO_FOLLOW, paramJsonObject);
        return JSONObject.parseObject(userInfoJsonString);
    }
	
	/**
	 * 通过授权码模式从微信获取openId
	 * @param code
	 * @param appid
	 * @return
	 */
	private String getOpenId(String code, WxConfig wxConfig) {
		JSONObject paramJsonObject = new JSONObject();
		paramJsonObject.put("appid", wxConfig.getAppId());
		paramJsonObject.put("secret", wxConfig.getSecret());
		paramJsonObject.put("grant_type", GRANT_TYPE_AUTHORIZATION_CODE);
		paramJsonObject.put("code", code);
        String accessTokenJson = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_ACCESSTOKEN_USER, paramJsonObject);
        JSONObject jsonObject = JSONObject.parseObject(accessTokenJson);
        String accessToken = jsonObject.getString(KEY_ACCESS_TOKEN);
        if (!StringUtils.isEmpty(accessToken)) {
            redisTemplate.opsForValue().set("accessTokenUser", accessToken, 7000, TimeUnit.SECONDS);
            return jsonObject.getString("openid");
        }
        return null;
	}
	
	/**
	 * 重新加载公众号token
	 * @return
	 */
	private String reloadOfficialAccountToken(WxConfig wxConfig) {
		JSONObject paramJsonObject = new JSONObject();
		paramJsonObject.put("appid", wxConfig.getAppId());
		paramJsonObject.put("secret", wxConfig.getSecret());
		paramJsonObject.put("grant_type", GRANT_TYPE_CLIENT_CREDENTIAL);
		String accessToken = null;
		String cacheKey = "access_token_" + wxConfig.getAppId();
		String cacheValue = redisTemplate.opsForValue().get(cacheKey);
		if(StringUtils.isNotBlank(cacheValue)) {
			accessToken = cacheValue;
		} else {
			String jsonResult = HttpURLConnectionUtil.doGet(WxUserUtil.URL_GET_ACCESSTOKEN, paramJsonObject);
			JSONObject jsonObject = JSONObject.parseObject(jsonResult);
			accessToken = jsonObject.getString(KEY_ACCESS_TOKEN);
			redisTemplate.opsForValue().set(cacheKey, accessToken, 7000, TimeUnit.SECONDS);
		}
		return accessToken;
	}
	
	/**
	 * 判断用户是否授过权
	 * @param nickname
	 * @param headimgurl
	 * @return
	 */
	private boolean isGranted(String nickname, String headimgurl) {
		return (StringUtils.isNotBlank(nickname) && StringUtils.isNotBlank(headimgurl));
	}
	
	/**
	 * 处理第三方渠道引流，如返利网
	 * @param paramDto
	 * @param userId
	 */
	private void handelChannelData(WxUserParamDto paramDto, long userId) {
		String advertiserId = paramDto.getAdvertiserId();
		if(StringUtils.isBlank(advertiserId)) {
			return;
		}
		log.info("-->处理第三方渠道引流，userId:{}", userId);
		String cacheKey = "THIRDID_" + paramDto.getThridId();
		long increment = redisTemplate.opsForValue().increment(cacheKey, 1);
		if (increment == 1) {
			redisTemplate.expire(cacheKey, 2, TimeUnit.SECONDS);
			
			// 获取渠道配置
			ChannelConfig channelConfig = null;
			
			cacheKey = "CHANNEL_CONFIG:" + advertiserId;
	        String cacheValue = redisTemplate.opsForValue().get(cacheKey);
	        if(StringUtils.isBlank(cacheValue)) {
	        	log.info("-->从数据库加载渠道配置：{}", advertiserId);
	        	Wrapper<ChannelConfig> wrapper = new EntityWrapper<ChannelConfig>();
	        	wrapper.eq("ADVERTISER_ID", paramDto.getAdvertiserId());
	        	channelConfig = channelConfigService.selectOne(wrapper);
	        	if(channelConfig != null) {
	        		redisTemplate.opsForValue().set(cacheKey, JSONObject.toJSONString(channelConfig));
	        	}
	        } else {
	        	channelConfig = JSONObject.parseObject(cacheValue, ChannelConfig.class);
	        }
	        if(channelConfig == null) {
	        	return;
	        }
	        Map<String, String> map = new HashMap<String, String>();
            map.put("advertiser_id", advertiserId);
            map.put("openid", paramDto.getThridId());
            map.put("is_success", "1");
            map.put("aduserid", String.valueOf(userId));
            map.put("error", "0");
            String params = WxUserUtil.generateSignature(map);
            params = params.substring(0, params.length() - 1);
            log.info("-->调用第三方渠道接口，渠道：{}，参数：{}", advertiserId, params);
            String sign = null;
            try {
                sign = URLEncoder.encode(params, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            sign = MD5Util.MD5Encode(sign + channelConfig.getType(), "utf-8");
            params = params + "&sign=" + sign + "&time=" + System.currentTimeMillis();
            HttpURLConnectionUtil.doGet(channelConfig.getUrl() + "?" + params , null);
		}
	}
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public WebUser publicSave(OfficialAccountUser user) {
		log.info("-->公众号直接登录，参数：{}", JSONObject.toJSONString(user));
		
		if(StringUtils.isBlank(user.getAppId()) 
				|| StringUtils.isBlank(user.getOpenId()) 
				|| StringUtils.isBlank(user.getUnionId())) {
			throw new CustomException("参数缺失");
		}
		
		// 封装用户的基础信息
		BaseUser baseUser = new BaseUser();
		baseUser.setOpenId(user.getOpenId());
		baseUser.setUnionId(user.getUnionId());
		baseUser.setUserFullName(user.getNickname());
		baseUser.setUserSex(user.getSex());
		baseUser.setImagesUrl(user.getHeadimgurl());
		baseUser.setUserChannel(user.getChannel());
		baseUser.setUserSource(user.getAppType());
		baseUser.setUserFrom(WxUserUtil.getUserFrom(user.getAppType()));
		baseUser.setUserCountry(user.getCountry());
		baseUser.setUserProvince(user.getProvince());
		baseUser.setUserCity(user.getCity());
		baseUser.setUserArea(user.getArea());
		baseUser.setSubscribe(user.getSubscribe());
		baseUser.setSubscribeTime(new Date());
		baseUser.setTagidList(user.getTagidList());
		baseUser.setTagName(user.getTagName());
		baseUser.setSubscribeScene(user.getSubscribeScene());
		baseUser.setQrScene(user.getQrScene());
		baseUser.setQrSceneStr(user.getQrSceneStr());
		baseUser.setGroupid(user.getGroupid());
		baseUser.setAppId(user.getAppId());
		return this.officialAccountLogin(null, baseUser);
	}

	@Override
	public boolean unfollow(String openId) {
		log.info("-->取消关注，参数：{}", openId);
		// 先根据openId找到对应的userId
		BaseUser bu = baseUserService.getByOpenId(openId);
		if(bu == null) {
			log.error("-->用户{}取消关注，但在t_base_user表中不存在");
			return false;
		}
		long userId = bu.getUserId();
		log.info("-->用户{}取消关注，userId:{}", userId);
		BaseUser baseUserForUpdate = new BaseUser();
		baseUserForUpdate.setUserId(userId);
		baseUserForUpdate.setSubscribe(0);
		return this.baseUserService.update(baseUserForUpdate);
	}

	@Override
	public Map<String, Object> getConfig(String appId, String url) {
		log.info("-->H5获取配置，参数:{},{}", appId, url);
		if(StringUtils.isBlank(appId)) {
			throw new CustomException("缺失appId");
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
        String noncestr = CommonUtil.getRandomUUID();
        resultMap.put("noncestr", noncestr);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        resultMap.put("timestamp", timestamp);
        resultMap.put("signature", WxUserUtil.getSignature(this.getTicket(appId), noncestr, timestamp, url));
        resultMap.put("appId", appId);
        return resultMap;
	}
	
	private String getTicket(String appId) {
		String cacheKey = "ticket:"+appId;
        String ticket = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isBlank(ticket)) {
            String accessToken = getH5AccessToken(appId);
            JSONObject jsonParam = new JSONObject();
            jsonParam.put(KEY_ACCESS_TOKEN, accessToken);
            jsonParam.put("type", "jsapi");
            String result = HttpURLConnectionUtil.doGet(URL_GET_TICKET, jsonParam);
            if ("40001".equals(JSONObject.parseObject(result).getString("errcode"))) {
                accessToken = reloadH5AccessToken(appId);
                jsonParam.put(KEY_ACCESS_TOKEN, accessToken);
                result = HttpURLConnectionUtil.doGet(URL_GET_TICKET, jsonParam);
            }
            ticket = JSONObject.parseObject(result).getString("ticket");
            if(StringUtils.isNotBlank(ticket)) {
            	redisTemplate.opsForValue().set(cacheKey, ticket, 7000, TimeUnit.SECONDS);
            }
        }
        return ticket;
    }
	
	private String reloadH5AccessToken(String appId){
		JSONObject jsonParam = new JSONObject();
		jsonParam.put("appid", appId);
		WxConfig wxConfig = wxConfigService.queryByAppId(appId);
		jsonParam.put("secret", wxConfig.getSecret());
		jsonParam.put("grant_type", "client_credential");
        String result = HttpURLConnectionUtil.doGet(URL_GET_ACCESSTOKEN, jsonParam);
        String accessToken = JSONObject.parseObject(result).getString(KEY_ACCESS_TOKEN);
        if(StringUtils.isNotBlank(accessToken)) {
        	redisTemplate.opsForValue().set(getH5AccessTokenKey(appId), accessToken, 7000, TimeUnit.SECONDS);
        }
        return accessToken;
    }
	
	private String getH5AccessToken(String appid) {
		String accessToken = redisTemplate.opsForValue().get(getH5AccessTokenKey(appid));
		if(StringUtils.isBlank(accessToken)) {
			accessToken = reloadH5AccessToken(appid);
		}
        return accessToken;
    }
	
	private String getH5AccessTokenKey(String appid) {
		return "accessToken:" + appid;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public void merge(long oldUserId, long newUserId) {
		
		// 第一步：先将用户自己的userId进行更新
		log.info("-->授权登录-->合并-->用户{}之前已在其它端进行过授权，对应的userId为{}，此次统一userId为{}", oldUserId, newUserId, newUserId);
		userLoginService.updateUserId(oldUserId, newUserId);
		
		// 第二步：记录合并记录
		MergeInfo mergeInfo = new MergeInfo();
		mergeInfo.setUserId(newUserId);
		mergeInfo.setMergeUserId(oldUserId);
		
		// 第三步：发送消息
		boolean isSuccess = false;
		UserMerge userMerge = new UserMerge(oldUserId, newUserId);
		try {
			kafkaTemplate.send(MQTopicConstant.TOPIC_USER_MERGE, String.valueOf(snowflakeIdWorker.nextId()), JSONObject.toJSONString(userMerge)).get();
			isSuccess = true;
			log.info("-->合并用户发送消息成功，oldUserId:{}, newUserId:{}", oldUserId, newUserId);
		} catch (InterruptedException e) {
			log.error("-->合并用户发送消息发生异常: ", e);
		} catch (ExecutionException e) {
			log.error("-->合并用户发送消息发生异常: ", e);
		}
		
		// 第四步：记录消息发送状态，以保证可排查合并异常的用户
		mergeInfo.setMqSendSuccess(isSuccess);
		mergeInfoService.insert(mergeInfo);
		log.info("-->记录用户{}的合并记录：{}", newUserId, JSONObject.toJSONString(mergeInfo));
	}

	@Override
	public UserDetails saveUserByMobile(String mobile, String appid, String channel) {
		if(StringUtils.isBlank(mobile)) {
			throw new CustomException("手机号不能为空");
		}
		if(!CommonUtil.checkMobile(mobile)) {
			throw new CustomException("手机号格式不正确");
		}
		if(StringUtils.isBlank(appid)) {
			throw new CustomException("appid缺失");
		}
		// 检查APPID&SECRET配置
		wxConfigService.queryByAppId(appid);
		long userId = snowflakeIdWorker.nextId();
		UserLogin userLogin = new UserLogin(mobile, LoginType.mobile.name(), userId, appid);
		boolean result = userLoginService.savePhoneLoginInfo(userLogin);
		log.info("-->保存手机号{}登录账号结果：{}", mobile, appid);
		if(result) {
			// 保存用户基础信息
			BaseUser user = new BaseUser();
			user.setUserId(userId);
			user.setUserFullName(mobile);
			user.setUserMobile(mobile);
			user.setUserStatus(UserConstant.STATUS_NORMAL);
			user.setUserChannel(channel);
			user.setUserSource(AppTypeConstant.APPTYPE_H5);
			user.setUserFrom(AppTypeConstant.APPTYPE_H5);
			baseUserService.save(user);
			return userDetailsService.loadUserByUsername(mobile);
		}
		return null;
	}
	
}
