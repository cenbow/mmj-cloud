package com.mmj.oauth.mapper;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.common.model.BaseUser;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
public interface BaseUserMapper extends BaseMapper<BaseUser> {
	
	BaseUser getByOpenId(@Param("openId") String openId);

}
