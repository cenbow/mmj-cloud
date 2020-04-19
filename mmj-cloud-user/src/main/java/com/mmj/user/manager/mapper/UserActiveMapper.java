package com.mmj.user.manager.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.user.manager.model.UserActive;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户活动参与表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Repository
public interface UserActiveMapper extends BaseMapper<UserActive> {

    List<UserActive> queryJoinUserList(Map<String,Object> map);

    UserActive queryWinner(UserActive userActive);

    List<UserActive> getActiveByCode(UserActive userActive);
}
