package com.mmj.oauth.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.oauth.model.UserLogin;

/**
 * <p>
 * 登录关联表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface UserLoginMapper extends BaseMapper<UserLogin> {
	
	List<UserLogin> getUserLoginInfoByUserName(@Param("userName")String userName);
	
	int updateUserId(@Param("oldUserId")Long oldUserId, @Param("newUserId")Long newUserId);

}
