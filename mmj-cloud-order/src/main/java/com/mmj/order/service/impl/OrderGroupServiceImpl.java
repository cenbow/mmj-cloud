package com.mmj.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.BaseUser;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.order.common.feign.ActiveFeignClient;
import com.mmj.order.common.feign.UserFeignClient;
import com.mmj.order.common.model.ActiveGood;
import com.mmj.order.common.model.LotteryConf;
import com.mmj.order.common.model.RelayInfo;
import com.mmj.order.common.model.UserActive;
import com.mmj.order.common.model.dto.OrderCheckDto;
import com.mmj.order.common.model.dto.OrderPaySuccessDto;
import com.mmj.order.common.model.vo.RedPackageUserVo;
import com.mmj.order.constant.OrderGroupStatus;
import com.mmj.order.constant.OrderGroupType;
import com.mmj.order.constant.OrderType;
import com.mmj.order.constant.RedPackageType;
import com.mmj.order.mapper.OrderGroupMapper;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderGroup;
import com.mmj.order.model.OrderGroupJoin;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.dto.*;
import com.mmj.order.model.vo.OrderSaveVo;
import com.mmj.order.model.vo.UpdateStatusVo;
import com.mmj.order.service.*;
import com.mmj.order.utils.*;
import com.xiaoleilu.hutool.lang.Assert;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 订单团信息表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-11
 */
@Service
public class OrderGroupServiceImpl extends ServiceImpl<OrderGroupMapper, OrderGroup> implements OrderGroupService {

    private final static Logger logger = LoggerFactory.getLogger(OrderGroupServiceImpl.class);

    private static String CACHE_KEY = "LOTTERY_RED_PACKET:";

    @Autowired
    private OrderGroupMapper orderGroupMapper;

    @Autowired
    private OrderGroupJoinService orderGroupJoinService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private OrderGoodService orderGoodService;

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private LotteryCodeService orderLotteryCodeService;

    @Autowired
    private MQProducer mQProducer;

    @Autowired
    private MessageUtils messageUtils;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCheckDto checkGroupOrder(OrderSaveVo orderSaveVo, Long userId) {
        if (OrderType.LOTTERY == orderSaveVo.getOrderType()) {
            //抽奖
            Object o = redisTemplate.opsForValue().get(CommonConstant.LOTTERY_CACHE_KEY + orderSaveVo.getBusinessId());
            LotteryConf conf;
            if (null == o) {
                conf = activeFeignClient.getLotteryById(orderSaveVo.getBusinessId());
            } else {
                conf = JSON.parseObject(o.toString(), LotteryConf.class);
            }
            org.springframework.util.Assert.notNull(conf, "抽奖活动不存在");
            org.springframework.util.Assert.isTrue(conf.getEndTime().after(new Date()), "活动已过期");

            int maxEveryOne = StringUtils.isBlank(conf.getMaxEveryone()) ? 1 : Integer.parseInt(conf.getMaxEveryone());
            String key = CommonConstant.LOTTERY_JOIN_COUNT_PREFIX + orderSaveVo.getBusinessId() + ":" + userId;
            Object object = redisTemplate.opsForValue().get(key);
            int joined = null == object ? 0 : Integer.parseInt(object.toString());

            if (joined >= maxEveryOne) {
                OrderInfo info = new OrderInfo();
                info.setBusinessId(orderSaveVo.getBusinessId());
                info.setOrderStatus(OrderStatus.PENDING_PAYMENT.getStatus());
                info.setCreaterId(userId);
                EntityWrapper<OrderInfo> wrapper = new EntityWrapper<>(info);
                int waitPay = orderInfoService.selectCount(wrapper);
                //不能再参加了
                org.springframework.util.Assert.isTrue(waitPay == 0, "已存在该活动未支付订单");
                org.springframework.util.Assert.isTrue(false, "已达最大参与次数");
            }

            PassingDataDto dto = PassingDataUtil.disPassingData(orderSaveVo.getPassingData());
            org.springframework.util.Assert.notNull(dto, "passDate参数异常");
            boolean isLan = false;
            if (null == dto.getGroupNo())
                isLan = true;

            if (isLan) {
                //支付人数校验
                String payKey = CommonConstant.LOTTERY_PAY_COUNT_PREFIX + orderSaveVo.getBusinessId();
                int payCnt = 0;
                Object cnt = redisTemplate.opsForValue().get(payKey);
                if (cnt != null)
                    payCnt = Integer.parseInt(cnt.toString());
                //计算抽奖活动有多少个抽奖码
                int start = Integer.parseInt(conf.getLotteryCodeStart());
                int end = Integer.parseInt(conf.getLotteryCodeEnd());
                int codeCnt = end - start + 1;
                logger.info("活动Id:{} 抽奖码数量:{} 已经支付的数量:{}", conf.getLotteryId(), codeCnt, payCnt);
                org.springframework.util.Assert.isTrue(codeCnt >= payCnt, "抽奖已达到最大参与人数");
            }
            return new OrderCheckDto(isLan, conf.getEndTime());
        } else if (OrderType.TWO_GROUP == orderSaveVo.getOrderType()) {
            PassingDataDto dto = PassingDataUtil.disPassingData(orderSaveVo.getPassingData());
            org.springframework.util.Assert.notNull(dto, "passDate参数异常");

            boolean isLan = false;
            if (null == dto.getGroupNo())
                isLan = true;

            if (isLan)
                return new OrderCheckDto(isLan, new Date(Instant.now().toEpochMilli() + 86400000));

            OrderGroup group = this.getByGroupNo(dto.getGroupNo());
            if (null == group)
                return new OrderCheckDto(true, new Date(Instant.now().toEpochMilli() + 86400000));

            org.springframework.util.Assert.isTrue(group.getExpireDate().after(new Date()), "该团已过期");
            OrderInfo orderInfo = orderInfoService.getByOrderNo(group.getLaunchOrderNo());
            if (Objects.isNull(orderInfo)) {
                return new OrderCheckDto(true, group.getExpireDate());
            } else { //拼友下单，判断拼主下单时是否会员
                return new OrderCheckDto(false, orderInfo.getMemberOrder(), group.getExpireDate());
            }
        }
        return null;
    }

