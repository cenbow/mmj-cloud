package com.mmj.user.manager.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.user.manager.model.UserActive;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户活动参与表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
public interface UserActiveService extends IService<UserActive> {

    List<UserActive> queryJoinUserList(UserActive userActive);

    UserActive activeQueryWinner(UserActive userActive);

    Map<String, Object> queryWinner(UserActive userActive);

    List<UserActive> getActiveByCode(UserActive userActive);
}
