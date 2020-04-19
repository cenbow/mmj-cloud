package com.mmj.user.manager.service.impl;

import java.util.List;
import com.mmj.user.manager.mapper.BaseRolePermissionMapper;
import com.mmj.user.manager.model.BaseRolePermission;
import com.mmj.user.manager.service.BaseRolePermissionService;
import com.mmj.user.manager.vo.RolePermissionDetail;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色权限映射表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Service
public class BaseRolePermissionServiceImpl extends ServiceImpl<BaseRolePermissionMapper, BaseRolePermission> implements BaseRolePermissionService {
	
	@Autowired
	private BaseRolePermissionMapper mapper;

	@Override
	public List<RolePermissionDetail> queryByRoleId(Integer roleId) {
		return mapper.queryByRoleId(roleId);
	}

}