    private static final List<String> RANDOM_USER_PIC = Lists.newArrayListWithCapacity(10);

    @Override
    public GroupInfoDto getGroupInfo(Long userid, String groupNo, String orderNo) {
        Assert.isTrue(userid != null, "缺少用户标识");
        Assert.isTrue(StringUtils.isNotBlank(groupNo), "缺少拼团号");
//        boolean lotteryGroup = isLotteryGroup(groupNo);
//        if (lotteryGroup) { //抽奖
//            GroupInfoDto groupInfoDto = lotteryGroupNumberService.getGroupInfo(userid, groupNo, orderNo);
//            if (Objects.nonNull(groupInfoDto)) {
//                //获取商品信息
//                List<OrderGoods> orderGoods = orderService.getOrderGoods(orderNo, null);
//                if (orderGoods.size() > 0) {
//                    groupInfoDto.setGoods(orderService.orderGoodsToDto(orderGoods.get(0)));
//                    Goodsbase goodsbase = goodsBaseService.getGoodsByIdWin(orderGoods.get(0).getGoodsId(), "2");
//                    groupInfoDto.getGoods().setGoodsshortname(goodsbase.getGoodsshortname());
//                    groupInfoDto.setGoodsbase(goodsbase);
//                }
//            }
//            return groupInfoDto;
//        } else {
        OrderGroup orderGroup = getOrderGroup(groupNo);
        GroupInfoDto groupInfoDto = new GroupInfoDto();
        groupInfoDto.setUserLevel(orderGroup.getLaunchUserId().equals(userid) ? 1 : 0); //是否拼主
        groupInfoDto.setOrderNo(orderGroup.getLaunchUserId().equals(userid) ? orderGroup.getLaunchOrderNo() : null);
        OrderGroupDto orderGroupDto = new OrderGroupDto();
        orderGroupDto.setGroupNo(orderGroup.getGroupNo());
        orderGroupDto.setGroupType(orderGroup.getGroupType());
        orderGroupDto.setGroupStatus(orderGroup.getGroupStatus());
        orderGroupDto.setGroupPeople(orderGroup.getGroupPeople());
        orderGroupDto.setCurrentPeople(orderGroup.getCurrentPeople());
        orderGroupDto.setCreateDate(orderGroup.getCreaterTime());
        orderGroupDto.setUpdateDate(orderGroup.getModifyTime());
        orderGroupDto.setExpireDate(orderGroup.getExpireDate());
        if (orderGroupDto.getGroupStatus() == OrderGroupStatus.JOINING.getStatus() && orderGroupDto.getExpireTime() <= 0) {
            orderGroupDto.setGroupStatus(OrderGroupStatus.EXPIRE.getStatus());
        }
//            orderGroupDto.setPassingData(orderGroup.getPassingData());
        List<OrderGroupJoin> orderGroupJoins = getOrderGroupRelationsByGroupNo(orderGroup.getGroupNo());
        List<OrderGroupDto.Member> members = Lists.newArrayListWithCapacity(orderGroupJoins.size());
        orderGroupJoins.stream().forEach(relations -> {
            if (relations.getGroupMain().equals(1)) {
                BaseUser baseUser = userFeignClient.getByById(relations.getJoinUserId());
                orderGroupDto.setLauncher(getMember(baseUser));
            } else {
                if (relations.getJoinUserId().equals(userid)) {
                    groupInfoDto.setUserLevel(2);
                    groupInfoDto.setOrderNo(relations.getJoinOrderNo());//参团订单号
                }
                BaseUser baseUser = userFeignClient.getByById(relations.getJoinUserId());
                members.add(getMember(baseUser));
            }
        });
        if (orderGroupDto.getGroupStatus() == OrderGroupStatus.COMPLETED.getStatus() && orderGroupDto.getCurrentPeople() < orderGroupDto.getGroupPeople()) {
            int randomNum = RandomUtils.nextInt(9);
            String pic_url = RANDOM_USER_PIC.get(randomNum);
            members.add(new OrderGroupDto.Member(0l, pic_url, randomNum + "", randomNum + "", false));
        }
        orderGroupDto.setMembers(members);
        groupInfoDto.setGroup(orderGroupDto);

        orderNo = StringUtils.isNotBlank(orderNo) ? orderNo : orderGroup.getLaunchOrderNo();
        //获取商品信息
//            List<OrderGood> orderGoods = orderService.getOrderGoods(orderNo, null);
//            if (orderGoods.size() > 0) {
//                OrderGoodsDto orderGoodsDto = orderService.orderGoodsToDto(orderGoods.get(0));
//                if (orderGroupDto.getLauncher().isVip()) {
//                    //TODO 需要获取会员价格 (只针对二人团)
//                    orderGoodsDto.setUnitPrice("8.88");
//                } else {
//                    //TODO 获取普通商品价格 (只针对二人团)
//                    orderGoodsDto.setUnitPrice("10");
//                }
//                groupInfoDto.setGoods(orderService.orderGoodsToDto(orderGoods.get(0)));
//                if (orderNo.endsWith("32")) {
//                    groupInfoDto.setGoodsbase(goodsBaseService.getGoodsByIdWin(orderGoods.get(0).getGoodsId(), "7"));
//                } else {
//                    groupInfoDto.setGoodsbase(goodsBaseService.getGoodsByIdWin(orderGoods.get(0).getGoodsId(), "1"));
//                }
//
//            }
        return groupInfoDto;
//        }
    }

