package com.mmj.user.member.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.common.constants.MemberConstant;
import com.mmj.common.constants.OrderType;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.vo.MemberOrderVo;
import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import com.mmj.user.member.dto.SaveUserMemberDto;
import com.mmj.user.member.mapper.UserMemberSendMapper;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.UserMemberSend;
import com.mmj.user.member.model.Vo.PayIsBuyGiveVo;
import com.mmj.user.member.model.Vo.PayRecordQualificationsVo;
import com.mmj.user.member.service.MemberConfigService;
import com.mmj.user.member.service.UserMemberSendService;
import com.mmj.user.member.service.UserMemberService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
@Slf4j
@Service
public class UserMemberSendServiceImpl extends ServiceImpl<UserMemberSendMapper, UserMemberSend> implements UserMemberSendService {

    @Autowired
    private UserMemberSendMapper userMemberSendMapper;

    @Autowired
    private UserMemberService userMemberService;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private MemberConfigService memberConfigService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 是否享受买送，true：是；false：否
     */
    private static final String MAP_KEY_BUYGIVE = "isBuyGive";
    private static final String MAP_KEY_SURPLUSTIME = "surplusTime";
    private static final String STRING_EMPTY = "";
    private static final String CREATE_BY = "create_by";
    private static final String ORDER_NO = "ORDER_NO";

    /**
     * 获取买送结束时间
     *
     * @return
     */
    @Override
    public Map<String, Object> getActivitySurplusTime() {

        long userId = SecurityUserUtil.getUserDetails().getUserId();
        log.info("-->用户{}查询买送活动结束时间.", userId);

        Map<String, Object> map = new HashMap<String, Object>();

        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);

        if (userMember == null || !userMember.getActive()) {
            map.put(MAP_KEY_BUYGIVE, false);
            return map;
        }

        Date now = new Date();

        // 会员买送活动配置的{day}天内的首单享受买送权益
        int day = memberConfigService.getMmjMemberFirstOrderDayLimit();

        // 如果超过指定的天数，则不享受买送
        if (DateUtils.addDays(userMember.getBeMemberTime(), day).before(now)) {
            map.put(MAP_KEY_BUYGIVE, false);
            map.put(MAP_KEY_SURPLUSTIME, STRING_EMPTY);
            return map;
        }

        // 查询是否首单
        Date memberTime = null;
        if (MemberConstant.BE_MEMBER_TYPE_ORDER.equals(userMember.getBeMemberType()) && !StringUtils.isEmpty(userMember.getOrderNo())) {
            // 如果通过下单成为会员，取下单时间
            OrderInfo memberOrder = orderFeignClient.getOrderByOrderNo(userMember.getOrderNo()).getData();
            if (memberOrder != null) {
                memberTime = memberOrder.getCreaterTime();
            } else {
                memberTime = userMember.getBeMemberTime();
            }
        } else {
            memberTime = userMember.getBeMemberTime();
        }
        MemberOrderVo memberOrderVo = new MemberOrderVo();
        memberOrderVo.setMemberTime(com.mmj.common.utils.DateUtils.SDF1.format(memberTime));
        memberOrderVo.setUserId(userId);
        List<OrderInfo> orderInfos = orderFeignClient.getOrderList(memberOrderVo).getData();
        if (!orderInfos.isEmpty()) {
            map.put(MAP_KEY_BUYGIVE, false);
            return map;
        } else {
            map.put(MAP_KEY_BUYGIVE, true);
        }

