package com.mmj.common.utils;

/**
 * 用户信息缓存工具类
 * @author shenfuding
 *
 */
public class UserCacheUtil {
	
	/**
	 * 登录表的缓存key：根据登录名存储
	 */
    private static final String USER_LOGIN_NAME_KEY_PREFIX = "user:login:username:";
    /**
	 * 登录表的缓存key：根据用户主userId存储所有的登录账号
	 */
    private static final String USER_ALLLOGIN_CACHEKEY_PREFIX = "user:allLogin:userid:";
    
    /**
	 * 基础表的缓存key：根据openId存储
	 */
    private static final String USER_BASE_OPENID_CACHE_KEY_PREFIX = "user:base:openid:";
    
    /**
	 * 登录表的缓存key：根据基础表的用户ID存储
	 */
    private static final String USER_BASE_USERID_PREFIX_PREFIX = "user:base:userid:";
    
    
    public static String getUserLoginCacheKey(String userName) {
		return USER_LOGIN_NAME_KEY_PREFIX + userName;
	}
    
    public static String getUserAllLoginCacheKey(long userId) {
		return USER_ALLLOGIN_CACHEKEY_PREFIX + userId;
	}
	
	public static String getBaseUserCacheKey(String openId) {
		return USER_BASE_OPENID_CACHE_KEY_PREFIX + openId;
	}
	
	public static String getBaseUserCacheKey(long userId) {
		return USER_BASE_USERID_PREFIX_PREFIX + userId;
	}

}