    /**
     * 二人团
     *
     * @param orderInfo
     */
    private PayResultDto payTwoGroup(OrderInfo orderInfo) {
        PassingDataDto passingDataDto = PassingDataUtil.disPassingData(orderInfo.getPassingData());
        if (Objects.nonNull(passingDataDto) && StringUtils.isNotBlank(passingDataDto.getGroupNo())) { //拼友
            OrderGroup orderGroup = getOrderGroup(passingDataDto.getGroupNo());
            OrderGroupJoin orderGroupJoin = createGroupJoin(orderGroup, orderGroup.getLaunchOrderNo(), orderGroup.getCreaterId(), 0, orderGroup.getGroupType(), orderInfo);
            OrderGroup updateOrderGroup = new OrderGroup();
            updateOrderGroup.setGroupId(orderGroup.getGroupId());
            updateOrderGroup.setCurrentPeople(orderGroup.getCurrentPeople() + 1);
            updateOrderGroup.setGroupStatus(OrderGroupStatus.COMPLETED.getStatus());
            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, orderGroup.getGroupNo());
            boolean result = updateById(updateOrderGroup);
            Assert.isTrue(result, "加入二人团失败");
            //拼主订单上传聚水潭
            orderInfoService.toBeDelivered(orderGroup.getLaunchOrderNo(), orderInfo.getOrderNo());

            //发送成团模板消息
            List<OrderGood> list = orderGoodService.selectByOrderNo(orderGroup.getLaunchOrderNo());
            if (list != null && list.size() > 0) {
                OrderPaySuccessDto paySuccessDto = new OrderPaySuccessDto(orderGroup.getLaunchUserId(),
                        orderGroup.getLaunchOrderNo(), list.get(0).getGoodName());
                paySuccessDto.setAmount(PriceConversion.intToString(orderInfo.getOrderAmount()));
                paySuccessDto.setGroupNo(orderGroup.getGroupNo());
                messageUtils.sendTwoGroupedMsg(paySuccessDto, list.get(0).getGoodAmount(),
                        list.get(0).getDiscountAmount());
            }
        } else { //拼主
            OrderGroup orderGroup = createOrderGroup(OrderGroupType.TWO_GROUP.getType(), 2, orderInfo, "二人团");
            OrderGroupJoin orderGroupJoin = createGroupJoin(orderGroup, orderGroup.getLaunchOrderNo(), orderGroup.getCreaterId(), 1, orderGroup.getGroupType(), orderInfo);
            //订单改成待分享
            orderInfoService.pendingSharing(orderInfo.getOrderNo());
        }
        return null;
    }

    /**
     * 新人团
     * <p>
     * 接力购
     *
     * @param orderInfo
     * @return
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public PayResultDto payNewComers(OrderInfo orderInfo) {
        if (orderInfo.getOrderStatus() > 1) {
            return null;
        }
        //新人团支付
        PassingDataDto passingDataDto = PassingDataUtil.disPassingData(orderInfo.getPassingData());
        String groupNo = null;
        boolean isLauncher = false;
        try {
            //查询商品属性上的拼团所需人数
            List<OrderGoodsDto> ogList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo(), orderInfo.getCreaterId());

            Assert.isTrue(null != ogList && ogList.size() > 0, "查询不到订单对应的商品,订单号是:" + orderInfo.getOrderNo());

            ActiveGood activeGood = new ActiveGood();
            activeGood.setGoodId(ogList.get(0).getGoodId());
            List<ActiveGood> activeGoods = activeFeignClient.queryDetail(activeGood).getData();
            activeGood = activeGoods.isEmpty() ? null : activeGoods.get(0);

            activeGood.setGroupPerson(activeGood.getGroupPerson() + 1);

            //GoodsbaseSpecial special = specialService.getSpecialByGoodId(gb.getGoodsbaseid());

            logger.info("商品信息,{}", JSONObject.toJSONString(activeGood));
            if (Objects.isNull(passingDataDto)) { //拼主
                OrderGroup orderGroup = createOrderGroup(OrderGroupType.NEWCOMERS.getType(), activeGood.getGroupPerson(), orderInfo, "新人团");
                createGroupJoin(orderGroup, orderInfo.getOrderNo(), orderInfo.getCreaterId(), 1, OrderGroupType.NEWCOMERS.getType(), orderInfo);
                groupNo = orderGroup.getGroupNo();
                //订单状态待成团
                orderInfoService.pendingSharing(orderInfo.getOrderNo());
                isLauncher = true;
            } else { //拼友
                groupNo = passingDataDto.getGroupNo();
                OrderGroup orderGroup = getOrderGroup(groupNo);
                boolean joinStatus = false; //是否加入成功
                if (orderGroup.getGroupStatus() == OrderGroupStatus.JOINING.getStatus()) { //可以加入团
                    logger.info("拼友加入团");
                    createGroupJoin(orderGroup, orderInfo.getOrderNo(), orderInfo.getCreaterId(), 1, OrderGroupType.NEWCOMERS.getType(), orderInfo);
                    // 判断团主拉的团员是否足够
                    if (orderGroup.getCurrentPeople() + 1 >= orderGroup.getGroupPeople()) {
                        //拼团成功
                        //拼主订单更改状态
                        orderInfoService.toBeDelivered(orderGroup.getLaunchOrderNo());

                        //团主成为会员
                        OrderInfo ordersLaunch = orderInfoService.getByOrderNo(orderGroup.getLaunchOrderNo());
                        userFeignClient.saveUserMember(ordersLaunch);

//                        //推送聚水潭
                        orderInfoService.updateOrderInfo(new UpdateStatusVo(orderGroup.getLaunchOrderNo(), orderGroup.getCreaterId(), OrderStatus.TO_BE_DELIVERED.getStatus()));
//                        //TODO 发送拼团成功再买一单模板消息(拼主)
//                        buyBackQualificationService.sendTempMsg(orderGroup.getCreateBy(), orderGroup.getLaunchOrderNo());
                        //团友重新开团做团主
                        OrderGroup newOrderGroup = createOrderGroup(OrderGroupType.NEWCOMERS.getType(), activeGood.getGroupPerson(), orderInfo, "新人团");
                        createGroupJoin(newOrderGroup, orderInfo.getOrderNo(), orderInfo.getCreaterId(), 1, OrderGroupType.NEWCOMERS.getType(), orderInfo);
                        groupNo = newOrderGroup.getGroupNo();

                        //订单状态待成团
                        orderInfoService.pendingSharing(orderInfo.getOrderNo());
                    } else {
                        //待成团时，团友重新开团做团主
                        OrderGroup newOrderGroup = createOrderGroup(OrderGroupType.NEWCOMERS.getType(), activeGood.getGroupPerson(), orderInfo, "新人团");
                        createGroupJoin(newOrderGroup, orderInfo.getOrderNo(), orderInfo.getCreaterId(), 1, OrderGroupType.NEWCOMERS.getType(), orderInfo);
                        groupNo = newOrderGroup.getGroupNo();
                        //订单状态待成团
                        orderInfoService.pendingSharing(orderInfo.getOrderNo());
                    }
                }
                logger.info("拼友加入结果,{}", joinStatus);
            }
            logger.info("最终groupNo:{}", groupNo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }
        return null;
    }


    @Autowired
    private SMSProcessor smsProcessor;

    @Override
    public void payOrder(OrderInfo orderInfo) {
        PayResultDto resultDto = null;
        if (orderInfo.getOrderType().equals(OrderType.LOTTERY)) {
            //抽奖
            resultDto = payLottery(orderInfo);
            if (null == resultDto)
                return;
            SMSInfoDto infoDto = new SMSInfoDto(orderInfo.getCreaterId(),
                    orderInfo.getOrderNo(), resultDto.getGoodsTitle());

            smsProcessor.sendLotteryPaySMS(infoDto);
        } else if (orderInfo.getOrderType().equals(OrderType.RELAY_LOTTERY)) {
            //接力购抽奖
            resultDto = payRelayLottery(orderInfo);
        } else if (orderInfo.getOrderType().equals(OrderType.TWO_GROUP)) {
            //二人团
            resultDto = payTwoGroup(orderInfo);
            if (null == resultDto)
                return;
            SMSInfoDto infoDto = new SMSInfoDto(orderInfo.getCreaterId(),
                    orderInfo.getOrderNo(), resultDto.getGoodsTitle());
            smsProcessor.sendTwoGroupPaySMS(infoDto);
        } else if (orderInfo.getOrderType().equals(OrderType.NEWCOMERS)) {
            //接力购
            resultDto = payNewComers(orderInfo);
        } else {
            logger.error("拼团订单未匹配到对应的类型");
            return;
        }
        if (null == resultDto)
            return;

        messageUtils.payGroup(orderInfo.getOrderType(), new OrderPaySuccessDto(orderInfo.getCreaterId(),
                orderInfo.getOrderNo(), orderInfo.getOrderSource(),
                PriceConversion.intToString(orderInfo.getOrderAmount()),
                resultDto.getGoodsTitle(), resultDto.getGroupNo(), resultDto.isGroupStatus()));
    }

    @Override
    public List<Map<String, Object>> getRedPackList() {
        OrderGroup group = new OrderGroup();
        group.setGroupType(OrderGroupType.FREE_ORDER.getType());
        EntityWrapper<OrderGroup> wrapper = new EntityWrapper<>(group);
        List<OrderGroup> list = selectList(wrapper);
        if (null == list || list.size() == 0)
            return null;

        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderGroup orderGroup : list) {
            if (null == orderGroup.getLaunchUserId())
                continue;
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderGroup.getCreaterId());
            OrderInfo orderInfo = orderInfoService.getByOrderNo(orderGroup.getLaunchOrderNo());
            if (null == orderInfo)
                continue;
            int money = orderInfo.getOrderAmount().intValue() / 100;

            BaseUser baseUser = userFeignClient.getByById(orderGroup.getLaunchUserId());
            if (null == baseUser)
                continue;
            Map<String, Object> map = Maps.newHashMapWithExpectedSize(2);
            map.put("nickname", baseUser.getUserFullName());
            map.put("money", money);
            result.add(map);
        }
        return result;
    }

    @Override
    public OrderGroup getByOrderNo(String orderNo) {
        OrderGroup group = new OrderGroup();
        group.setLaunchOrderNo(orderNo);
        EntityWrapper<OrderGroup> wrapper = new EntityWrapper<>(group);
        return selectOne(wrapper);
    }

    @Override
    public OrderGroup getByGroupNo(String groupNo) {
        OrderGroup group = new OrderGroup();
        group.setGroupNo(groupNo);
        EntityWrapper<OrderGroup> wrapper = new EntityWrapper<>(group);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, groupNo);
        return selectOne(wrapper);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public PayResultDto payLottery(OrderInfo orderInfo) {
        PayResultDto payResult = new PayResultDto();
        //抽奖订单支付
        List<OrderGoodsDto> orderGoodsDtoList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo(), orderInfo.getCreaterId());
        if (null == orderGoodsDtoList || orderGoodsDtoList.size() == 0)
            return payResult;
        OrderGoodsDto dto = orderGoodsDtoList.get(0);
        payResult.setGoodsTitle(dto.getGoodTitle());
        Integer actId = orderInfo.getBusinessId(); //活动id
        logger.info("查询到抽奖活动id是:{}", actId);
        LotteryConf lc = activeFeignClient.getLotteryById(actId);
        logger.info("查询活动结果:{}", lc);
        if (null == lc)
            return payResult;
        String payKey = CommonConstant.LOTTERY_PAY_COUNT_PREFIX + actId;
        redisTemplate.opsForValue().increment(payKey, 1);
        boolean isLan = false;
        PassingDataDto passingDataDto = PassingDataUtil.disPassingData(orderInfo.getPassingData());

        if (null == passingDataDto || StringUtils.isBlank(passingDataDto.getGroupNo())) {
            isLan = true;
        }
        //订单改成待分享状态
        orderInfoService.pendingSharing(orderInfo.getOrderNo());
        if (isLan) { //拼主
            OrderGroup orderGroup = lotteryCreate(lc, orderInfo);
            if (orderGroup != null) {
                payResult.setGroupNo(orderGroup.getGroupNo());
                //用于发送待成团模板消息
                mQProducer.orderWaiteGroupMsg(orderInfo.getOrderNo(), orderGroup.getGroupNo());
            }

            payResult.setGroupStatus(false);
        } else {
            //获取团主订单号
            OrderGroup group = lotteryJoin(lc, orderInfo, dto.getGoodTitle());
            payResult.setGroupNo(passingDataDto.getGroupNo());
            if (null != group)
                payResult.setGroupStatus(group.getGroupStatus() == 1);

        }
        payResult.setLauncher(isLan);
        return payResult;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.NESTED)
    public PayResultDto payRelayLottery(OrderInfo orders) {
        //接力购抽奖支付回调
        logger.info("接力购抽奖回调入参：{}", orders.toString());

        if (orders.getOrderStatus() > 1) {
//            return new PayResultDto();
        }

        boolean isLauncher = false;
        String groupNumber = null;
        try {
            //查询接力购抽奖活动配置拼团所需人数
            //根据订单查询活动
            RelayInfo relayInfo = activeFeignClient.getRelayInfo(orders.getBusinessId());
            Assert.isTrue(relayInfo != null, "接力购抽奖，活动信息配置有误,订单号是:" + orders.getOrderNo());

            //订单改成待分享
            orderInfoService.pendingSharing(orders.getOrderNo());
            //  todo  null == orders.getGroupNumber()  缺少 groupNumber 字段
            if (true) { //拼主
                OrderGroup orderGroup = createOrderGroup(OrderGroupType.RELAY_LOTTERY.getType(), relayInfo.getRelayNum() + 1, orders, "接力购抽奖");
                createGroupJoin(orderGroup, orders.getOrderNo(), orders.getCreaterId(), 1, OrderGroupType.RELAY_LOTTERY.getType(), orders);
                groupNumber = orderGroup.getGroupNo();
                //同步接力购抽奖团信息
//                Map<String, Object> map = new HashMap<>();
//                map.put("configureSublistId", relayInfo.getRelayId());//活动id
//                map.put("groupNo", groupNo);
//                map.put("orderNo", orders.getOrderNo());
//                map.put("status", GroupJoinType.JOINING);
//                map.put("userId", orders.getCreateBy());
//                relayLotteryGroupService.synchronizationGroup(map);
                isLauncher = true;
            } else { //拼友
                // todo 缺少字段

//                groupNumber = orders.getGroupNumber();

                OrderGroup orderGroup = getOrderGroup(groupNumber);
                if (orderGroup.getGroupStatus() == OrderGroupStatus.JOINING.getStatus()) { //可以加入团
                    logger.info("接力购抽奖拼友入团{}：", orders.getOrderNo());
                    createGroupJoin(orderGroup, orders.getOrderNo(), orders.getCreaterId(), 1, OrderGroupType.RELAY_LOTTERY.getType(), orders);
                    // 判断团主拉的团员是否足够
                    if (orderGroup.getCurrentPeople() + 1 >= orderGroup.getGroupPeople()) {
                        //拼团成功
                        //随机分配抽奖码
//                        RelayLotteryCode rlc = new RelayLotteryCode();
//                        rlc.setLotterySublistId(actId);
//                        rlc.setRelayUserid(0L);
//                        List<RelayLotteryCode> list = relayLotteryCodeService.getAllRelayLotteryCode(rlc);

                        //如果抽奖码用完了，重新生成新的
//                        if (list.size() <= 0) {
//                            //获取最大抽奖码
//                            int startCode = 0;
//                            String winCodes = relayLotteryCodeService.getMaxWinCode(configureSublist.getId());
//                            if (!"".equals(winCodes)) {
//                                String[] winCode = winCodes.split("MMJ-");
//                                startCode = Integer.parseInt(winCode[1]) + 1;
//                            }
//                            relayLotteryCodeService.addRelayLotteryCode(startCode, configureSublist.getRunLotteryNumber(), configureSublist.getId());
//                            list = relayLotteryCodeService.getAllRelayLotteryCode(rlc);
//                        }

//                        RelayLotteryCode relayLotteryCode = list.get(genRandom(list.size()));
//                        relayLotteryCode.setRelayUserid(orderGroup.getCreateBy());
//                        relayLotteryCode.setGroupNo(orderGroup.getGroupNo());
//                        relayLotteryCodeService.updateByPrimaryKeySelective(relayLotteryCode);

                        //TODO 更新用户参与表抽奖码

                        //团友重新开团做团主
                        OrderGroup newOrderGroup = createOrderGroup(OrderGroupType.RELAY_LOTTERY.getType(), relayInfo.getRelayNum() + 1, orders, "接力购抽奖");
                        createGroupJoin(newOrderGroup, orders.getOrderNo(), orders.getCreaterId(), 1, OrderGroupType.RELAY_LOTTERY.getType(), orders);
                        groupNumber = newOrderGroup.getGroupNo();
                    } else {
                        //待成团时，团友重新开团做团主
                        OrderGroup newOrderGroup = createOrderGroup(OrderGroupType.RELAY_LOTTERY.getType(), relayInfo.getRelayNum() + 1, orders, "接力购抽奖");
                        createGroupJoin(newOrderGroup, orders.getOrderNo(), orders.getCreaterId(), 1, OrderGroupType.RELAY_LOTTERY.getType(), orders);
                        groupNumber = newOrderGroup.getGroupNo();
                    }
                }
                logger.info("接力购抽奖拼友加入结果:{}", orders.getOrderNo());
            }
            logger.info("接力购抽奖最终groupNo:{}，orderNo", groupNumber, orders.getOrderNo());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        return null;
    }


    private OrderGroup lotteryCreate(LotteryConf lc, OrderInfo info) {
        //创建团
        OrderGroup orderGroup = createOrderGroup(OrderGroupType.LOTTERY.getType(),
                lc.getTuanBuildNum(), info, "拼团抽奖");
        //创建团关系
        createGroupJoin(orderGroup, info.getOrderNo(), info.getCreaterId(),
                1, OrderGroupType.LOTTERY.getType(), info);
        return orderGroup;
    }

    private OrderGroup lotteryJoin(LotteryConf lc, OrderInfo info, String goodName) {
        //判断团人数,成团之后分配抽奖码
        PassingDataDto pdd = PassingDataUtil.disPassingData(info.getPassingData());
        if (null == pdd) {
            logger.info("订单passDate解析失败,orderNo:{}", info.getOrderNo());
            return null;
        }
        String lanGroupNo = pdd.getGroupNo();

        OrderGroup group = getByGroupNo(lanGroupNo);

        if (group.getCurrentPeople() + 1 > group.getGroupPeople()) {
            //团满,开新团
            PassingDataDto passingDataDto = PassingDataUtil.disPassingData(info.getPassingData());
            if (null != passingDataDto) {
                Integer actId = passingDataDto.getActiveId();
                passingDataDto = new PassingDataDto();
                passingDataDto.setActiveId(actId);
                info.setPassingData(JSON.toJSONString(passingDataDto));//清除旧的团号
            }
            logger.info("团已满，重新开团,订单:{}", info);
            group = this.lotteryCreate(lc, info);
        } else if (group.getCurrentPeople() + 1 == group.getGroupPeople()) {
            logger.info("抽奖成团,订单:{}", info);
            //刚好成团
            //创建拼友记录
            createGroupJoin(group, group.getLaunchOrderNo(), group.getLaunchUserId(),
                    0, OrderGroupType.LOTTERY.getType(), info);

            //修改团状态为待开奖
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, group.getLaunchUserId());
            OrderGroup og = new OrderGroup();
            og.setGroupId(group.getGroupId());
            og.setModifyTime(new Date());
            og.setGroupStatus(OrderGroupStatus.COMPLETED.getStatus());
            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, group.getGroupNo());
            orderGroupMapper.updateById(og);
            group.setGroupStatus(OrderGroupStatus.COMPLETED.getStatus());

            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, lanGroupNo);
            List<OrderGroupJoin> joins = orderGroupJoinService.getListByGroupNo(lanGroupNo);
            //分配抽奖码
            List<UserActive> codeList = new ArrayList<>();
            String data[] = new String[group.getGroupPeople()];
            int index = 0;
            for (OrderGroupJoin join : joins) {
                UserActive code = new UserActive();
                code.setUserId(join.getJoinUserId());
                code.setActiveType(1);
                code.setBusinessId(join.getBusinessId());
                code.setOrderNo(join.getJoinOrderNo());
                code.setLotteryCode(orderLotteryCodeService.genLotteryCode(join.getBusinessId()));
                code.setCreaterTime(new Date());
                codeList.add(code);
                data[index] = join.getJoinOrderNo();
                index++;
                SMSInfoDto infoDto = new SMSInfoDto(join.getJoinUserId(),
                        join.getJoinOrderNo(), goodName);
                smsProcessor.sendLotteryGroupedSMS(infoDto);

                //给所有人发送模板消息
                messageUtils.sendGroupedMsg(join.getJoinUserId(), lc.getLotteryName(), goodName
                        , lc.getTuanBuildNum(), code.getLotteryCode(), join.getJoinOrderNo());
            }
            //修改订单状态未待开奖
            orderInfoService.toBeAwarded(data);

            mQProducer.sendLotteryCode(codeList);

            if (lc.getTzRondHb() != 1)
                return group;

            CACHE_KEY = CACHE_KEY + group.getLaunchUserId() + ":" + lc.getLotteryId();
            long diff = ((lc.getEndTime().getTime() - new Date().getTime()) / (1000 * 3600)) + 1;
            Object o = redisTemplate.opsForValue().get(CACHE_KEY);
            if (null != o)
                return group;
            logger.info("给团主发红包,活动ID:{},用户ID:{}", lc.getLotteryId(), group.getLaunchUserId());
            BaseUser user = userFeignClient.getByById(group.getLaunchUserId());
            logger.info("查询团长信息:{}", user);
            if (null == user)
                return group;
            //成团，发红包
            RedPackageUserVo packageUserVo = new RedPackageUserVo();
            logger.info("抽奖红包范围,start:{},end:{}", lc.getHbStart(), lc.getHbEnd());
            if (null == lc.getHbStart())
                lc.setHbStart(new BigDecimal(0.3));
            if (null == lc.getHbEnd())
                lc.setHbEnd(new BigDecimal(0.3));
            packageUserVo.setPackageAmount(RedPackCodeUtils.genRandom(lc.getHbStart().multiply(BigDecimal.valueOf(100)).intValue(), lc.getHbEnd().multiply(BigDecimal.valueOf(100)).intValue()));
            packageUserVo.setPackageCode(RedPackCodeUtils.genLotteryRedPackCode());
            packageUserVo.setActiveType(RedPackageType.LOTTERY.getType());
            packageUserVo.setPackageSource(RedPackageType.getNameByType(RedPackageType.LOTTERY.getType()));
            packageUserVo.setUnionId(user.getUnionId());
            packageUserVo.setPackageStatus(0);
            packageUserVo.setOrderNo(group.getLaunchOrderNo());
            packageUserVo.setUserId(group.getLaunchUserId());
            packageUserVo.setBusinessId(group.getBusinessId());
            try {
                boolean bool = userFeignClient.addRedPackage(packageUserVo);
                logger.info("抽奖活动红包结果:{}, {}", bool, packageUserVo);
                //活动结束后 ,key就没有意义了,可以删除
                redisTemplate.opsForValue().set(CACHE_KEY, "o", diff, TimeUnit.HOURS);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            //创建团关系
            createGroupJoin(group, group.getLaunchOrderNo(), group.getLaunchUserId(),
                    0, OrderGroupType.LOTTERY.getType(), info);
        }
        return group;
    }


    private void sendGroupedSMS() {

    }

    /**
     * 创建团
     *
     * @param groupType
     * @param groupPeople
     * @param info
     * @param remark
     * @return
     */
    private OrderGroup createOrderGroup(int groupType, int groupPeople, OrderInfo info, String remark) {
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setBusinessId(info.getBusinessId());
        orderGroup.setCreaterId(info.getCreaterId());
        orderGroup.setCreaterTime(new Date());
        orderGroup.setGroupPeople(groupPeople);
        orderGroup.setExpireDate(info.getExpirtTime());
        orderGroup.setCurrentPeople(1);
        orderGroup.setGroupType(groupType);
        orderGroup.setDeleteFlag(1);
        orderGroup.setPassingData(info.getPassingData());
        orderGroup.setGroupStatus(OrderGroupStatus.JOINING.getStatus());
        orderGroup.setLaunchOrderNo(info.getOrderNo());
        orderGroup.setLaunchUserId(info.getCreaterId());
        PassingDataDto passingData = PassingDataUtil.disPassingData(info.getPassingData());
        if (null != passingData && StringUtils.isNotEmpty(passingData.getBindGroupNo())) {
            orderGroup.setGroupNo(passingData.getBindGroupNo());
        } else {
            String gn = OrderGroupUtils.genGroupNo(info.getCreaterId());
            logger.info("从订单未获取到团号,从新生成,orderNo:{},groupNo:{}", info.getOrderNo(), gn);
            orderGroup.setGroupNo(gn);
            if (null != passingData) {
                OrderInfo oi = new OrderInfo();
                oi.setOrderId(info.getOrderId());
                passingData.setGroupNo(gn);
                oi.setPassingData(JSON.toJSONString(passingData));
                orderInfoService.updateById(oi);
            }
        }
        orderGroup.setGroupDesc(remark);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, orderGroup.getGroupNo());
        orderGroupMapper.insert(orderGroup);
        return orderGroup;
    }

    /**
     * 创建团关系
     *
     * @param group
     * @param launchOrderNo
     * @param launchUserId
     * @param isMain
     * @param activeType
     * @param info
     */
    private OrderGroupJoin createGroupJoin(OrderGroup group,
                                           String launchOrderNo, Long launchUserId, int isMain, int activeType, OrderInfo info) {
        if (0 == isMain) {
            //更新团人数
            OrderGroup og = new OrderGroup();
            og.setGroupId(group.getGroupId());
            og.setCurrentPeople(group.getCurrentPeople() + 1);
            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, group.getGroupNo());
            orderGroupMapper.updateById(og);
        }
        OrderGroupJoin join = new OrderGroupJoin();
        join.setActiveType(activeType);
        join.setBusinessId(info.getBusinessId());
        join.setGroupMain(isMain);
        join.setGroupNo(group.getGroupNo());
        join.setJoinOrderNo(info.getOrderNo());
        join.setJoinUserId(info.getCreaterId());
        join.setJoinTime(new Date());
        join.setRemark(group.getGroupDesc());
        join.setLaunchOrderNo(launchOrderNo);
        join.setLaunchUserId(launchUserId);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, group.getGroupNo());
        orderGroupJoinService.insert(join);
        return join;
    }

    /**
     * 通过团号获取团信息
     *
     * @param groupNo
     * @return
     */
    public OrderGroup getOrderGroup(String groupNo) {
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setDeleteFlag(1);
        orderGroup.setGroupNo(groupNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, orderGroup.getGroupNo());
        return orderGroupMapper.selectOne(orderGroup);
    }

    /**
     * 通过拼团号获取拼团关联列表
     *
     * @param groupNo
     * @return
     */
    public List<OrderGroupJoin> getOrderGroupRelationsByGroupNo(String groupNo) {
        EntityWrapper<OrderGroupJoin> wrapper = new EntityWrapper();
        wrapper.where("GROUP_NO", groupNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, groupNo);
        return orderGroupJoinService.selectList(wrapper);
    }

    /**
     * 用户信息转换
     *
     * @param baseUser
     * @return
     */
    private OrderGroupDto.Member getMember(BaseUser baseUser) {
        if (Objects.isNull(baseUser)) return null;
        return new OrderGroupDto.Member(baseUser.getUserId(), baseUser.getImagesUrl(),
                baseUser.getUserFullName(), baseUser.getUnionId(), false);
    }

    @Override
    public List<OrderGroup> getCompletedGroupList(OrderGroup orderGroup) {
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, orderGroup.getGroupNo());
        return orderGroupMapper.getCompletedGroupList(orderGroup);
    }

    @Override
    public Integer getCompletedGroupCount(OrderGroup orderGroup) {
        return orderGroupMapper.getCompletedGroupCount(orderGroup);
    }
}