        Calendar c = Calendar.getInstance();
        c.setTime(userMember.getCreateTime());
        c.add(Calendar.DAY_OF_MONTH, day); //成为会员后X天买就送活动结束.
        map.put(MAP_KEY_SURPLUSTIME, com.mmj.common.utils.DateUtils.getSurplusTime(now, c.getTime()));
        return map;
    }

    /**
     * 写入买送资格
     *
     * @param orderNo
     * @param orderAmount
     * @param userId
     * @return
     */
    @Override
    public boolean saveBuyGive(String orderNo, Double orderAmount, Long userId) {
        UserMemberSend userMemberSend = new UserMemberSend();
        userMemberSend.setOrderNo(orderNo);
        userMemberSend.setOrderAmount(orderAmount);
        userMemberSend.setCreateBy(userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return userMemberSendMapper.insert(userMemberSend) > 0;
    }

    /**
     * 取消会员买送资格
     *
     * @param userId
     * @return
     */
    @Override
    public boolean editBuyGice(Long userId) {
        EntityWrapper<UserMemberSend> entityWrapper = new EntityWrapper<UserMemberSend>();
        entityWrapper.eq(CREATE_BY, userId);
        UserMemberSend userMemberSend = new UserMemberSend();
        userMemberSend.setStatus(false);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return userMemberSendMapper.update(userMemberSend, entityWrapper) > 0;
    }

    /**
     * 根据订单号获取是否有买送资格
     *
     * @param orderNo
     * @return
     */
    @Override
    public boolean getOrderIdBuyGive(String orderNo, Long userId) {
        EntityWrapper<UserMemberSend> entityWrapper = new EntityWrapper<UserMemberSend>();
        entityWrapper.eq(ORDER_NO, orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        List<UserMemberSend> userMemberSendList = userMemberSendMapper.selectList(entityWrapper);
        if (userMemberSendList.isEmpty()) {
            return false;
        }
        return userMemberSendList.get(0).getStatus();
    }

    /**
     * 微服务查询是否享受买送
     *
     * @param payIsBuyGiveVo
     * @return
     */
    @Override
    public boolean getPayIsBuyGiveClient(PayIsBuyGiveVo payIsBuyGiveVo) {
        return getPayIsBuyGive(payIsBuyGiveVo.getPayAmount(), payIsBuyGiveVo.getUserid(),
                payIsBuyGiveVo.getOrderNo(), payIsBuyGiveVo.getOrderType(), payIsBuyGiveVo.getGoodsAmount());
    }

    /**
     * 支付时获取当单是否享受买送
     *
     * @param payAmount
     * @param userId
     * @param orderNo
     * @param orderType
     * @param goodsAmount
     * @return
     */
    @Override
    public boolean getPayIsBuyGive(double payAmount, Long userId, String orderNo, Integer orderType, double goodsAmount) {
        //先判断订单类型
        log.info("获取是否享受买送入参：{}, {}, {}, {}, {}", userId, payAmount, orderNo, orderType, goodsAmount);
        if (!orderType.equals(OrderType.TEN_YUAN_SHOP) && !orderType.equals(OrderType.RECHARGE)) {
            log.info("非店铺和话费订单不享受买送活动：{}, {}", userId, orderType);
            return false;
        }
        boolean result;
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(Long.valueOf(userId));
        if (userMember != null && userMember.getActive()) {//1、准会员升级,3、购买会员

            //会员买送天数
            int day = memberConfigService.getMmjMemberFirstOrderDayLimit();
            if (org.apache.commons.lang.time.DateUtils.addDays(userMember.getBeMemberTime(), day).before(new Date())) {
                log.info("用户{}成为会员的时间已超过{}天，不享受买送", userId, day);
                return false;
            }

            //下单时间在成为会员之前的，不享受买送
            UserOrderVo userOrderVo = new UserOrderVo();
            userOrderVo.setOrderNo(orderNo);
            userOrderVo.setUserId(userId.toString());
            OrderInfo orderInfo = orderFeignClient.getAsyncOrderInfo(userOrderVo).getData();
            if (orderInfo != null && orderInfo.getCreaterTime().getTime() < userMember.getBeMemberTime().getTime()) {
                if (MemberConstant.BE_MEMBER_TYPE_ORDER.equals(userMember.getBeMemberType())
                        && !StringUtils.isEmpty(userMember.getOrderNo()) && orderNo.equals(userMember.getOrderNo())) {
                    return true;
                }
                log.info("下单时间在成为会员之前的，不享受买送,{},{}", userId, day);
                return false;
            }

            //查询是否首单
            Date memberTime;
            if (MemberConstant.BE_MEMBER_TYPE_ORDER.equals(userMember.getBeMemberType()) && !StringUtils.isEmpty(userMember.getOrderNo())) {//如果通过下单成为会员，取下单时间
                UserOrderVo memberOrderVo = new UserOrderVo();
                memberOrderVo.setUserId(userId.toString());
                memberOrderVo.setOrderNo(userMember.getOrderNo());
                OrderInfo memberOrder = orderFeignClient.getAsyncOrderInfo(memberOrderVo).getData();
                memberTime = memberOrder.getCreaterTime();
            } else {
                memberTime = userMember.getBeMemberTime();
            }
            MemberOrderVo memberOrderVo = new MemberOrderVo();
            memberOrderVo.setMemberTime(com.mmj.common.utils.DateUtils.SDF1.format(memberTime));
            memberOrderVo.setUserId(Long.valueOf(userId));
            ReturnData<List<OrderInfo>> returnData = orderFeignClient.getAsyncOrderList(memberOrderVo);
            List<OrderInfo> orderInfos = returnData.getData().size() > 0 ? returnData.getData() : new ArrayList<>();
            log.info("OrderList长度：{}", orderInfos.size());
            if (orderInfos.size() > 0) {
                for (OrderInfo o : orderInfos) {
                    log.info("OrderList订单号：{}", o.getOrderNo());
                }
            }
            result = orderInfos.size() <= 0 || (orderNo.equals(orderInfos.get(0).getOrderNo()));

        } else {//2、非会员下单成为会员
            // 查询该单是否能升级为会员
            //历史消费金额
            if (orderType.equals(OrderType.RECHARGE)) {
                log.info("话费订单满足条件,订单号：{}，", orderNo);
                result = true;
            } else {
                OrderDetailVo orderDetailVo = new OrderDetailVo();
                orderDetailVo.setOrderNo(orderNo);
                orderDetailVo.setUserId(userId.toString());
                double consumeMoney = orderFeignClient.getAsyncConsumeMoneyTwo(orderDetailVo).getData();

                //成为会员门槛
                int mcc = memberConfigService.getMmjMemberCumulativeConsumption();
                log.info("历史消费：{}，门槛：{},订单号：{}，", consumeMoney, mcc, orderNo);
                result = DoubleUtil.add(consumeMoney, payAmount) >= mcc;
            }
        }
        log.info("用户{}下单(订单类型{})是否享受买送结果：{}", userId, orderType, result);
        return result;
    }

    /**
     * 支付时记录买送资格
     */
    @Override
    public void recordQualifications(PayRecordQualificationsVo prq) {
        boolean isBuyGive = getPayIsBuyGive(prq.getOrderAmount(), prq.getUserId(), prq.getOrderNo(), prq.getOrderType(), prq.getGoodAmount());
        if (isBuyGive) {
            saveBuyGive(prq.getOrderNo(), prq.getOrderAmount(), Long.valueOf(prq.getUserId()));
            log.info("订单{}享受买送:", prq.getOrderNo());
        } else {
            log.info("订单{}不享受买送:", prq.getOrderNo());
        }
    }


    @Override
    public void saveUserMember(Long userId, String orderNo, Integer orderType, String appId, String openId, double orderAmount) {
        //成为会员
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setUserId(userId.toString());
        orderDetailVo.setOrderNo(orderNo);
        if (orderType.equals(OrderType.RECHARGE)) {
            saveUserMember(userId, orderNo, appId, openId);
        } else {
            double consumeMoney = orderFeignClient.getAsyncConsumeMoneyTwo(orderDetailVo).getData();//历史消费金额
            int mcc = memberConfigService.getMmjMemberCumulativeConsumption();
            log.info("历史消费金额:{},成为会员门槛:{},订单金额：{},订单号：{}", consumeMoney, mcc, orderAmount, orderNo);
            if ((consumeMoney + orderAmount) >= mcc) {
                saveUserMember(userId, orderNo, appId, openId);
            }
        }
    }

    public void saveUserMember(Long userId, String orderNo, String appId, String openId) {
        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(userId);
        if (userMember == null || !userMember.getActive()) {
            log.info("开始下单成为会员:{}", orderNo);
            SaveUserMemberDto saveUserMemberDto = new SaveUserMemberDto();
            saveUserMemberDto.setBeMemberType(MemberConstant.BE_MEMBER_TYPE_ORDER);
            saveUserMemberDto.setOrderNo(orderNo);
            saveUserMemberDto.setUserId(userId);
            saveUserMemberDto.setAppId(appId);
            saveUserMemberDto.setOpenId(openId);
            log.info("订单{}下单成为会员", orderNo);
            userMemberService.save(saveUserMemberDto);
        }
    }

}
