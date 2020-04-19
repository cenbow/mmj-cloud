package com.mmj.user.manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.manager.model.BaseRolePermission;
import com.mmj.user.manager.vo.RolePermissionDetail;

/**
 * <p>
 * 角色权限映射表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseRolePermissionMapper extends BaseMapper<BaseRolePermission> {
	
	List<RolePermissionDetail> queryByRoleId(@Param("roleId")Integer roleId);

}
