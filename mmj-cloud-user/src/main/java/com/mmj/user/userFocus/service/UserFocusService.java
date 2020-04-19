package com.mmj.user.userFocus.service;

import com.baomidou.mybatisplus.service.IService;
import com.mmj.common.model.UserMerge;
import com.mmj.user.userFocus.model.UserFocus;

import java.util.List;

/**
 * <p>
 * 用户关注公众号记录 服务类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-16
 */
public interface UserFocusService extends IService<UserFocus> {

    void subscribe(String channel, String openId);

    void unsubscribe(String openId);

    void subscribe(String openId);

    void sync(Integer module, Integer type);

    void updateUserID(UserMerge userMerge);
}
