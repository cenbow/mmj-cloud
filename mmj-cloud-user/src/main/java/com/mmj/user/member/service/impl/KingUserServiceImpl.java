package com.mmj.user.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.UserMerge;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.manager.model.BaseUser;
import com.mmj.user.manager.service.BaseUserService;
import com.mmj.user.member.dto.MyKingExchangeParam;
import com.mmj.user.member.mapper.KingUserMapper;
import com.mmj.user.member.mapper.UserKingLogMapper;
import com.mmj.user.member.model.KingUser;
import com.mmj.user.member.model.UserKingLog;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.service.KingUserService;
import com.mmj.user.member.service.UserMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户买买金表 服务实现类
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Service
@Slf4j
public class KingUserServiceImpl extends ServiceImpl<KingUserMapper, KingUser> implements KingUserService {

    private final OrderFeignClient orderFeignClient;

    private final BaseUserService baseUserService;

    private final KingUserMapper kingUserMapper;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    private UserKingLogMapper kingLogMapper;

    public KingUserServiceImpl(OrderFeignClient orderFeignClient, BaseUserService baseUserService, KingUserMapper kingUserMapper) {
        this.orderFeignClient = orderFeignClient;
        this.baseUserService = baseUserService;
        this.kingUserMapper = kingUserMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserId(UserMerge userMerge) {
        long oldUserId = userMerge.getOldUserId();
        long newUserId = userMerge.getNewUserId();
        log.info("-->买买金表合并-->oldUserId:{}, newUserId:{}", oldUserId, newUserId);
        if (oldUserId == newUserId) {
            log.info("-->买买金表合并-->新旧userId相等，不用合并");
            return;
        }
        this.mergeKingUser(oldUserId, newUserId);
        this.mergeKingLog(oldUserId, newUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void mergeKingLog(long oldUserId, long newUserId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        EntityWrapper<UserKingLog> wrapper = new EntityWrapper<>();
        wrapper.eq("USER_ID", oldUserId);
        List<UserKingLog> list = kingLogMapper.selectList(wrapper);
        if (null == list || list.size() == 0) {
            log.info("-->买买金记录合并-->根据oldUserId:{}未查到买买金记录，不用合并", oldUserId);
            return;
        }

        // 判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        log.info("-->买买金记录表合并-->oldUserId:{}所在表t_user_king_log_{}，newUserId:{}所在表t_user_king_log_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
        if (oldTableIndex == newTableIndex) {
            // 两个userId在同一张表，直接修改userId
            log.info("-->买买金记录合并-->新旧ID都在同一张表:t_user_king_log_{}，直接修改用户ID：{}为{}", oldTableIndex, oldUserId, newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            kingLogMapper.updateUserId(oldUserId, newUserId);
            return;
        }
        //数据迁移表

        // 1.插入到新表
        for (UserKingLog ukl : list) {
            ukl.setUserId(newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
            kingLogMapper.insert(ukl);
        }

        // 2.删除旧数据
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        kingLogMapper.delete(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void mergeKingUser(long oldUserId, long newUserId) {
        KingUser ku = getByUserId(oldUserId);
        if (null == ku) {
            log.info("-->买买金合并-->根据oldUserId:{}未查到买买金账户，不用合并", oldUserId);
            return;
        }

        // 判断是否需要切换表
        int oldTableIndex = (int) (oldUserId % 10);
        int newTableIndex = (int) (newUserId % 10);
        log.info("-->买买金表合并-->oldUserId:{}所在表t_king_user_{}，newUserId:{}所在表t_king_user_{}", oldUserId, oldTableIndex, newUserId, newTableIndex);
        if (oldTableIndex == newTableIndex) {
            // 两个userId在同一张表，直接修改userId
            log.info("-->买买金表合并-->新旧ID都在同一张表:t_king_user_{}，直接修改用户ID:{}为{}", oldTableIndex, oldUserId, newUserId);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
            kingUserMapper.updateUserId(oldUserId, newUserId);
            return;
        }
        //数据迁移表

        // 1.插入到新表
        ku.setUserId(newUserId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, newUserId);
        this.insert(ku);

        // 2.删除旧数据
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, oldUserId);
        EntityWrapper<KingUser> wrapper = new EntityWrapper<>();
        wrapper.eq("USER_ID", oldUserId);
        this.delete(wrapper);
    }

    @Override
    public KingUser getByUserId(Long userId) {
        KingUser user = new KingUser();
        user.setUserId(userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return kingUserMapper.selectOne(user);
    }

    @Override
    public Map<String, Object> getMyKing(Long userId) {
        Map<String, Object> map = Maps.newHashMapWithExpectedSize(1);
        if (null == userId) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            if (null != userDetails) {
                userId = userDetails.getUserId();
            }
        }
        if (null == userId) {
            map.put("myKing", 0);
            map.put("frozen", 0);//冻结数量
            return map;
        }
        // TODO: 2019/7/15 判断用户是否是会员
        KingUser ku = new KingUser();
        ku.setUserId(userId);
        EntityWrapper<KingUser> wrapper = new EntityWrapper<>(ku);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        ku = selectOne(wrapper);
        map.put("myKing", null == ku ? 0 : ku.getKingNum());//我的买买金
        int frozen = orderFeignClient.frozenKingNum(userId);
        map.put("frozen", frozen);//冻结数量
        return map;
    }

    @Override
    public Boolean verify(Long userId, Integer count) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        BaseUser baseUser = baseUserService.getById(userId);
        Assert.notNull(baseUser, "用户不存在");
        KingUser ku = getByUserId(userId);
        Assert.notNull(ku, "买买金账户不存在");
        log.info("用户id:{},买买金数量:{},使用买买金数量:{}", userId, ku.getKingNum(), count);
        if (count > ku.getKingNum())
            return false;
        //查询冻结的买买金
        int frozen = orderFeignClient.frozenKingNum(userId);
        log.info("总数:{},冻结数:{},使用数:{}", ku.getKingNum(), frozen, count);
        return ku.getKingNum() >= (frozen + count);
    }

    @Override
    public Map<String, Object> getMyKingExchangeInfo(MyKingExchangeParam param) {
        long userId = SecurityUserUtil.getUserDetails().getUserId();
        // 当前只有普通订单、十元店订单、拼团订单（二人团）可进行买买金抵扣
        int orderType = param.getOrderType();
        if (!(orderType == OrderType.ORDINARY || orderType == OrderType.TEN_YUAN_SHOP ||
                orderType == OrderType.TWO_GROUP)) {
            log.info("-->getMyKingExchangeInfo-->当前订单类型不可使用买买金进行抵扣，orderType:{}", orderType);
            return null;
        }
        Map<String, Object> resultMap = new HashMap<String, Object>();

        Map<String, Object> map = this.getMyKing(userId);
        int useKing = 0;
        int myKing = 0;
        int frozenAmount = 0;
        if (!map.isEmpty()) {
            myKing = map.get("myKing") != null ? (int) map.get("myKing") : 0; // 买买金余额
            frozenAmount = map.get("frozen") != null ? (int) map.get("frozen") : 0; // 冻结余额
            // 可使用的买买金  = 买买金余额 - 冻结的数量
            useKing = myKing - frozenAmount;
            useKing = useKing < 0 ? 0 : useKing;
            log.info("-->getMyKingExchangeInfo-->用户{}买买金余额为{}，冻结的买买金为{}，最终的买买金余额为{}", userId, myKing, frozenAmount, useKing);
        }
        resultMap.put("myKing", useKing);
        if (useKing < 1000) {
            resultMap.put("exchangeDesc", "共" + useKing + "个，满1000个可用");
            resultMap.put("exchangeMoneyDesc", "0元");
            resultMap.put("useKingNum", 0);
            resultMap.put("exchangeMoney", 0);
        } else {
            int maxMoney = 10;// 最高可抵扣10元
            int canUseMaxKing1 = maxMoney * 1000; // 最多可使用的买买金个数
            DecimalFormat df = new DecimalFormat("#");
            UserMember member = userMemberService.queryUserMemberInfoByUserId(userId);
            boolean isMember = member != null && member.getActive();
            double goodTotalPrice = param.getGoodTotalPrice(isMember);
            int canUseMaxKing2 = Integer.valueOf(df.format(goodTotalPrice * 1000 * 0.1));// 最高可抵扣订单商品金额的10%
            int canUseMaxKing = 0;
            if (canUseMaxKing1 == canUseMaxKing2) {
                canUseMaxKing = canUseMaxKing1;
            } else {
                canUseMaxKing = canUseMaxKing1 > canUseMaxKing2 ? canUseMaxKing2 : canUseMaxKing1;
            }

            useKing = useKing >= canUseMaxKing ? canUseMaxKing : useKing; // 当前使用
            // 抵扣金额保留两位小数，向上取整，如2996则是抵扣3块--->2019年6月11号下午2点跟测试肖聪已与产品唐超确认
            double exchangeMoney = DoubleUtil.divideUp((double) useKing, 1000d, DoubleUtil.SCALE_2);

            resultMap.put("exchangeDesc", "可用" + useKing + "个抵扣" + exchangeMoney + "元");
            resultMap.put("exchangeMoneyDesc", "-" + exchangeMoney + "元");
            resultMap.put("useKingNum", useKing);
            resultMap.put("exchangeMoney", exchangeMoney);
        }
        return resultMap;
    }
}
