package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.BaseRole;

/**
 * <p>
 * 用户角色表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseRoleService extends IService<BaseRole> {
	
	boolean deleteByRoleId(Integer roleId);

}
