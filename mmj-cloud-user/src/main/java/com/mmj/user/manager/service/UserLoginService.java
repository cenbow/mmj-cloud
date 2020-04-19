package com.mmj.user.manager.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.UserLogin;

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

}
