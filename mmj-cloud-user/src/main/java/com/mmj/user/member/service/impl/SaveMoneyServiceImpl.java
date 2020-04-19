package com.mmj.user.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.mmj.common.constants.OrderType;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.PriceConversion;
import com.mmj.user.common.feigin.OrderFeignClient;
import com.mmj.user.common.model.OrderInfo;
import com.mmj.user.common.model.dto.OrderGoodsDto;
import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.common.model.vo.OrderGoodVo;
import com.mmj.user.common.model.vo.UserOrderVo;
import com.mmj.user.manager.dto.UserCouponDto;
import com.mmj.user.manager.service.CouponUserService;
import com.mmj.user.manager.vo.CouponSource;
import com.mmj.user.manager.vo.OrderCouponVo;
import com.mmj.user.member.constant.SaveMoneySource;
import com.mmj.user.member.mapper.UserMemberPreferentialMapper;
import com.mmj.user.member.model.UserMember;
import com.mmj.user.member.model.UserMemberPreferential;
import com.mmj.user.member.model.Vo.PayRecordQualificationsVo;
import com.mmj.user.member.service.SaveMoneyService;
import com.mmj.user.member.service.UserMemberSendService;
import com.mmj.user.member.service.UserMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 会员节省金额服务
 */
@Service
public class SaveMoneyServiceImpl implements SaveMoneyService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserMemberPreferentialMapper userMemberPreferentialMapper;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private OrderFeignClient orderFeignClient;
    @Autowired
    private UserMemberSendService userMemberSendService;
    @Autowired
    private CouponUserService couponUserService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    public void save(SaveMoneySource source, Double money, Long userId, Long memberId, String orderNo) {
        log.info(" 写入会员省钱=> push {},{},{},{},{}", source, money, userId, memberId, orderNo);
        UserMemberPreferential userMemberPreferential = new UserMemberPreferential();
        userMemberPreferential.setType(source.toString());
        userMemberPreferential.setPreferentialAmount(money);
        userMemberPreferential.setUserId(userId);
        userMemberPreferential.setMemberId(memberId);
        userMemberPreferential.setOrderNo(orderNo);
        userMemberPreferential.setCreateTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        userMemberPreferentialMapper.insert(userMemberPreferential);
    }

    @Override
    public void saveMoney(OrderDetailVo orderDetailVo) {
        log.info("进入省钱处理:{}", orderDetailVo.toString());

        UserMember userMember = userMemberService.queryUserMemberInfoByUserId(Long.valueOf(orderDetailVo.getUserId()));
        UserOrderVo userOrderVo = new UserOrderVo();
        userOrderVo.setOrderNo(orderDetailVo.getOrderNo());
        userOrderVo.setUserId(orderDetailVo.getUserId());
        OrderInfo orderInfo = orderFeignClient.getAsyncOrderInfo(userOrderVo).getData();
        OrderGoodVo orderGoodVo = new OrderGoodVo(orderDetailVo.getOrderNo(), orderDetailVo.getUserId());

        log.info("userMember:{},orderInfo:{}", userMember, orderInfo);
        if (userMember != null && userMember.getActive() && orderInfo.getMemberOrder()) {
            if (orderInfo.getOrderType() != OrderType.RECHARGE) {
                double saveMoneyAmount = 0;
                ReturnData<List<OrderGoodsDto>> returnData = orderFeignClient.getAsyncOrderGoodList(orderGoodVo);
                List<OrderGoodsDto> orderGoodsDtos = returnData.getData().size() > 0 ? returnData.getData() : new ArrayList<>();
                log.info("计算会员省钱订单的商品是:{}", JSON.toJSONString(orderGoodsDtos));
                for (OrderGoodsDto good : orderGoodsDtos) {
                    if (Double.valueOf(good.getMemberPrice()) > 0) {
                        double memberPrice = Double.valueOf(good.getMemberPrice());
                        double unitPrice = Double.valueOf(good.getGoodAmount());
                        saveMoneyAmount += (((unitPrice / 100) * good.getGoodNum()) - ((memberPrice / 100) * good.getGoodNum()));
                    }
                }

                if (saveMoneyAmount > 0) {
                    if (Objects.isNull(userMember)) {
                        log.warn(" => 支付成功-推送省钱信息 userId:{},saveMoneyAmount:{} 获取会员信息失败", userMember.getUserId(), saveMoneyAmount);
                    } else {
                        this.save(SaveMoneySource.GOODS, saveMoneyAmount, userMember.getUserId(), userMember.getMemberId().longValue(), orderInfo.getOrderNo());
                    }
                }

                //优惠券省钱
                OrderCouponVo orderCouponVo = new OrderCouponVo();
                orderCouponVo.setOrderNo(orderInfo.getOrderNo());
                orderCouponVo.setUserId(orderInfo.getCreaterId());
                log.info("查询订单使用优惠券入参,orderNo:{},userId:{}", orderInfo.getOrderNo(), orderInfo.getCreaterId());
                List<UserCouponDto> userCouponDtos = couponUserService.myOrderCouponList(orderCouponVo);
                log.info("查询订单有没有使用优惠券长度：{}", userCouponDtos.size());
                for (UserCouponDto userCouponDto : userCouponDtos) {
                    log.info("优惠券类型:{}", userCouponDto.getCouponInfo().getActiveFlag());
                    if (null == userCouponDto.getCouponInfo().getActiveFlag())
                        continue;
                    if (!userCouponDto.getCouponInfo().getActiveFlag().equals(CouponSource.MEMBER_DAY))
                        continue;

                    //会员日领劵
                    log.info("开始计算会员省钱，优惠为:{}", userCouponDto.getCouponInfo());
                    if ("1".equals(userCouponDto.getCouponInfo().getCouponAmount())) {//优惠券
                        log.info("写入满减卷");
                        this.save(SaveMoneySource.COUPON, Double.valueOf(userCouponDto.getCouponInfo().getCouponValue()),
                                userMember.getUserId(), Long.valueOf(userMember.getMemberId()), orderInfo.getOrderNo());
                    } else if ("2".equals(userCouponDto.getCouponInfo().getCouponAmount())) {//折扣卷
                        //计算折扣值 约定好折扣值为100
                        log.info("写入折扣卷");
                        int couponValue = 100 - Integer.parseInt(userCouponDto.getCouponInfo().getCouponValue());
                        double money = orderInfo.getGoodAmount() * couponValue;
                        this.save(SaveMoneySource.COUPON, money,
                                userMember.getUserId(), Long.valueOf(userMember.getMemberId()), orderInfo.getOrderNo());
                    }
                }
                log.info("优惠券省钱统计完毕");
            }
        } else {
            userMemberSendService.saveUserMember(Long.valueOf(orderDetailVo.getUserId()), orderDetailVo.getOrderNo(), orderInfo.getOrderType(), orderDetailVo.getAppId(), orderDetailVo.getOpenId(), PriceConversion.longToDouble(orderInfo.getOrderAmount().longValue()));
        }

        //记录买送资格
        PayRecordQualificationsVo payRecordQualificationsVo = new PayRecordQualificationsVo();
        payRecordQualificationsVo.setGoodAmount(PriceConversion.longToDouble(Long.valueOf(orderInfo.getGoodAmount())));
        payRecordQualificationsVo.setOrderAmount(PriceConversion.longToDouble(Long.valueOf(orderInfo.getOrderAmount())));
        payRecordQualificationsVo.setOrderNo(orderInfo.getOrderNo());
        payRecordQualificationsVo.setOrderType(orderInfo.getOrderType());
        payRecordQualificationsVo.setUserId(orderInfo.getCreaterId());
        userMemberSendService.recordQualifications(payRecordQualificationsVo);
        log.info("记录会员省钱回调完成");

        //删除会员正在升级标识
        redisTemplate.delete(UserConstant.ISMEMBERONGOING + orderInfo.getCreaterId());
        log.info("删除会员正在处理标识成功:{},{}" + orderInfo.getCreaterId(), orderInfo.getOrderNo());
    }
}
