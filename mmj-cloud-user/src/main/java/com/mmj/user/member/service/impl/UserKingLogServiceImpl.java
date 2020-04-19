package com.mmj.user.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.member.constant.MemberKingConstant;
import com.mmj.user.member.mapper.UserKingLogMapper;
import com.mmj.user.member.model.KingUser;
import com.mmj.user.member.model.UserKingLog;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.KingUserService;
import com.mmj.user.member.service.UserKingLogService;
import com.mmj.user.member.service.UserMemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 买买金日志表 服务实现类
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Service
@Slf4j
public class UserKingLogServiceImpl extends ServiceImpl<UserKingLogMapper, UserKingLog> implements UserKingLogService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final KingUserService kingUserService;

    private final UserKingLogMapper logMapper;

    private final OrderFeignClient orderFeignClient;

    private final UserMemberService userMemberService;

    public UserKingLogServiceImpl(RedisTemplate<String, Object> redisTemplate, KingUserService kingUserService, UserKingLogMapper logMapper, OrderFeignClient orderFeignClient, UserMemberService userMemberService) {
        this.redisTemplate = redisTemplate;
        this.kingUserService = kingUserService;
        this.logMapper = logMapper;
        this.orderFeignClient = orderFeignClient;
        this.userMemberService = userMemberService;
    }

    @Override
    public List<UserKingLog> getMyKingLog() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (null == userDetails)
            return null;

        EntityWrapper<UserKingLog> wrapper = new EntityWrapper();
        wrapper.eq("USER_ID", userDetails.getUserId());
        wrapper.orderBy("CREATE_TIME", false);
        wrapper.orderBy("LOG_ID", false);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        return selectList(wrapper);
    }

    @Override
    public Double getSumKingNum() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (null == userDetails)
            return null;
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        return logMapper.getSumKingNum(userDetails.getUserId());
    }

    @Override
    public Map<String, Object> getActCnt() {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(userDetails, "用户未登录");
        Map<String, Object> data = Maps.newHashMapWithExpectedSize(2);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        int goodsCnt = logMapper.getLogCount(userDetails.getUserId()
                , "'" + MemberKingConstant.ShareType.SHARE_GOODS + "'",
                DateUtils.getNowDate(DateUtils.DATE_PATTERN_10));
        data.put("goodCnt", goodsCnt);

        StringBuilder shareStr = new StringBuilder("'").append(MemberKingConstant.ShareType.BARGAIN).append("','")
                .append(MemberKingConstant.ShareType.WHEELS).append("','").append(MemberKingConstant.ShareType.SIGN)
                .append("','").append(MemberKingConstant.ShareType.LOTTERY).append("'");
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userDetails.getUserId());
        int actCnt = logMapper.getLogCount(userDetails.getUserId()
                , shareStr.toString(), DateUtils.getNowDate(DateUtils.DATE_PATTERN_10));
        data.put("actCnt", actCnt);
        return data;
    }

    private boolean isMember(Long shareUserId) {
        UserMember um = userMemberService.queryUserMemberInfoByUserId(shareUserId);
        return um != null ? um.getActive() : false;
    }

    @Override
    public void clickInsert(UserKingLog uLog) {

        // 好友的ID
        long friendUserId = uLog.getFriendUserId();

        // 分享人的ID
        long shareUserId = uLog.getUserId();

        if (friendUserId == shareUserId) {
            log.info("自己给自己点击的不算");
            return;
        }

        if (!isMember(shareUserId)) {
            log.info("用户不是会员，userId:{}", shareUserId);
            return;
        }

        String today = DateUtils.getNowDate(DateUtils.DATE_PATTERN_10);

        //判断这个用户是否已经点击过了
        StringBuilder key = new StringBuilder("CLICK_INSERT:").append(today).
                append(":").append(shareUserId).append("-").append(friendUserId);

        Object insertVal = redisTemplate.opsForValue().get(key.toString());
        if (null != insertVal) {
            log.info("好友已经帮忙点击过了,分享人id:{} 点击用户id:{}", shareUserId, friendUserId);
            return;
        }
        int cnt = getCountByTypeAndTime(shareUserId, today, uLog.getShareType());
        int max = 1;
        if (MemberKingConstant.ShareType.SHARE_GOODS.equals(uLog.getShareType())) {
            max = 5;
        }
        if (cnt >= max) {
            log.info("{},已达最大次数", uLog.getShareType());
            return;
        }
        String markTxt = null;
        switch (uLog.getShareType()) {
            case MemberKingConstant.ShareType.SHARE_GOODS:
                markTxt = "分享商品成功";
                break;
            case MemberKingConstant.ShareType.SIGN:
                markTxt = "分享现金签到";
                break;
            case MemberKingConstant.ShareType.LOTTERY:
                markTxt = "分享抽奖活动";
                break;
            case MemberKingConstant.ShareType.BARGAIN:
                markTxt = "分享砍价活动";
                break;
            case MemberKingConstant.ShareType.WHEELS:
                markTxt = "分享转盘活动";
                break;
        }

        if (null == markTxt) {
            log.info("{},未知分享，不增加买买金", uLog.getShareType());
            return;
        }
        save(uLog, shareUserId, markTxt);
        redisTemplate.opsForValue().set(key.toString(), "1", 24, TimeUnit.HOURS);
    }


    @Override
    public void actInsert(Long userId, String type) {
        if (StringUtils.isBlank(type)) {
            log.info("参与活动的类型为空，type:{}", type);
            return;
        }

        if (!isMember(userId)) {
            log.info("参与活动时,用户不是会员，userId:{}", userId);
            return;
        }

        String today = DateUtils.getNowDate(DateUtils.DATE_PATTERN_10);
        int cnt = getCountByTypeAndTime(userId, today, type);
        if (cnt >= 1) {
            log.info("{},参与活动已达最大次数", type);
            return;
        }

        String markTxt = null;
        switch (type) {
            case MemberKingConstant.ShareType.SIGN:
                markTxt = "参与现金签到";
                break;
            case MemberKingConstant.ShareType.LOTTERY:
                markTxt = "参与抽奖活动";
                break;
            case MemberKingConstant.ShareType.BARGAIN:
                markTxt = "参与砍价活动";
                break;
            case MemberKingConstant.ShareType.WHEELS:
                markTxt = "参与转盘活动";
                break;
        }
        if (null == markTxt) {
            log.info("{},未知活动，不增加买买金", type);
            return;
        }
        UserKingLog uLog = new UserKingLog();
        uLog.setShareType(type);
        save(uLog, userId, markTxt);
    }

    @Override
    public void addMMKing(Long userId, String orderNo) {
        log.info("推荐订单获得买买金,orderNo:{}", orderNo);
        UserKingLog uLog = new UserKingLog();
        uLog.setKingContext("推荐获得:" + orderNo);
        uLog.setUserId(userId);
        uLog.setShareType(MemberKingConstant.ShareType.RECOMMEND);
        uLog.setCreateTime(new Date());
        uLog.setOrderNo(orderNo);
        uLog.setSurplus(30);
        uLog.setUpdateNum(30);
        uLog.setSort(0);

        KingUser ku = kingUserService.getByUserId(userId);
        if (null == ku) {
            uLog.setKingNum(30);
            ku = new KingUser();
            ku.setKingNum(30);
            ku.setCreateTime(new Date());
            ku.setUserId(userId);
            kingUserService.insert(ku);
        } else {
            uLog.setKingNum(ku.getKingNum() + 30);
            ku.setKingNum(ku.getKingNum() + 30);
            ku.setUpdateTime(new Date());
            kingUserService.updateById(ku);
        }
        insert(uLog);
    }

    @Override
    @Transactional
    public int orderKingProd(Map<String, Object> map) {
        Long userId = Long.parseLong(map.get("userId").toString());
        String orderNo = (String) map.get("orderNo");
        int kingNum = (int) map.get("kingNum");

        boolean isGiveBy = false;
        if ("1".equals(map.get("isGiveBy"))) isGiveBy = true;

        UserKingLog uLog = new UserKingLog();
        uLog.setUserId(userId);
        if (isGiveBy) {
            uLog.setKingContext("买送活动获得");
            uLog.setShareType(MemberKingConstant.ShareType.GIVE_BUY);
            uLog.setSort(MemberKingConstant.KingLogSort.GIVE_BUY_GET);
        } else {
            uLog.setKingContext("订单获得:" + orderNo);
            uLog.setShareType(MemberKingConstant.ShareType.ORDER);
            uLog.setSort(MemberKingConstant.KingLogSort.ORDER_GET);
        }
        uLog.setOrderNo(orderNo);
        uLog.setStatus(0);
        uLog.setUpdateNum(kingNum);
        uLog.setSurplus(kingNum);
        uLog.setCreateTime(new Date());

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        KingUser kingUser = kingUserService.getByUserId(userId);
        if (null == kingUser) {
            uLog.setKingNum(kingNum);
            kingUser = new KingUser();
            kingUser.setKingNum(kingNum);
            kingUser.setUserId(userId);
            kingUser.setCreateTime(new Date());
            kingUser.setUsedNum(0);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            kingUserService.insert(kingUser);
        } else {
            uLog.setKingNum(kingUser.getKingNum() + kingNum);
            kingUser.setKingNum(kingUser.getKingNum() + kingNum);
            kingUser.setUpdateTime(new Date());
            kingUserService.updateById(kingUser);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        insert(uLog);
        return kingUser.getKingNum();
    }

    private void save(UserKingLog uLog, Long shareUserId, String markTxt) {
        uLog.setKingContext(markTxt);
        uLog.setUserId(shareUserId);
        uLog.setCreateTime(new Date());
        uLog.setUpdateNum(10);
        uLog.setSort(MemberKingConstant.KingLogSort.ACTIVE_GET);
        uLog.setStatus(0);
        uLog.setSurplus(10);
        KingUser ku = kingUserService.getByUserId(shareUserId);
        log.info("查询到用户买买金账户:{}", null == ku ? null : ku.getKingNum());
        if (null == ku) {
            uLog.setKingNum(10);
            ku = new KingUser();
            ku.setKingNum(10);
            ku.setCreateTime(new Date());
            ku.setUserId(shareUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shareUserId);
            kingUserService.insert(ku);
        } else {
            uLog.setKingNum(ku.getKingNum() + 10);
            ku.setKingNum(ku.getKingNum() + 10);
            ku.setUpdateTime(new Date());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shareUserId);
            kingUserService.updateById(ku);
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, shareUserId);
        insert(uLog);
    }

    @Override
    public boolean degradeProces(String orderNo, Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        KingUser kingUser = kingUserService.getByUserId(userId);
        if (null == kingUser)
            return false;
        int originNum = kingUser.getKingNum();
        log.info("退款降级时查询到账户买买金余额:{}", originNum);

        String data = orderFeignClient.getGiveBy(userId);
        if (null == data)
            return false;
        JSONObject ok = JSONObject.parseObject(data);
        if (ok == null)
            return false;
        Integer num = ok.getInteger("num");
        log.info("退款降级时查询到买送活动获得买买金:{}", num);
        kingUser.setUsedNum(0);
        int surplus = ((kingUser.getKingNum() - num) <= 0) ? 0 : (kingUser.getKingNum() - num);
        kingUser.setKingNum(surplus);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        kingUserService.updateById(kingUser);

        JSONObject oKing = new JSONObject();
        oKing.put("id", ok.getInteger("id"));
        oKing.put("status", MemberKingConstant.OrderKingStatus.DELETE);
        orderFeignClient.updateById(oKing);

        UserKingLog kingLog = new UserKingLog();
        kingLog.setSort(MemberKingConstant.KingLogSort.GIVE_BUY_GET);
        kingLog.setOrderNo(orderNo);
        int dec = originNum - num;
        log.info("会员降级时剩余买买金:{}", dec);
        if (dec >= 0) {
            //未使用买买金
            kingLog.setUpdateNum(-num);
        } else {
            kingLog.setUpdateNum(-originNum);
        }
        kingLog.setKingNum(surplus);
        kingLog.setStatus(0);
        kingLog.setCreateTime(new Date());
        kingLog.setKingContext("订单退款，会员降级");
        kingLog.setShareType(MemberKingConstant.ShareType.REFUND);
        kingLog.setUserId(userId);
        kingLog.setCreateTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        insert(kingLog);
        return true;
    }

    @Override
    public int getOweKingNum(String orderNo, Long userId) {
        KingUser kingUser = kingUserService.getByUserId(userId);
        if (null == kingUser)
            return 0;

        log.info("降级时查询到账户买买金余额,B:{}", kingUser.getKingNum());
        String data = orderFeignClient.getGiveBy(userId);
        if (StringUtils.isBlank(data))
            return 0;
        JSONObject ok = JSONObject.parseObject(data);
        if (ok == null)
            return 0;
        int orderKingNum = ok.getInteger("num");
        log.info("降级时查询到买送活动获得买买金,A:{}", orderKingNum);
        if (orderKingNum > kingUser.getKingNum()) {
            return orderKingNum - kingUser.getKingNum();
        }
        return 0;
    }

    private int getCountByTypeAndTime(Long userId, String today, String type) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return logMapper.getCountByTypeAndTime(userId, type, today);
    }


    @Override
    public Integer procMMKing(OrdersMQDto dto) {
        Assert.notNull(dto.getUseKingNum(), "买买金数量不能为空");
        Assert.isTrue(dto.getUseKingNum() > 0, "买买金数量必须大于0");

        Assert.notNull(dto.getUserId(), "扣减买买金时userId不存在");

        Long userId = dto.getUserId();
        KingUser ku = kingUserService.getByUserId(userId);

        Assert.notNull(dto.getUserId(), "扣减买买金时发现买买金账户不存在");

        int kingNum = dto.getUseKingNum();
        ku.setKingNum(ku.getKingNum() - kingNum < 0 ? 0 : ku.getKingNum() - kingNum);
        ku.setUpdateTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        kingUserService.updateById(ku);
        UserKingLog kingLog = new UserKingLog();
        kingLog.setKingNum(ku.getKingNum());
        kingLog.setUpdateNum(-kingNum);
        kingLog.setCreateTime(new Date());
        kingLog.setUserId(userId);
        if (dto.getOrderType() == OrderType.MM_KING) {
            kingLog.setShareType(MemberKingConstant.ShareType.EXCHANGE);
            kingLog.setKingContext("兑换商品");
            if (null != dto.getGoods() && dto.getGoods().size() > 0)
                kingLog.setGoodId(dto.getGoods().get(0).getGoodId());
        } else {
            kingLog.setShareType(MemberKingConstant.ShareType.ORDER);
            kingLog.setKingContext("抵扣订单:" + dto.getOrderNo());
        }

        kingLog.setOrderNo(dto.getOrderNo());
        boolean bool = this.insert(kingLog);
        log.info("扣减买买金结果:{}", bool);
        return kingLog.getLogId();
    }
}
