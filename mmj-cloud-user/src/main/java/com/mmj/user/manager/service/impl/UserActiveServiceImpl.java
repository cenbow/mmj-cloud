package com.mmj.user.manager.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.manager.mapper.UserActiveMapper;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.model.UserActive;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.manager.service.UserActiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户活动参与表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-06
 */
@Service
@Slf4j
public class UserActiveServiceImpl extends ServiceImpl<UserActiveMapper, UserActive> implements UserActiveService {

    @Autowired
    private BaseUserService baseUserService;

    @Autowired
    private UserActiveMapper userActiveMapper;


    @Override
    public List<UserActive> queryJoinUserList(UserActive userActive) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("businessId", userActive.getBusinessId());
        params.put("activeType", userActive.getActiveType());
        return userActiveMapper.queryJoinUserList(params);
    }

    @Override
    public UserActive activeQueryWinner(UserActive userActive) {
        UserActive ua = userActiveMapper.queryWinner(userActive);
        if (null == ua)
            return null;
        return ua;
    }

    @Override
    public Map<String, Object> queryWinner(UserActive userActive) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);

        userActive.setActiveType(1);
        UserActive ua = userActiveMapper.queryWinner(userActive);
        if (null == ua) {
            map.put("code", 0);
            map.put("msg", "无人中奖");
            return map;
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, ua.getUserId());
        BaseUser baseUser = baseUserService.getById(ua.getUserId());
        if (null == baseUser) {
            map.put("code", 0);
            map.put("msg", "无人中奖");
            return map;
        }
        map.put("code", 1);
        map.put("name", baseUser.getUserFullName());
        return map;
    }

    @Override
    public List<UserActive> getActiveByCode(UserActive userActive) {
        return userActiveMapper.getActiveByCode(userActive);
    }
}
