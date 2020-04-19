package com.mmj.oauth.service;

import java.util.List;

import com.mmj.oauth.model.UserLogin;
import com.baomidou.mybatisplus.service.IService;

/**
 * <p>
 * 登陆关联表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface UserLoginService extends IService<UserLogin> {
	
	/**
	 * 根据用户主ID查询所有的登录账号，包括openid、unionid、phone
	 * @param userId
	 * @return
	 */
	List<UserLogin> getUserAllLoginInfo(long userId);
	
	/**
	 * 根据登录名查询登录信息<br/>
	 * 条件包括openid、unionid、phone
	 * @param userName
	 * @return
	 */
	UserLogin getUserLoginInfoByUserName(String userName);
	
	/**
	 * 根据userId查询绑定的手机号，一个用户只有一个绑定的手机号
	 * @param userId
	 * @return
	 */
	UserLogin getUserPhoneLoginInfoByUserId(long userId);
	
	/**
	 * 获取用户绑定的手机号
	 * @param userId
	 * @return
	 */
	String getUserPhone(long userId);
	
	/**
	 * 保存手机号登录信息
	 * @param userLogin
	 */
	boolean savePhoneLoginInfo(UserLogin userLogin);
	
	/**
	 * 保存登录信息
	 * @param userLogin
	 */
	void saveLoginInfo(UserLogin userLogin);
	
	/**
	 * 合并用户时更新userId
	 * @param oldUserId
	 * @param newUserId
	 * @return
	 */
	void updateUserId(Long oldUserId, Long newUserId);
	
	/**
	 * 根据userId删除所有的登录账号
	 * @param userId
	 */
	void deleteUserAllLoginInfo(long userId);
	
}
