package com.mmj.user.manager.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.BaseRolePermission;
import com.mmj.user.manager.vo.RolePermissionDetail;

/**
 * <p>
 * 角色权限映射表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseRolePermissionService extends IService<BaseRolePermission> {
	
	List<RolePermissionDetail> queryByRoleId(Integer roleId);
	
}
