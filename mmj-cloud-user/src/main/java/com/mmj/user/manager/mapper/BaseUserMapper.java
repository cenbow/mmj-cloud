package com.mmj.user.manager.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.user.manager.model.BaseUser;

import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@Repository
public interface BaseUserMapper extends BaseMapper<BaseUser> {
	
	List<BaseUser> getBaseUserByUserFullNameLike(@Param("userFullName")String userFullName, @Param("searchType")String searchType);
	
	List<ChannelUserVO> getChannelUsers(ChannelUserParam param);
	
}
