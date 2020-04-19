package com.mmj.user.manager.service.impl;

import java.util.List;
import com.mmj.user.manager.mapper.BaseRoleResourceMapper;
import com.mmj.user.manager.model.BaseRoleResource;
import com.mmj.user.manager.service.BaseRoleResourceService;
import com.mmj.user.manager.vo.RoleResourceDetail;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色资源映射表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Service
public class BaseRoleResourceServiceImpl extends ServiceImpl<BaseRoleResourceMapper, BaseRoleResource> implements BaseRoleResourceService {
	
	@Autowired
	private BaseRoleResourceMapper mapper;

	@Override
	public List<RoleResourceDetail> queryByRoleId(Integer roleId) {
		return mapper.queryByRoleId(roleId);
	}

}
