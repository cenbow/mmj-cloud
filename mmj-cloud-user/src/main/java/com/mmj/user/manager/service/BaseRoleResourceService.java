package com.mmj.user.manager.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.BaseRoleResource;
import com.mmj.user.manager.vo.RoleResourceDetail;

/**
 * <p>
 * 角色资源映射表 服务类
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseRoleResourceService extends IService<BaseRoleResource> {
	
	List<RoleResourceDetail> queryByRoleId(Integer roleId);

}
