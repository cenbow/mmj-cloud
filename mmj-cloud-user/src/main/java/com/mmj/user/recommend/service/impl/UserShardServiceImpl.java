package com.mmj.user.recommend.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.MemberConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.*;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.*;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.common.feigin.WxMessageFeignClient;
import com.mmj.user.common.feigin.WxpayTransfersFeignClient;
import com.mmj.user.manager.dto.BaseUserDto;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.model.UserLogin;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.manager.service.UserLoginService;
import com.mmj.user.recommend.constants.UserShardConstants;
import com.mmj.user.recommend.mapper.RedPackageUserMapper;
import com.mmj.user.recommend.mapper.UserShardMapper;
import com.mmj.user.recommend.model.RedPackageUser;
import com.mmj.user.recommend.model.UserShard;
import com.mmj.user.recommend.model.UserShardEx;
import com.mmj.user.recommend.service.RedPackageUserService;
import com.mmj.user.recommend.service.UserShardService;
import com.xiaoleilu.hutool.collection.CollectionUtil;
import com.xiaoleilu.hutool.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 分享关联表 服务实现类
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Service
@Slf4j
public class UserShardServiceImpl extends ServiceImpl<UserShardMapper, UserShard> implements UserShardService {
    @Autowired
    private UserShardMapper userShardMapper;
    @Autowired
    private BaseUserService baseUserService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RedPackageUserMapper redPackageUserMapper;
    @Autowired
    private WxpayTransfersFeignClient wxpayTransfersFeignClient;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;
    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private RedPackageUserService redPackageUserService;


    @Override
    @Transactional
    public void add(UserShard shard) {
        log.info("绑定免费送关系,{}", shard);
        Assert.notNull(shard.getOrderNo(), "邀请人订单号不能为空");
        Assert.notNull(shard.getShardTo(), "被邀请人用户id不能为空");
        Assert.notNull(shard.getShardFrom(), "邀请人用户id不能为空");

        if (shard.getShardFrom().equals(shard.getShardTo()))
            return;

        UserShard us = new UserShard();
        us.setShardTo(shard.getShardTo());
        us.setOrderNo(shard.getOrderNo());
        us.setShardFrom(shard.getShardFrom());
        us.setShardType(UserShardConstants.UserShardType.FREE_ORDER);
        EntityWrapper<UserShard> wrapper = new EntityWrapper<>(us);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shard.getShardTo());
        int cnt = selectCount(wrapper);
        if (cnt > 0)
            return;

