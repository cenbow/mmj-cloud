package com.mmj.user.manager.service.impl;

import java.util.HashMap;
import java.util.Map;
import com.mmj.user.manager.mapper.BasePermissionMapper;
import com.mmj.user.manager.model.BasePermission;
import com.mmj.user.manager.service.BasePermissionService;
import com.mmj.user.manager.service.BaseRolePermissionService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户权限表 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Service
public class BasePermissionServiceImpl extends ServiceImpl<BasePermissionMapper, BasePermission> implements BasePermissionService {
	
	@Autowired
	private BaseRolePermissionService baseRolePermissionService;

	@Override
	public boolean deleteByPerId(Integer perId) {
		boolean flag = this.deleteById(perId);
		if(flag) {
			// 删除该权限和角色的关联关系
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("PER_ID", perId);
			baseRolePermissionService.deleteByMap(map);
		}
		return flag;
	}

}
