package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.BasePermission;

/**
 * <p>
 * 用户权限表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BasePermissionService extends IService<BasePermission> {
	
	boolean deleteByPerId(Integer perId);

}
