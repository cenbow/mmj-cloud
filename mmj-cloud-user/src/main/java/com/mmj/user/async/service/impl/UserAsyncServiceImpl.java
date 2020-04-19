package com.mmj.user.async.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.address.service.BaseUserAddrService;
import com.mmj.user.async.service.UserAsyncService;
import com.mmj.user.member.service.KingUserService;
import com.mmj.user.member.service.UserMemberService;
import com.mmj.user.recommend.service.UserRecommendService;
import com.mmj.user.recommend.service.UserShardService;
import com.mmj.user.userFocus.model.UserFocus;
import com.mmj.user.userFocus.service.UserFocusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@Configuration
@EnableAsync
public class UserAsyncServiceImpl implements UserAsyncService {
    @Autowired
    private BaseUserAddrService baseUserAddrService;
    @Autowired
    private UserRecommendService userRecommendService;
    @Autowired
    private UserShardService userShardService;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private UserFocusService userFocusService;

    @Autowired
    private KingUserService kingUserService;

    @Async
    @Override
    public void mergeUserMemberTables(UserMerge userMerge) {
        log.info("-->处理会员表数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        userMemberService.updateUserID(userMerge);
    }

    @Async
    @Override
    public void mergeBaseUserAddrTables(UserMerge userMerge) {
        log.info("-->处理收货地址管理表数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        baseUserAddrService.updateUserId(userMerge);
    }

    @Async
    @Override
    public void mergeUserRecommend(UserMerge userMerge) {
        log.info("-->处理商品推荐表数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        userRecommendService.updateUserId(userMerge);
    }

    @Async
    @Override
    public void mergeUserShard(UserMerge userMerge) {
        log.info("-->处理推荐返现表数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        userShardService.updateUserId(userMerge);
    }

    @Async
    @Override
    public void mergeUserFocus(UserMerge userMerge) {
        log.info("-->处理流量池关注信息表数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        userFocusService.updateUserID(userMerge);
    }

    @Async
    @Override
    public void mergeMMKing(UserMerge userMerge) {
        log.info("-->处理用户买买金数据合并，参数：{}", JSONObject.toJSONString(userMerge));
        kingUserService.updateUserId(userMerge);
    }
}
