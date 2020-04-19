package com.mmj.user.manager.service.impl;

import java.util.HashMap;
import java.util.Map;
import com.mmj.user.manager.mapper.BaseRoleMapper;
import com.mmj.user.manager.model.BaseRole;
import com.mmj.user.manager.service.BaseRolePermissionService;
import com.mmj.user.manager.service.BaseRoleResourceService;
import com.mmj.user.manager.service.BaseRoleService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户角色表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Service
public class BaseRoleServiceImpl extends ServiceImpl<BaseRoleMapper, BaseRole> implements BaseRoleService {
	
	@Autowired
	private BaseRolePermissionService baseRolePermissionService;
	
	@Autowired
	private BaseRoleResourceService baseRoleResourceService;
	

	@Override
	public boolean deleteByRoleId(Integer roleId) {
		boolean flag = this.deleteById(roleId);
        if(flag) {
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("ROLE_ID", roleId);
        	// 删除该角色和权限的关联关系
        	baseRolePermissionService.deleteByMap(map);
        	// 删除该角色和资源的关联关系
        	baseRoleResourceService.deleteByMap(map);
        }
		return flag;
	}

}
