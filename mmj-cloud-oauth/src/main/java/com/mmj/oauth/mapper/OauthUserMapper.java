package com.mmj.oauth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface OauthUserMapper {
    
    List<Integer> findRoleIdByUserId(@Param("userId")Long userId);
    
    List<String> findPermissionByRoleId(@Param("roleIds")String roleIds);
    
}
