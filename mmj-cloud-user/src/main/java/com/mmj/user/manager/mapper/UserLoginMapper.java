package com.mmj.user.manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.manager.model.UserLogin;

/**
 * <p>
 * 登陆关联表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-26
 */
public interface UserLoginMapper extends BaseMapper<UserLogin> {
	
	List<UserLogin> getUserLoginInfoByUserName(@Param("userName")String userName);

}