        Map<String, Object> map = new HashMap<>();
        map.put("userId", shard.getShardTo());
        ReturnData<Boolean> data = orderFeignClient.checkNewUser(map);
        if (data == null) {
            log.info("查询新老用户接口报错,{}", data);
            return;
        }
        log.info("绑定关系时用户身份结果:{}", data);
        shard.setUserFlag(data.getData() ? 1 : 0);
        shard.setShardType(UserShardConstants.UserShardType.FREE_ORDER);
        shard.setCreaterTime(new Date());

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shard.getShardTo());
        boolean res = insert(shard);
        log.info("绑定免费送关系结果:{},{}", res, shard);
    }

    @Override
    @Transactional
    public boolean del(Long shardTo) {
        UserShard shard = new UserShard();
        shard.setShardTo(shardTo);
        shard.setShardType(UserShardConstants.UserShardType.FREE_ORDER);
        EntityWrapper<UserShard> wrapper = new EntityWrapper<>(shard);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shardTo);
        return delete(wrapper);
    }

    @Override
    public UserShard getByToUserId(Long toUserId) {
        UserShard shard = new UserShard();
        shard.setShardTo(toUserId);
        shard.setShardType(UserShardConstants.UserShardType.FREE_ORDER);
        EntityWrapper<UserShard> wrapper = new EntityWrapper<>(shard);
        wrapper.orderAsc(Collections.singletonList("CREATER_TIME"));
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, toUserId);
        return selectOne(wrapper);
    }

    /**
     * 推荐返现 - 保存
     *
     * @param userShard
     * @return
     */
    @Override
    public Object recommendSharedSave(UserShard userShard) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();  //被分享人
        JSONObject map = new JSONObject();
        //新用户才能参与推荐返现
        Map<String, Object> paramMap = new HashMap<String, Object>(1);
        paramMap.put("userId", userDetails.getUserId());
        Boolean isNewUser = orderFeignClient.checkNewUser(paramMap).getData();
        if (!isNewUser) {
            userShard.setShardTo(userDetails.getUserId());
            userShard.setShardType(UserShardConstants.UserShardType.RECOMMEND);
            List<UserShard> userShardList = userShardMapper.selectByShardTo(userShard);
            if (CollectionUtils.isEmpty(userShardList)) { //没有绑定过
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                BaseUser baseUser = baseUserService.getById(userShard.getShardFrom()); //分享人登录信息
                log.info("-->,推荐返现保存,分享人用户登录信息:{}", JSONObject.toJSONString(baseUser));
                if (baseUser != null) {
                    userShard.setFromHeadImg(baseUser.getImagesUrl());
                    userShard.setFromNiclName(baseUser.getUserFullName());
                }
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                BaseUserDto fromBaseUserDto = baseUserService.queryUserInfoByUserId(userShard.getShardFrom(), userDetails.getAppId());  //分享人的openid
                log.info("-->,推荐返现保存,分享人当前端的用户信息:{}", JSONObject.toJSONString(fromBaseUserDto));
                if (fromBaseUserDto != null) {
                    userShard.setFromShardOpenid(fromBaseUserDto.getOpenId());
                }

                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
                BaseUser toUser = baseUserService.getById(userDetails.getUserId()); //被分享人登录信息
                log.info("-->,推荐返现保存,被分享人用户登录信息:{}", JSONObject.toJSONString(toUser));
                if (toUser != null) {
                    userShard.setToHeadImg(toUser.getImagesUrl());
                    userShard.setToNickName(toUser.getUserFullName());
                }
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
                BaseUserDto toBaseUserDto = baseUserService.queryUserInfoByUserId(userDetails.getUserId(), userDetails.getAppId());  //被分享人的openid
                log.info("-->,推荐返现保存,被分享人当前端的用户信息:{}", JSONObject.toJSONString(toBaseUserDto));
                if (toBaseUserDto != null) {
                    userShard.setToShardOpenid(toBaseUserDto.getOpenId());
                }
                userShard.setCreaterTime(new Date());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                userShardMapper.insert(userShard);
                redPackageSave(userShard);  //保存红包信息
                map.put("shardId", userShard.getShardId());
            } else {
                map.put("message", "该用户已经绑定过");
                log.info("-->,推荐返现保存,该用户已经绑定过:{}", userDetails.getUserId());
            }
        } else {
            map.put("message", "该用户不是新用户");
            log.info("-->,推荐返现保存,用户不是新用户:{}", userDetails.getUserId());
        }
        return map;
    }

    /**
     * 推荐返现 - 给订单和支付调用，生成订单和支付成功，修改订单状态
     * orderStatus:  1:待付款  2:已支付
     *
     * @param userId
     * @param orderNo
     * @param orderStatus
     */
    @Override
    public void updateRecommendShared(Long userId, String orderNo, String appId, Integer orderStatus) {
        log.info("-->推荐返现,生成订单和支付成功修改状态开始,用户id:{}, 订单号:{},订单状态：{},当前端的appId：{}", userId, orderNo,orderStatus, appId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        BaseUser baseUser = baseUserService.getById(userId);
        log.info("-->推荐返现,用户信息:{}", JSONObject.toJSONString(baseUser));
        if (null == baseUser) {
            return;
        }
        UserShard shard = new UserShard();
        shard.setShardTo(userId);
        shard.setShardType(UserShardConstants.UserShardType.RECOMMEND);
        List<UserShard> shards = userShardMapper.selectByShardTo(shard);
        log.info("--> 推荐返现,数据查询结果：{}", JSONObject.toJSONString(shards));
        if (CollectionUtils.isEmpty(shards)) {
            return;
        }
        UserShard userShard = shards.get(0);
        //生成订单是时候,查询出来的数据, 是没有订单号的
        if(orderStatus.equals(UserShardConstants.Other.orderStatus.WAIT_PAY) && userShard.getOrderNo() != null ){
            return;
        }
        //支付成功的时候,查询出来的结果,是有订单号, 且订单状态是1
        if(orderStatus.equals(UserShardConstants.Other.orderStatus.FINISH_PAY)){
            if(!orderNo.equals(userShard.getOrderNo()) ||  userShard.getOrderStatus() != 1)
            return;
        }
        //推荐成为会员, 用户支付, 也会掉这个接口,防止更新推荐返现的已经确定收货的数据
        if (userShard.getOrderStatus() != null && userShard.getOrderStatus() == 3) {
            log.info("--> 推荐返现,用户已确定收货,用户id:{}", userId);
            return;
        }
        userShard.setShardTo(userId);
        if (null == userShard.getToShardOpenid() && appId != null) {     //补全被分享人的信息
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            BaseUserDto toBaseUserDto = baseUserService.queryUserInfoByUserId(userId, appId);
            userShard.setToShardOpenid(toBaseUserDto.getOpenId());
        }
        if (null == userShard.getToHeadImg()) {
            userShard.setToHeadImg(baseUser.getImagesUrl());
        }
        if (null == userShard.getToNickName()) {
            userShard.setToNickName(baseUser.getUserFullName());
        }

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());   //补全分享人的信息
        BaseUser fromUser = baseUserService.getById(userShard.getShardFrom());
        if (null == userShard.getFromShardOpenid() && appId != null) {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
            BaseUserDto fromBaseUserDto = baseUserService.queryUserInfoByUserId(userShard.getShardFrom(), appId);
            userShard.setFromShardOpenid(fromBaseUserDto.getOpenId());
        }
        if (null == userShard.getFromHeadImg()) {
            userShard.setFromHeadImg(fromUser.getImagesUrl());
        }
        if (null == userShard.getFromNiclName()) {
            userShard.setFromNiclName(fromUser.getUserFullName());
        }
        if (orderStatus.equals(UserShardConstants.Other.orderStatus.WAIT_PAY)) {
            userShard.setOrderNo(orderNo);
            userShard.setOrderCreateTime(new Date());
            userShard.setOrderStatus(UserShardConstants.Other.orderStatus.WAIT_PAY);
        }
        if (orderStatus.equals(UserShardConstants.Other.orderStatus.FINISH_PAY)) {
            userShard.setOrderStatus(UserShardConstants.Other.orderStatus.FINISH_PAY);
        }
        log.info("-->推荐返现,生成订单和支付成功修改状态,userId:{}, 订单号:{},订单状态：{}", userId, orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
        Integer integer = userShardMapper.updateById(userShard);
        log.info("-->推荐返现,生成订单和支付成功修改状态完成,userId:{}, 订单号:{},订单状态：{},更新结果:{}", userId, orderNo, orderStatus, integer);

    }

    /**
     * 推荐返现, 推荐成为会员: 被推荐人确定收货
     * 推荐返现: 给分享人发送奖励模板消息, 给被分享人发送零钱
     * 推荐成为会员: 给分享人发送奖励模板消息
     *
     * @param orderNo
     * @param userid
     */
    @Override
    public void updateConfirm(String orderNo, long userid, String appId) {
        log.info("-->推荐返现,被推荐人确定收货，用户id：{},订单号:{},当前端appId:{}", userid, orderNo, appId);
        UserShard shardInfo = new UserShard();
        shardInfo.setShardTo(userid);
        List<UserShard> shards = userShardMapper.selectByShardTo(shardInfo);
        log.info("--> 推荐返现,确定收货,数据查询结果：{}", JSONObject.toJSONString(shards));
        if (shards.isEmpty()) {
            return;
        }
        List<UserShard> recommendCollect = shards.stream().filter(n -> UserShardConstants.UserShardType.RECOMMEND.equals(n.getShardType())).collect(Collectors.toList());
        List<UserShard> memberCollect = shards.stream().filter(n -> UserShardConstants.UserShardType.MEMBER.equals(n.getShardType())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(recommendCollect)) { //这条记录是推荐订单的
            UserShard userShard = recommendCollect.get(0);
            if (orderNo.equals(userShard.getOrderNo())) {
                Integer orderStatus = userShard.getOrderStatus();
                if (orderStatus == UserShardConstants.Other.orderStatus.FINISH_PAY) {  //已付款才能发送零钱
                    try {
                        //todo 给推荐人发送得奖励的模板消息
                        log.info("-->推荐返现,确定收货,开始发送奖励的模板消息,用户id:{}", userShard.getShardFrom());
                        JSONObject tempParams = new JSONObject();
                        tempParams.put("appid", appId);  //同一端appid一样
                        tempParams.put("touser", userShard.getFromShardOpenid());
                        tempParams.put("page", "/pkgMember/cashBackCentre/main?from=aboutme&orderNor=" + orderNo);
                        tempParams.put("template_id", "AuH2RwmJDM7rGwNwRrvQLIz4sQwyzeM0UpC-8eeRlIk"); //其他小程序也能接收到這個模板id

                        JSONObject data = new JSONObject();
                        JSONObject keyword1 = new JSONObject();
                        keyword1.put("value", "2元");

                        JSONObject keyword2 = new JSONObject();
                        keyword2.put("value", DateUtils.getNowDate("yyyy-MM-dd"));

                        JSONObject keyword3 = new JSONObject();
                        keyword3.put("value", " 推荐下单返现");

                        JSONObject keyword4 = new JSONObject();
                        EntityWrapper<UserShard> wrapper = new EntityWrapper<>();
                        wrapper.eq("SHARD_FROM", userShard.getShardFrom());
                        wrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.CONFIRM_GOOD);
                        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                        Integer amount = getAmount(wrapper) + 200;
                        keyword4.put("value", "累计收益为" + (amount / 100) + "元");

                        JSONObject keyword5 = new JSONObject();
                        keyword5.put("value", "好友通过您的推荐下单，您获得了2元现金，确认收货后即可提取。点击查看返现详情");

                        data.put("keyword1", keyword1);
                        data.put("keyword2", keyword2);
                        data.put("keyword3", keyword3);
                        data.put("keyword4", keyword4);
                        data.put("keyword5", keyword5);
                        tempParams.put("data", data);
                        wxMessageFeignClient.sendTemplateM(tempParams.toJSONString());  //发送模板消息
                        log.info("-->推荐返现,确定收货,发送模板消息成功,用户id:{}", userShard.getShardFrom());
                    } catch (Exception e) {
                        log.info("-->推荐返现,确定收货,发送板消息失败,用户id:{},报错信息:{}", userShard.getShardFrom(), e.getMessage());
                    }
                    try {
                        //todo 给被推荐人发送零钱
                        WxpayTransfers wxpayTransfers = new WxpayTransfers();
                        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
                        BaseUserDto baseUserDto = baseUserService.queryUserInfoByUserId(userid, appId);
                        wxpayTransfers.setMchAppid(appId);
                        wxpayTransfers.setOpenid(baseUserDto.getOpenId());
                        wxpayTransfers.setAmount(200);
                        wxpayTransfers.setDesc("推荐下单返现");
                        String partnerTradeNo = MD5Util.MD5Encode(JSONObject.toJSONString(orderNo), "utf-8");
                        wxpayTransfers.setPartnerTradeNo(partnerTradeNo);
                        wxpayTransfersFeignClient.transfers(wxpayTransfers);    //发送零钱
                        log.info("-->推荐返现,确定收货发送零钱成功,用户id:{}", userid);
                    } catch (Exception e) {
                        log.info("-->推荐返现,确定收货发送零钱失败,用户id:{},报错信息:{}", userid, e.getMessage());
                    }
                    //修改订单状态
                    UserShard shard = new UserShard();
                    shard.setShardId(userShard.getShardId());
                    shard.setOrderStatus(UserShardConstants.Other.orderStatus.CONFIRM_GOOD); //确定收货
                    shard.setOrderEndTime(new Date());
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                    userShardMapper.updateById(shard);
                    log.info("-->推荐返现,确定收货,修改订单状态成功,用户id:{},订单号：{}", userid, orderNo);

                    //修改被推荐人的红包状态
                    RedPackageUser redPackageUser = new RedPackageUser();
                    redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.FINISH_GET);
                    redPackageUser.setAccountTime(new Date());
                    EntityWrapper<RedPackageUser> entity = new EntityWrapper<>();
                    entity.eq("USER_ID", userShard.getShardTo());
                    entity.eq("BUSINESS_ID", userShard.getShardId());
                    entity.eq("PACKAGE_SOURCE", UserShardConstants.Other.packageSource.RECOMMEND);
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
                    redPackageUserMapper.update(redPackageUser, entity);
                    log.info("-->推荐返现,确定收货,修改红包状态成功,用户id:{},分享id：{}", userid, userShard.getShardId());
                }
            }
        }

        if (CollectionUtil.isNotEmpty(memberCollect)) { //这个记录是推荐成为会员的
            UserShard userShard = memberCollect.get(0);
            if (orderNo.equals(userShard.getOrderNo())) {
                //todo 给推荐人发送获得奖励的模板消息
                try {
                    log.info("-->推荐成为会员,确定收货,开始发送奖励的模板消息,被推荐人用户id:{}", userShard.getShardFrom());
                    JSONObject tempParams = new JSONObject();
                    tempParams.put("appid", appId);   //同一段appid一样
                    tempParams.put("touser", userShard.getFromShardOpenid());
                    tempParams.put("page", "/pkgMember/cashBackCentre/main?from=aboutme&orderNom=" + orderNo);
                    tempParams.put("template_id", "AuH2RwmJDM7rGwNwRrvQLIz4sQwyzeM0UpC-8eeRlIk"); //其他小程序也能接收到這個模板id

                    JSONObject data = new JSONObject();
                    JSONObject keyword1 = new JSONObject();
                    keyword1.put("value", "5元");

                    JSONObject keyword2 = new JSONObject();
                    keyword2.put("value", DateUtils.getNowDate("yyyy-MM-dd"));

                    JSONObject keyword3 = new JSONObject();
                    keyword3.put("value", " 邀请好友成为会员");

                    JSONObject keyword4 = new JSONObject();
                    EntityWrapper<UserShard> wrapper = new EntityWrapper<>();
                    wrapper.eq("SHARD_FROM", userShard.getShardFrom());
                    wrapper.eq("SHARD_TYPE", UserShardConstants.UserShardType.MEMBER);
                    wrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.CONFIRM_GOOD);
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                    Integer amount = getAmount(wrapper) + 500;
                    keyword4.put("value", "累计收益为" + (amount / 100) + "元");

                    JSONObject keyword5 = new JSONObject();
                    keyword5.put("value", "您邀请的好友已经成为会员，您获得了5元现金。点击查看返现详情");

                    data.put("keyword1", keyword1);
                    data.put("keyword2", keyword2);
                    data.put("keyword3", keyword3);
                    data.put("keyword4", keyword4);
                    data.put("keyword5", keyword5);
                    tempParams.put("data", data);
                    wxMessageFeignClient.sendTemplateM(tempParams.toJSONString());  //发送模板消息
                    log.info("-->推荐成为会员,确定收货,发送模板消息成功,被推荐人用户id:{}", userShard.getShardFrom());
                } catch (Exception e) {
                    log.info("-->推荐成为会员,确定收货,发送模板消息失败,被推荐人用户id:{},报错信息:{}", userShard.getShardFrom(), e.getMessage());
                }

                //修改订单状态
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());   //补全分享人的信息
                BaseUser fromUser = baseUserService.getById(userShard.getShardFrom());
                if (null == userShard.getFromShardOpenid()) {
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                    BaseUserDto fromBaseUserDto = baseUserService.queryUserInfoByUserId(userShard.getShardFrom(), appId);
                    userShard.setFromShardOpenid(fromBaseUserDto.getOpenId());
                }
                if (null == userShard.getFromHeadImg()) {
                    userShard.setFromHeadImg(fromUser.getImagesUrl());
                }
                if (null == userShard.getFromNiclName()) {
                    userShard.setFromNiclName(fromUser.getUserFullName());
                }

                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid); //补全被分享人的信息
                BaseUser baseUser = baseUserService.getById(userid);
                if (null == userShard.getToShardOpenid()) {
                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
                    BaseUserDto toBaseUserDto = baseUserService.queryUserInfoByUserId(userid, appId);
                    userShard.setToShardOpenid(toBaseUserDto.getOpenId());
                }
                if (null == userShard.getToHeadImg()) {
                    userShard.setToHeadImg(baseUser.getImagesUrl());
                }
                if (null == userShard.getToNickName()) {
                    userShard.setToNickName(baseUser.getUserFullName());
                }
                userShard.setOrderStatus(UserShardConstants.Other.orderStatus.CONFIRM_GOOD); //确定收货
                userShard.setOrderEndTime(new Date());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                userShardMapper.updateById(userShard);
                log.info("-->推荐成为会员,确定收货,修改订单状态,用户id:{},订单号:{}", userShard.getShardTo(), userShard.getOrderNo());
            }
        }
    }

    /**
     * 会员中心:即将到账, 未支付, 可提现 明细查询
     * type 1: 待付款  2:已支付(即将到账)  3:确定收货
     *
     * @param type
     * @return
     */
    @Override
    public List<UserShardEx> queryCash(String type, Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM", userId);
        entityWrapper.orderBy("CREATER_TIME DESC");

        switch (type) {
            case "1":  //未支付明细
                entityWrapper.eq("SHARD_TYPE", UserShardConstants.Other.packageSource.RECOMMEND);   //推荐
                entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.WAIT_PAY); //未支付
                break;
            case "2": //即将到账明细
                entityWrapper.eq("SHARD_TYPE", UserShardConstants.Other.packageSource.RECOMMEND);   //推荐
                entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.FINISH_PAY); //已付款
                break;
            case "3": //可提现明细
                entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.CONFIRM_GOOD); //确定收货
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        List<UserShard> shards = userShardMapper.selectList(entityWrapper);
        List<UserShardEx> list = JSONObject.parseArray(JSON.toJSONString(shards), UserShardEx.class);
        if (CollectionUtil.isNotEmpty(list)) {
            for (int i = 0; i < list.size(); i++) {
                //查询红包表，过滤已领取的数据
                EntityWrapper<RedPackageUser> entity = new EntityWrapper<>();
                entity.eq("USER_ID", list.get(i).getShardFrom());
                entity.eq("BUSINESS_ID", list.get(i).getShardId());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
                List<RedPackageUser> redPackageUsers = redPackageUserMapper.selectList(entity);
                if (CollectionUtil.isNotEmpty(redPackageUsers)) {
                    Integer packageStatus = redPackageUsers.get(0).getPackageStatus();
                    if (packageStatus == 1) {  //已领取
                        list.remove(i);
                        i--;
                    } else {
                        Integer packageAmount = redPackageUsers.get(0).getPackageAmount();
                        list.get(i).setPackageAmount(packageAmount / 100);
                        if ("2".equals(type)) {   //判断改订单是否为团订单
                            boolean groupOrder = OrderUtils.isGroupOrder(list.get(i).getOrderNo());
                            if (groupOrder) {
                                list.get(i).setOrderStatus(0);
                            }
                        }
                    }

                }
            }
        }

        return list;
    }

    /**
     * 查询可提现总额
     *
     * @return
     */
    @Override
    public Integer queryCanCashAmount(Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM", userId);
        entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.CONFIRM_GOOD);  //确定收货
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Integer amount = getAmount(entityWrapper);
        return amount;
    }

    /**
     * 查询即将到账总额
     *
     * @return
     */
    @Override
    public Integer querySoonCashAmount(Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM", userId);
        entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.FINISH_PAY);  //已完成
        entityWrapper.eq("SHARD_TYPE", UserShardConstants.Other.packageSource.RECOMMEND);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Integer amount = getAmount(entityWrapper);
        return amount;
    }

    /**
     * 查询未支付总额
     *
     * @return
     */
    @Override
    public Integer queryUnPayCashAmount(Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM", userId);
        entityWrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.WAIT_PAY);  //待支付
        entityWrapper.eq("SHARD_TYPE", UserShardConstants.UserShardType.RECOMMEND);  //未支付的,只会展示推荐返现的数据, 不会展示推荐成为会员的数据
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Integer amount = getAmount(entityWrapper);
        return amount;
    }

    /**
     * 用户退款, 更新被分享人退款信息, 售后调用
     *
     * @param userId
     * @param orderNo
     */
    @Override
    public boolean updateRefundByOrderNo(Long userId, String orderNo) {
        UserShard userShard = new UserShard();
        userShard.setShardTo(userId);
        userShard.setOrderNo(orderNo);
        userShard.setShardType(UserShardConstants.UserShardType.RECOMMEND);
        List<UserShard> shards = userShardMapper.selectByShardTo(userShard);
        if (CollectionUtil.isNotEmpty(shards)) {
            UserShard shard = shards.get(0);
            if (shard.getOrderStatus() == UserShardConstants.Other.orderStatus.CONFIRM_GOOD) {  //确定收货了, 才有退款
                shard.setOrderStatus(UserShardConstants.Other.orderStatus.REFUND_GOOD);   //退款
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shard.getShardFrom());
                userShardMapper.updateById(shard);
            }
        }
        return true;
    }

    /**
     * 查询退款金额, 售后调用
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public int queryRefundByOrderNo(Long userId, String orderNo) {
        UserShard shard = new UserShard();
        shard.setShardTo(userId);
        shard.setOrderNo(orderNo);
        shard.setShardType(UserShardConstants.UserShardType.RECOMMEND);  //推荐
        List<UserShard> shards = userShardMapper.selectByShardTo(shard);
        if (CollectionUtil.isEmpty(shards)) {
            return 0;
        } else {
            UserShard userShard = shards.get(0);
            if (userShard.getOrderStatus() == UserShardConstants.Other.orderStatus.CONFIRM_GOOD) {  //确定收货才存在退款
                EntityWrapper<RedPackageUser> entity = new EntityWrapper<>();
                entity.eq("USER_ID", userId);
                entity.eq("BUSINESS_ID", userShard.getShardId());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
                List<RedPackageUser> redPackageUsers = redPackageUserMapper.selectList(entity);
                if (CollectionUtil.isNotEmpty(redPackageUsers)) {
                    return redPackageUsers.get(0).getPackageAmount();
                }
            }
        }
        return 0;
    }

    /**
     * 批量更新提现记录
     *
     * @param userShardList
     * @return
     */
    @Override
    public boolean updateBachCash(List<UserShardEx> userShardList) {
        EntityWrapper<RedPackageUser> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("USER_ID", userShardList.get(0).getShardFrom());
        List<Integer> shardList = userShardList.stream().map(UserShard::getShardId).collect(Collectors.toList());
        entityWrapper.in("BUSINESS_ID", shardList);
        RedPackageUser redPackageUser = new RedPackageUser();
        redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.FINISH_GET);
        redPackageUser.setAccountTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShardList.get(0).getShardFrom());
        redPackageUserMapper.update(redPackageUser, entityWrapper);
        return true;
    }

    /**
     * 待支付状态下取消付款
     *
     * @param userId
     * @param orderNo
     * @return
     */
    @Override
    public boolean cancelOrder(Long userId, String orderNo) {
        log.info("-->进入推荐返现,取消付款，开始修改订单状态,userid:{},订单号:{}", userId, orderNo);
        UserShard userShard = new UserShard();
        userShard.setShardTo(userId);
        userShard.setOrderNo(orderNo);
        List<UserShard> shards = userShardMapper.selectByShardTo(userShard);
        log.info("-->进入推荐返现,推荐返现数据查询结果：{}", JSONObject.toJSONString(shards));
        if (CollectionUtil.isNotEmpty(shards)) {
            UserShard shard = shards.get(0);
            shard.setOrderStatus(UserShardConstants.Other.orderStatus.CANCEL_PAY);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shard.getShardFrom());
            userShardMapper.updateById(shard);
            log.info("-->进入推荐返现,取消付款，修改订单状态为已取消，成功");
        }

        if (CollectionUtil.isNotEmpty(shards)) {
            //修改红包状态
            RedPackageUser redPackageUser = new RedPackageUser();
            redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.PAST_DUE);  //已过期

            EntityWrapper<RedPackageUser> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("USER_ID", userId);
            entityWrapper.eq("BUSINESS_ID", shards.get(0).getShardId());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            redPackageUserMapper.update(redPackageUser, entityWrapper);
            log.info("-->进入推荐返现,取消付款，修改红包状态为已过期，成功");
        }
        return true;
    }

    /**
     * 推荐成为会员
     *
     * @param userShard
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Object saveUserShardInfo(UserShard userShard) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        userShard.setShardTo(userDetails.getUserId());
        userShard.setShardType(UserShardConstants.UserShardType.MEMBER);  //推荐成为会员
        List<UserShard> shards = userShardMapper.selectByShardTo(userShard);
        if (CollectionUtil.isNotEmpty(shards)) {  //说明这个人被别人推荐过, 不保存数据
            return "用户已被其他人推荐";
        } else {  //没被推荐,保存记录数

            //被分享人信息
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
            BaseUser toUser = baseUserService.getById(userDetails.getUserId()); //被分享人
            userShard.setShardTo(userDetails.getUserId());
            if (toUser != null) {
                userShard.setToHeadImg(toUser.getImagesUrl());
                userShard.setToNickName(toUser.getUserFullName());
            }
            BaseUserDto toBaseUserDto = baseUserService.queryUserInfoByUserId(userDetails.getUserId(), userDetails.getAppId());  //被分享人的openid
            if (toBaseUserDto != null) {
                userShard.setToShardOpenid(toBaseUserDto.getOpenId());
            }

            //分享人信息
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
            BaseUser baseUser = baseUserService.getById(userShard.getShardFrom()); //分享人
            if (baseUser != null) {
                userShard.setFromNiclName(baseUser.getUserFullName());
                userShard.setFromHeadImg(baseUser.getImagesUrl());
            }
            BaseUserDto fromBaseUserDto = baseUserService.queryUserInfoByUserId(userShard.getShardFrom(), userDetails.getAppId());  //分享人的openid
            if (fromBaseUserDto != null) {
                userShard.setFromShardOpenid(fromBaseUserDto.getOpenId());
            }
            userShard.setCreaterTime(new Date());
            userShard.setOrderStatus(UserShardConstants.Other.orderStatus.WAIT_PAY);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
            userShardMapper.insert(userShard);
            //保存红包记录数
            redPackageSave(userShard);
        }
        return userShard;
    }

    /**
     * 通过累计消费成为会员, 更新信息, 给会员调用
     *
     * @param param
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public UserShard saveUserSharedInfo(UserSharedParam param) {
        log.info("--> 推荐成为会员:{}",JSON.toJSONString(param));
        long userid = param.getUserId();
        String beMemberType = param.getBeMemberType();
        String orderNo = param.getOrderNo();
        String appId = param.getAppId();
        String openId = param.getOpenId();

        UserShard userShardInfo = new UserShard();
        userShardInfo.setShardTo(userid);
        userShardInfo.setShardType(UserShardConstants.UserShardType.MEMBER);
        List<UserShard> shards = userShardMapper.selectByShardTo(userShardInfo);
        if (CollectionUtil.isEmpty(shards)) {   //这个人没有被推荐过
            log.info("-->用户没有被推荐过,用户id:{}", userid);
            return null;
        }
        if (MemberConstant.BE_MEMBER_TYPE_ORDER.equalsIgnoreCase(beMemberType)) {  //通过累计消费成为会员
            log.info("--> 推荐成为会员,通过累计消费成为会员:{}",JSON.toJSONString(param));
            //更新订单号和订单金额
            UserShard userShard = shards.get(0);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
            BaseUser baseUser = baseUserService.getById(userid);
            log.info("--> 用户信息:{}", JSONObject.toJSONString(baseUser));
            if (null != baseUser) {
                if (null == userShard.getToShardOpenid()) {
                    userShard.setToShardOpenid(baseUser.getOpenId());
                }
                if (null == userShard.getToNickName()) {
                    userShard.setToNickName(baseUser.getUserFullName());
                }
                if (null == userShard.getToHeadImg()) {
                    userShard.setToHeadImg(baseUser.getImagesUrl());
                }
            } else {
                log.info("--> 用户信息为空");
            }
            userShard.setOrderNo(orderNo);
            userShard.setOrderCreateTime(new Date());
            userShard.setOrderStatus(UserShardConstants.Other.orderStatus.FINISH_PAY);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shards.get(0).getShardFrom());
            userShardMapper.updateById(userShard);
            log.info("--> 推荐成为会员,通过累计消费成为会员,更新信息成功:{}",JSON.toJSONString(userShard));
            return userShard;
        } else {  //直接购买188成为会员
            log.info("--> 推荐成为会员,直接购买成为会员{}",JSON.toJSONString(param));
            UserShard userShard = shards.get(0);
            try {
                //todo 给推荐人发送获得奖励的模板消息
                log.info("-->推荐成为会员,直接购买188成为会员,开始发送模板消息,用户id:{}", userShard.getShardFrom());
                JSONObject tempParams = new JSONObject();
                tempParams.put("appid", appId);   //同一端appid一样
                tempParams.put("touser", userShard.getFromShardOpenid());
                tempParams.put("page", "/pkgMember/cashBackCentre/main?from=aboutme&orderNob=" + orderNo);
                tempParams.put("template_id", "AuH2RwmJDM7rGwNwRrvQLIz4sQwyzeM0UpC-8eeRlIk"); //其他小程序也能接收到這個模板id

                JSONObject data = new JSONObject();
                JSONObject keyword1 = new JSONObject();
                keyword1.put("value", "5元");

                JSONObject keyword2 = new JSONObject();
                keyword2.put("value", DateUtils.getNowDate("yyyy-MM-dd"));

                JSONObject keyword3 = new JSONObject();
                keyword3.put("value", " 邀请好友成为会员");

                JSONObject keyword4 = new JSONObject();
                EntityWrapper<UserShard> wrapper = new EntityWrapper<>();
                wrapper.eq("SHARD_FROM", userShard.getShardFrom());
                wrapper.eq("ORDER_STATUS", UserShardConstants.Other.orderStatus.CONFIRM_GOOD);
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
                Integer amount = getAmount(wrapper) + 500;
                keyword4.put("value", "累计收益为" + (amount / 100) + "元");

                JSONObject keyword5 = new JSONObject();
                keyword5.put("value", "您邀请的好友已经成为会员，您获得了5元现金。点击查看返现详情");

                data.put("keyword1", keyword1);
                data.put("keyword2", keyword2);
                data.put("keyword3", keyword3);
                data.put("keyword4", keyword4);
                data.put("keyword5", keyword5);
                tempParams.put("data", data);
                wxMessageFeignClient.sendTemplateM(tempParams.toJSONString());  //发送模板消息
                log.info("-->推荐成为会员,直接购买188成为会员,发送模板消息成功,用户id:{}", userShard.getShardFrom());
            } catch (Exception e) {
                log.info("-->推荐成为会员,直接购买188成为会员,发送模板消息失败,用户id:{},报错信息:{}", userShard.getShardFrom(), e.getMessage());
            }

            if(StringUtils.isEmpty(openId)){
                log.info("-->推荐成为会员,直接购买188成为会员openId为空");
                return null;
            }
            try {
                // todo 给被推荐人发送零钱
                WxpayTransfers wxpayTransfers = new WxpayTransfers();
                wxpayTransfers.setMchAppid(appId);
                wxpayTransfers.setOpenid(openId);
                wxpayTransfers.setAmount(500);
                wxpayTransfers.setDesc("推荐会员返现");
                String partnerTradeNo = MD5Util.MD5Encode(JSONObject.toJSONString(userShard.getShardId()), "utf-8");
                wxpayTransfers.setPartnerTradeNo(partnerTradeNo);
                wxpayTransfersFeignClient.transfers(wxpayTransfers);    //发送零钱
                log.info("-->推荐成为会员,直接购买188成为会员,发送零钱成功,用户id:{}", userid);
            } catch (Exception e) {
                log.info(" --> 推荐成为会员,直接购买188成为会员,发送零钱失败,用户id:{},报错信息:{}", userid, e.getMessage());
            }


            //修改订单状态
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
            BaseUser baseUser = baseUserService.getById(userid);
            log.info("--> 用户信息:{}", JSONObject.toJSONString(baseUser));
            if (null != baseUser) {
                if (null == userShard.getToShardOpenid()) {
                    userShard.setToShardOpenid(baseUser.getOpenId());
                }
                if (null == userShard.getToNickName()) {
                    userShard.setToNickName(baseUser.getUserFullName());
                }
                if (null == userShard.getToHeadImg()) {
                    userShard.setToHeadImg(baseUser.getImagesUrl());
                }
            } else {
                log.info("--> 用户信息为空");
            }
            userShard.setOrderStatus(UserShardConstants.Other.orderStatus.CONFIRM_GOOD);
            userShard.setOrderNo(orderNo);
            userShard.setOrderEndTime(new Date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
            userShardMapper.updateById(userShard);
            log.info(" -->推荐成为会员,直接购买188成为会员,修改订单状态,用户id:" + userid + ",订单号:" + orderNo + ",修改订单状态成功");

            //修改红包状态
            RedPackageUser redPackageUser = new RedPackageUser();
            redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.FINISH_GET);
            redPackageUser.setAccountTime(new Date());
            EntityWrapper<RedPackageUser> entity = new EntityWrapper<>();
            entity.eq("USER_ID", userid);
            entity.eq("BUSINESS_ID", userShard.getShardId());
            entity.eq("PACKAGE_SOURCE", UserShardConstants.Other.packageSource.MEMBER);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userid);
            redPackageUserMapper.update(redPackageUser, entity);
            log.info("--> 推荐成为会员,直接购买188成为会员,修改红包状态,用户id:" + userid + ",修改红包状态成功");
            return userShard;
        }
    }

    /**
     * 查询金额
     *
     * @param entityWrapper
     * @return
     */
    private Integer getAmount(EntityWrapper<UserShard> entityWrapper) {
        List<UserShard> shards = userShardMapper.selectList(entityWrapper);
        Integer amount = 0;
        if (CollectionUtil.isNotEmpty(shards)) {
            for (UserShard shard : shards) {
                EntityWrapper<RedPackageUser> entity = new EntityWrapper<>();
                entity.eq("USER_ID", shard.getShardFrom());
                entity.eq("PACKAGE_STATUS", 0);  //待领取
                entity.eq("BUSINESS_ID", shard.getShardId());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shard.getShardFrom());
                List<RedPackageUser> list = redPackageUserMapper.selectList(entity);
                if (CollectionUtil.isNotEmpty(list)) {
                    Integer packageAmount = list.get(0).getPackageAmount();
                    amount += packageAmount;
                }
            }
        }
        return amount;
    }


    /**
     * 绑定用户关系时， 保存红包数据
     *
     * @param userShard
     */
    private void redPackageSave(UserShard userShard) {
        RedPackageUser redPackageUser = new RedPackageUser();
        if (userShard.getShardType().equals(UserShardConstants.UserShardType.RECOMMEND)) {  //好评分享返现
            redPackageUser.setBusinessId(userShard.getShardId());
            redPackageUser.setPackageSource(UserShardConstants.Other.packageSource.RECOMMEND);
            redPackageUser.setPackageAmount(200);
        }

        if (userShard.getShardType().equals(UserShardConstants.UserShardType.MEMBER)) {  //推荐成为会员
            redPackageUser.setBusinessId(userShard.getShardId());
            redPackageUser.setPackageSource(UserShardConstants.Other.packageSource.MEMBER);
            redPackageUser.setPackageAmount(500);
        }

        if (userShard.getShardFrom() != null) { //分享人
            redPackageUser.setUserId(userShard.getShardFrom());
            redPackageUser.setOpenId(userShard.getFromShardOpenid());
            redPackageUser.setCreaterId(userShard.getShardFrom());
            redPackageUser.setCreaterTime(DateUtil.date());
            redPackageUser.setActiveType(9);
            redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.WAIT_GET); //红包状态：未领取
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardFrom());
            redPackageUserMapper.insert(redPackageUser);
        }
        if (userShard.getShardTo() != null) { //被分享人
            redPackageUser.setUserId(userShard.getShardTo());
            redPackageUser.setOpenId(userShard.getToShardOpenid());
            redPackageUser.setCreaterId(userShard.getShardTo());
            redPackageUser.setCreaterTime(DateUtil.date());
            redPackageUser.setActiveType(9);
            redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.WAIT_GET); //红包状态：未领取
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardTo());
            redPackageUserMapper.insert(redPackageUser);
        }
    }

    /**
     * 用户点击提现
     *
     * @return
     */
    @Override
    @Transactional
    public ReturnData<Object> doCash() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        ReturnData<Object> rd = new ReturnData<>();
        Long increment = redisTemplate.opsForValue().increment("com.mmj.user.recommend.service.impl.UserShardServiceImpl_" + userDetails.getUserId(), 1);
        if (increment == 1) {
            redisTemplate.expire("com.mmj.user.recommend.service.impl.UserShardServiceImpl_" + userDetails.getUserId(), 60, TimeUnit.SECONDS);
            Integer amount = queryCanCashAmount(userDetails.getUserId());  //可提现总金额
            log.info("-->推荐返现,用户提现,用户id:{},提现金额:{}", userDetails.getUserId(), amount);
            if (0 == amount) {
                log.info("-->推荐返现,用户提现,没有可提现的金额,用户id:{}", userDetails.getUserId());
                rd.setCode(SecurityConstants.FAIL_CODE);
                rd.setDesc("没有可提现的金额");
                return rd;
            }

            List<UserShardEx> shareds = queryCash("3", userDetails.getUserId());
            if (CollectionUtil.isNotEmpty(shareds)) {
                WxpayTransfers wxpayTransfers = new WxpayTransfers();
                wxpayTransfers.setMchAppid(userDetails.getAppId());
                wxpayTransfers.setOpenid(userDetails.getOpenId());
                wxpayTransfers.setAmount(amount);
                wxpayTransfers.setDesc("推荐成为会员提现");
                List<Integer> collect = shareds.stream().map(UserShard::getShardId).collect(Collectors.toList());
                String partnerTradeNo = MD5Util.MD5Encode(JSONObject.toJSONString(collect), "utf-8");
                wxpayTransfers.setPartnerTradeNo(partnerTradeNo);
                try {
                    wxpayTransfersFeignClient.transfers(wxpayTransfers);    //发送零钱
                    log.info("-->推荐返现,用户点击提现,发送零钱成功,用户id:{}", userDetails.getUserId());
                } catch (Exception e) {
                    JSONObject error = JSON.parseObject(e.getCause().getMessage().split("content:\n")[1]);
                    e.printStackTrace();
                    rd.setCode(SecurityConstants.FAIL_CODE);
                    rd.setDesc(error.getString("desc"));
                    log.info("-->推荐返现,用户点击提现,发送零钱失败,用户id:{}", userDetails.getUserId());
                    return rd;
                }
                updateBachCash(shareds);  //批量更新提现记录
                log.info("-->推荐返现,用户点击提现,批量更新红包状态成功,用户id:{}", userDetails.getUserId());
            }
        } else {
            rd.setCode(SecurityConstants.FAIL_CODE);
            rd.setDesc("请勿频繁操作,一分钟以后再试");
            log.info("-->推荐返现,用户点击提现,重复提交,用户id:{}", userDetails.getUserId());
            return rd;
        }
        rd.setCode(SecurityConstants.SUCCESS_CODE);
        rd.setDesc("提现成功");
        return rd;
    }

    /**
     * 被分享人确定收货10天后,发送零钱 - 定时任务
     *
     * @return
     */
    @Override
    public Object userShardSendMoney() {
        List<UserShard> list = userShardMapper.selectUserShard();
        log.info("-->推荐成为会员，定时任务查询确定收货10天后的数据：{}",JSON.toJSONString(list));
        if (CollectionUtil.isNotEmpty(list)) {
            list.stream().forEach(userShard -> {
                WxpayTransfers wxpayTransfers = new WxpayTransfers();
                UserLogin userLogin = userLoginService.getUserLoginInfoByUserName(userShard.getToShardOpenid());
                wxpayTransfers.setMchAppid(userLogin.getAppId());
                wxpayTransfers.setOpenid(userShard.getToShardOpenid());
                wxpayTransfers.setAmount(500);
                wxpayTransfers.setDesc("推荐会员返现");
                wxpayTransfers.setPartnerTradeNo(userShard.getShardId() + "t");
                log.info("-->推荐成为会员，定时任务,发送零钱参数{}",JSON.toJSONString(wxpayTransfers));
                wxpayTransfersFeignClient.transfers(wxpayTransfers);    //发送零钱
                log.info("-->推荐成为会员，定时任务,发送零钱成功：{}",JSON.toJSONString(userShard));

                //修改红包状态
                EntityWrapper<RedPackageUser> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("USER_ID", userShard.getShardTo());
                entityWrapper.eq("BUSINESS_ID", userShard.getShardId());
                RedPackageUser redPackageUser = new RedPackageUser();
                redPackageUser.setPackageStatus(UserShardConstants.Other.packageStatus.FINISH_GET);
                redPackageUser.setAccountTime(new Date());
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userShard.getShardTo());
                redPackageUserMapper.update(redPackageUser, entityWrapper);
                log.info("-->推荐成为会员，定时任务,修改红包状态成功，用户数据：{}，修改红包参数：{}",JSON.toJSONString(entityWrapper),JSON.toJSONString(redPackageUser));
            });
        }

        return "发送零钱成功!";
    }

    /**
     * 查询用户被谁绑定成为会员
     *
     * @return
     */
    @Override
    public Object queryBindMember() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        UserShard userShard = new UserShard();
        userShard.setShardTo(userDetails.getUserId());
        userShard.setShardType(UserShardConstants.UserShardType.MEMBER);
        List<UserShard> shards = userShardMapper.selectByShardTo(userShard);
        if (CollectionUtil.isNotEmpty(shards)) {
            return shards.get(0);
        }
        return null;
    }


    @Override
    @Transactional(rollbackFor=Exception.class)
    public void updateUserId(UserMerge userMerge) {
        Long newUserId = userMerge.getNewUserId();
        Long oldUserId = userMerge.getOldUserId();
        log.info("-->推荐返现表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);

        if(oldUserId == newUserId) {
            log.info("-->推荐返现表合并-->新旧userId相等，不用合并");
            return;
        }

        List<UserShard> fromShardList = this.selectFromShardList(oldUserId);
        if(CollectionUtils.isEmpty(fromShardList)){
            log.info("-->推荐返现表合并-->根据分享人oldUserId:{}未查到返现信息，不用合并", oldUserId);
        }else{
            updateFromUserId(newUserId, oldUserId, fromShardList);
        }

        UserShard userShard = new UserShard();
        userShard.setShardTo(oldUserId);
        List<UserShard> toShardList = userShardMapper.selectByShardTo(userShard);
        if(CollectionUtils.isEmpty(toShardList)){
            log.info("-->推荐返现表合并-->根据被分享人oldUserId:{}未查到返现信息，不用合并", oldUserId);
        }else{
           toShardList.forEach(shard -> {
              shard.setShardTo(newUserId);
              BaseContextHandler.set(SecurityConstants.SHARDING_KEY,shard.getShardFrom());
              userShardMapper.updateById(shard);
           });
            log.info("-->推荐返现表合并-->修改被分享数据成功,newUserId:{}",newUserId);
        }

        redPackageUserService.updateAllUser(oldUserId,newUserId);
    }


    public List<UserShard> selectFromShardList(Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM",userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userId);
        return userShardMapper.selectList(entityWrapper);
    }

    public void deleteAllShard(Long userId) {
        EntityWrapper<UserShard> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("SHARD_FROM",userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY,userId);
        userShardMapper.delete(entityWrapper);
    }

    private void updateFromUserId(Long newUserId, Long oldUserId, List<UserShard> fromShardList) {
        //判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        log.info("-->推荐返现表合并-->分享人,oldUserId:{}所在表t_user_shard_{}，newUserId:{}所在表t_user_shard_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);

        if(oldTableIndex != newTableIndex){
            fromShardList.forEach(userShard -> {
                userShard.setShardFrom(newUserId);
            });
            //插入新数据
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
            this.insertBatch(fromShardList);
            //删除老数据
            this.deleteAllShard(oldUserId);
            log.info("-->推荐返现表合并-->修改分享人数据成功,newUserId:{}所在表t_user_shard_{}",newUserId, newTableIndex);
        }else{
            log.info("-->推荐返现表合并-->分享人新旧ID都在同一张表：t_user_shard_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY,oldUserId);
            userShardMapper.updateUserId(oldUserId,newUserId);
            log.info("-->推荐返现表合并-->修改分享人数据成功,newUserId:{}所在表t_user_shard_{}",newUserId, newTableIndex);
        }
    }
}
