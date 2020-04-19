package com.mmj.user.manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.manager.model.BaseRoleResource;
import com.mmj.user.manager.vo.RoleResourceDetail;

/**
 * <p>
 * 角色资源映射表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseRoleResourceMapper extends BaseMapper<BaseRoleResource> {
	
	List<RoleResourceDetail> queryByRoleId(@Param("roleId")Integer roleId);

}
