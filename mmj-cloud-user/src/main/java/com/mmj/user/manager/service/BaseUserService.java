package com.mmj.user.manager.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.user.manager.dto.BaseUserDto;
import com.mmj.user.manager.dto.SearchUserParamDto;
import com.mmj.user.manager.model.BaseUser;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-05-06
 */
public interface BaseUserService extends IService<BaseUser> {
	
	Set<BaseUserDto> search(SearchUserParamDto dto);
	
	BaseUserDto queryUserInfoByUserId(Long userId, String appId);
	
	BaseUserDto queryUserInfoByPhone(String phone);
	
	BaseUser getById(Long userId);
	
	List<ChannelUserVO> getChannelUsers(ChannelUserParam param);

}
