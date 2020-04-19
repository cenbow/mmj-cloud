package com.mmj.user.recommend.mapper;

import com.mmj.user.recommend.model.UserShard;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 分享关联表 Mapper 接口
 * </p>
 *
 * @author dashu
 * @since 2019-06-21
 */

@Repository
public interface UserShardMapper extends BaseMapper<UserShard> {

    List<UserShard> selectUserShard();

    List<UserShard> selectByShardTo(UserShard userShard);

    void updateUserId(@Param("oldUserId") long oldUserId, @Param("newUserId") long newUserId);
}
