package com.mmj.user.member.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.member.model.UserKingLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 买买金日志表 Mapper 接口
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Repository
public interface UserKingLogMapper extends BaseMapper<UserKingLog> {

    Double getSumKingNum(Long userId);

    int getLogCount(@Param("userId") Long userId,
                 @Param("shareType") String shareType,
                 @Param("today") String today);

    int getCountByTypeAndTime(@Param("userId") Long userId,
                          @Param("shareType") String shareType,
                          @Param("today") String today);

    int updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
