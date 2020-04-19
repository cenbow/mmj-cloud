package com.mmj.user.member.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.member.model.KingUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 用户买买金表 Mapper 接口
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Repository
public interface KingUserMapper extends BaseMapper<KingUser> {

    int updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
