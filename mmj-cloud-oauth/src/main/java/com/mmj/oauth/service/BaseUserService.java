package com.mmj.oauth.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.BaseUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseUserService extends IService<com.mmj.common.model.BaseUser> {

	boolean save(BaseUser baseUser);
	
	boolean update(BaseUser baseUser);
	
	BaseUser getById(Long userId);
	
	BaseUser getByOpenId(String openId);
	
}
