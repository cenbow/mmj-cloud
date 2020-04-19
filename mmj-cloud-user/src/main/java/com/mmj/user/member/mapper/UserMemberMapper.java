package com.mmj.user.member.mapper;

import org.apache.ibatis.annotations.Param;

import com.mmj.user.member.model.UserMember;
import com.baomidou.mybatisplus.mapper.BaseMapper;

/**
 * <p>
 * 会员表 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
public interface UserMemberMapper extends BaseMapper<UserMember> {
	
	int getMaxMemberId();

	int getTotalCount();

	void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
