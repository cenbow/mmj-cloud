package com.mmj.active.grouplottery.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmj.active.common.MessageUtils;
import com.mmj.active.common.OrderRefundService;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.constants.CouponConstants;
import com.mmj.active.common.feigin.CouponUserFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.OrderGroup;
import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.UserActive;
import com.mmj.active.common.model.dto.DecrGoodNum;
import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.active.grouplottery.mapper.LotteryConfMapper;
import com.mmj.active.grouplottery.model.LotteryConf;
import com.mmj.active.grouplottery.model.vo.LotteryConfSearchVo;
import com.mmj.active.grouplottery.model.vo.LotterySucceedMsg;
import com.mmj.active.grouplottery.service.LotteryConfService;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.UserLotteryDto;
import com.mmj.common.utils.DateUtils;
import com.xiaoleilu.hutool.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 抽奖配置表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-05
 */
@Service
@Slf4j
public class LotteryConfServiceImpl extends ServiceImpl<LotteryConfMapper, LotteryConf> implements LotteryConfService {

    @Autowired
    private LotteryConfMapper confMapper;

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private OrderRefundService refundService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private CouponInfoService couponInfoService;

    @Autowired
    private CouponUserFeignClient couponUserFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MessageUtils messageUtils;

    @Override
    @Transactional
    public Integer saveVo(LotteryConf confVo) {
        verify(confVo);

        LotteryConf lotteryConf = new LotteryConf();
        BeanUtils.copyProperties(confVo, lotteryConf);
        lotteryConf.setOpenFlag(0);
        boolean result = insert(lotteryConf);
        if (result) {
            redisTemplate.opsForValue().set(CommonConstant.LOTTERY_CACHE_KEY + lotteryConf.getLotteryId(),
                    JSON.toJSONString(lotteryConf), CommonConstant.LOTTERY_CACHE_TIME, TimeUnit.HOURS);
            return lotteryConf.getLotteryId();
        }
        return null;
    }

    @Override
    @Transactional
    public void deleteByLotteryId(Integer id) {
        LotteryConf lotteryConf = selectById(id);
        Assert.notNull(lotteryConf, "活动不存在");

        Assert.isTrue(new Date().before(lotteryConf.getStartTime()), "活动已开始，不可删除");

        if (deleteById(id)) {
            //删除对应的活动
            EntityWrapper<ActiveGood> activeGoodEntityWrapper = new EntityWrapper<>();
            activeGoodEntityWrapper.eq("BUSINESS_ID", lotteryConf.getLotteryId());
            activeGoodEntityWrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
            activeGoodService.delete(activeGoodEntityWrapper);
            redisTemplate.delete(CommonConstant.LOTTERY_CACHE_KEY + lotteryConf.getLotteryId());
        }
    }

    @Override
    @Transactional
    public Integer updateVo(LotteryConf confVo) {
        verify(confVo);
        Assert.notNull(confVo.getLotteryId(), "活动id不能为空");

        LotteryConf lc = selectById(confVo.getLotteryId());
        Assert.notNull(lc, "活动不存在");

        //判断是否活动进行中
        Assert.isTrue(new Date().before(lc.getStartTime()), "活动已开始，不可修改");

        LotteryConf lotteryConf = new LotteryConf();
        BeanUtils.copyProperties(confVo, lotteryConf);
        int upcnt = confMapper.updateById(lotteryConf);

        Assert.isTrue(upcnt > 0, "修改活动失败!");

        redisTemplate.opsForValue().set(CommonConstant.LOTTERY_CACHE_KEY + lotteryConf.getLotteryId(),
                JSON.toJSONString(lotteryConf), CommonConstant.LOTTERY_CACHE_TIME, TimeUnit.HOURS);
        return lotteryConf.getLotteryId();
    }

    @Override
    public Integer updateOpenDetail(LotteryConf conf) {
        Assert.notNull(conf, "抽奖活动不能为空");
        Assert.notNull(conf.getLotteryId(), "抽奖活动id不能为空");
        LotteryConf lc = selectById(conf.getLotteryId());
        Assert.notNull(lc, "活动不存在");
        Integer id = lc.getLotteryId();
        lc = new LotteryConf();
        lc.setLotteryId(id);
        lc.setOpenDetail(conf.getOpenDetail());
        updateById(lc);
        return id;
    }

    @Override
    public Page<LotteryConf> list(LotteryConfSearchVo entity) {
        Page<LotteryConf> page = new Page<>(entity.getCurrentPage(), entity.getPageSize());
        List<Integer> ids = null;
        if (StringUtils.isNotEmpty(entity.getGoodsName())) {
            ids = confMapper.selectActiveGood(entity.getGoodsName());
        }
        List<LotteryConf> list = confMapper.list(page, entity, ids);
        page.setRecords(list);
        return page;
    }

    @Override
    public LotteryConf getLotteryById(Integer id) {
        LotteryConf conf = selectById(id);
        Assert.notNull(conf, "抽奖活动不存在");
        addGoods(conf);
        return conf;
    }

    private void addGoods(LotteryConf conf) {
        if (null == conf)
            return;
        ActiveGood activeGood = new ActiveGood();
        activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
        activeGood.setBusinessId(conf.getLotteryId());
        EntityWrapper<ActiveGood> goodWrapper = new EntityWrapper<>(activeGood);
        List<ActiveGood> activeGoodList = activeGoodService.selectList(goodWrapper);
        conf.setActiveGoodList(activeGoodList);
    }

    @Override
    public Map<String, Object> getLotteryGoodsNow(Integer page, Integer size) {
        List<Map<String, Object>> list = confMapper.getLotteryGoodsNow((page - 1) * size, size);
        int total = confMapper.getLotteryGoodsNowCnt();

        Map<String, Object> result = Maps.newHashMapWithExpectedSize(2);

        if (null == list || list.size() == 0) {
            result.put("goodsList", null);
            result.put("count", total);
            return result;
        }

        for (Map<String, Object> map : list) {
            int countDownTime = Integer.parseInt(map.get("countdowntime").toString());
            int day = countDownTime / 24;
            int hour = countDownTime % 24 > 0 ? countDownTime % 24 : 1;
            StringBuilder endTime = new StringBuilder();
            if (day > 0)
                endTime.append("仅剩").append(day).append("天");
            else
                endTime.append("最后");
            if (hour > 0)
                endTime.append(hour).append("小时");

            map.put("endTime", endTime.toString());

//            Integer goodId = Integer.parseInt(map.get("goodId").toString());
        }
        result.put("goodsList", list);
        result.put("count", total);
        return result;
    }

    @Override
    public Map<String, Object> getLotteryActivityWinTips(Integer page, Integer size) {
        Page<LotteryConf> pages = new Page<>(page, size);

        EntityWrapper<LotteryConf> wrapper = new EntityWrapper<>();
        wrapper.in("OPEN_FLAG", Arrays.asList(1, 2));
        wrapper.orderAsc(Collections.singletonList("OPEN_FLAG"));
        wrapper.orderDesc(Collections.singletonList("OPEN_TIME"));
        Page<LotteryConf> resultList = selectPage(pages, wrapper);

        Map<String, Object> result = Maps.newHashMapWithExpectedSize(2);

        List<Map<String, Object>> winTipsList = new ArrayList<>();
        if (null == resultList || resultList.getSize() == 0) {
            result.put("lotteryList", winTipsList);
            result.put("count", 0);
            return result;
        }
        for (LotteryConf conf : resultList.getRecords()) {
            Map<String, Object> mapTips = Maps.newHashMapWithExpectedSize(10);
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
            activeGood.setBusinessId(conf.getLotteryId());
            EntityWrapper entityWrapper = new EntityWrapper(activeGood);
            List<ActiveGood> ags = activeGoodService.selectList(entityWrapper);
            if (null == ags || ags.size() == 0)
                continue;
            ActiveGood ag = ags.get(0);
            mapTips.put("goodName", ag.getGoodName());
            mapTips.put("mainPic", ag.getGoodImage());
            mapTips.put("wincode", conf.getCheckCode());
            mapTips.put("openTime", conf.getOpenTime());
            mapTips.put("url", conf.getOpenDetail());
            mapTips.put("drawTypePay", "0.01元抽，不中退款送优惠券");
            mapTips.put("activeTime", DateUtils.getDate(conf.getStartTime(), DateUtils.DATE_PATTERN_10)
                    + " 至 " + DateUtils.getDate(conf.getEndTime(), DateUtils.DATE_PATTERN_10));

            if (2 == conf.getOpenFlag()) {
                //未中奖
                mapTips.put("ship", "无");
                mapTips.put("winner", "本期活动无人中奖");
                winTipsList.add(mapTips);
                continue;
            }

            if (null == conf.getCheckMan()) {
                //未中奖
                mapTips.put("ship", "无");
                mapTips.put("winner", "本期活动无人中奖");
                winTipsList.add(mapTips);
                continue;
            }

            //有人中奖
            BaseUser baseUser = userFeignClient.getUserById(conf.getCheckMan());
            if (null == baseUser) {
                //未中奖
                mapTips.put("ship", "无");
                mapTips.put("winner", "本期活动无人中奖");
                winTipsList.add(mapTips);
                continue;
            }
            mapTips.put("headImg", StringUtils.isBlank(
                    baseUser.getImagesUrl()) ? "" : baseUser.getImagesUrl());

            mapTips.put("winner", "@" + baseUser.getUserFullName());
            mapTips.put("ship", "已发货");

            winTipsList.add(mapTips);
        }
        result.put("lotteryList", winTipsList);
        result.put("count", resultList.getTotal());
        return result;
    }

    @Override
    @Transactional
    public String drawLottery(LotteryConf conf) {
        log.info("活动开始开奖-->:{}", conf);
        Assert.isTrue(0 != conf.getOpenFlag(), "活动已开奖");
        Assert.notNull(conf.getLotteryId(), "活动id不能为空");
        LotteryConf lotteryConf = selectById(conf.getLotteryId());
        Assert.notNull(lotteryConf, "活动不存在");
        Assert.isTrue(lotteryConf.getEndTime().before(new Date()), "活动进行中，不能开奖");

        conf.setOpenTime(new Date());

        List<UserLotteryDto> list = orderFeignClient.getJoinUser(conf.getLotteryId());
        log.info("参与活动的订单:{}", JSON.toJSONString(list));
//        List<UserActive> list = userFeignClient.queryJoinUserList(active);

        if (null == list || 0 == list.size()) {
            //没人参与活动
            log.info("没人参与活动，直接开奖~~");
            boolean result = updateById(conf);
            if (result)
                return "开奖成功";
            return "开奖失败";
        }

        // 活动下的订单
        Set<String> orderNoSet = list.stream().map(UserLotteryDto::getOrderNo).collect(Collectors.toSet());
        // 活动下的参与人
        List<Long> userIdList = list.stream().map(UserLotteryDto::getUserId).collect(Collectors.toList());

        //查询中奖人
        UserActive userActive = new UserActive();
        userActive.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
        userActive.setBusinessId(lotteryConf.getLotteryId());
        userActive.setLotteryCode(conf.getCheckCode());
        List<UserActive> userActiveList = userFeignClient.getActiveByCode(userActive);
        UserActive ua = null != userActiveList && userActiveList.size() > 0 ? userActiveList.get(0) : null;
        //非中奖人集合
        List<UserLotteryDto> unList;
        if (null == ua) {
            unList = list;
        } else {
            unList = list.stream().filter(obj ->
                    !ua.getOrderNo().equals(obj.getOrderNo())
            ).collect(Collectors.toList());
        }

        LotterySucceedMsg succeedMsg = new LotterySucceedMsg();
        succeedMsg.setSucceed(false);

        ActiveGood ag = getActiveGood(conf.getLotteryId());
        Assert.notNull(ag, "抽奖商品不存在");
        Integer price = ag.getActivePrice();
        String goodName = ag.getGoodName();
        if (null != ua && conf.getOpenFlag() == 1) {
            log.info("中奖人信息:{}", JSON.toJSONString(ua));
            //开奖,查询所有的订单
            //剔除中奖人订单
            orderNoSet.remove(ua.getOrderNo());

            DecrGoodNum decrGoodNum = new DecrGoodNum();
            decrGoodNum.setNum(1);
            decrGoodNum.setSku(ag.getGoodSku());
            decrGoodNum.setOrderNo(ua.getOrderNo());
            ReturnData<Boolean> returnData = orderFeignClient.decr(decrGoodNum);
            log.info("开奖时扣减库存,{}", returnData);
            Assert.notNull(returnData, "扣减库存失败");
            Assert.isTrue(returnData.getData(), "开奖时扣库存失败");

            int userIdIndex = userIdList.indexOf(ua.getUserId());
            if (userIdIndex > -1)
                //删除中奖人id
                userIdList.remove(userIdIndex);

            succeedMsg.setSucceed(true);
            succeedMsg.setGoodsName(ag.getGoodName());
            succeedMsg.setCode(ua.getLotteryCode());
            succeedMsg.setDate(DateUtils.getDate(lotteryConf.getOpenTime(), DateUtils.DATE_PATTERN_11));
            succeedMsg.setUserId(ua.getUserId());
            succeedMsg.setOrderNo(ua.getOrderNo());

            BaseUser user = userFeignClient.getUserById(ua.getUserId());
            Assert.notNull(user, "用户不存在");
            succeedMsg.setNickName(user.getUserFullName());

            boolean bool = orderFeignClient.toBeDelivered(ua.getOrderNo());
            log.info("订单 {} 发货结果:{}", ua.getOrderNo(), bool);

            conf.setCheckMan(ua.getUserId());
            conf.setOrderNo(ua.getOrderNo());
        }

        log.info("退款用户数量：" + userIdList.size() + " 退款订单数量：" + orderNoSet.size());

        // 退款(在定时任务中) 送优惠券（成团未中奖）
        // 成团未中奖 LOSING_LOTTERY 未成团 GROUP_NOT_FULL(不改状态)
        refundService.batchUpdateOrderStatus(unList, OrderStatus.CLOSED.getStatus());
        refundService.batchSendRefundMsg(unList, goodName, price);

        List<OrderInfo> waitPayList = orderFeignClient.getLotteryWaitPay(lotteryConf.getLotteryId());
        refundService.batchCloseOrder(waitPayList, OrderStatus.CANCELLED.getStatus());
        //发送优惠券
        sendCoupon(lotteryConf.getCouponId(), unList);

        log.info("更新开奖表，活动id:{}", conf.getLotteryId());
        log.info(JSON.toJSONString(conf));
        boolean updated = updateById(conf);
        log.info("更新开奖表行数结果:{}", updated);

        if (!updated)
            return "开奖失败";

        if (succeedMsg.getSucceed()) {
            messageUtils.winLotteryMsg(succeedMsg.getUserId(), succeedMsg.getOrderNo(), succeedMsg.getGoodsName(),
                    succeedMsg.getCode(), succeedMsg.getNickName());
        }

        //异步退款
        refundService.batchRefund(orderNoSet, price);
        //从缓存中删除
        redisTemplate.delete(CommonConstant.LOTTERY_CACHE_KEY + conf.getLotteryId());
        return "开奖成功";
    }

    @Override
    @Transactional
    public void autoDrawLottery() {
        log.info("### 自动开奖开始执行 ###");
        EntityWrapper<LotteryConf> wrapper = new EntityWrapper<>();
        wrapper.eq("OPEN_TYPE", "1");
        wrapper.eq("OPEN_FLAG", "0");
        wrapper.le("OPEN_TIME", new Date());
        List<LotteryConf> list = selectList(wrapper);
        if (null == list || list.size() == 0) {
            log.info("暂时没有需要开奖的活动");
            return;
        }
        for (LotteryConf conf : list) {
            if (conf.getOpenFlag() != 0)
                continue;
            LotteryConf lotteryConf = new LotteryConf();
            lotteryConf.setLotteryId(conf.getLotteryId());

            List<UserLotteryDto> joinList = orderFeignClient.getJoinUser(conf.getLotteryId());
            if (null == joinList || 0 == joinList.size()) {
                //没人参与活动
                log.info("没人参与活动，直接开奖~~");
                lotteryConf.setOpenFlag(2);
                boolean result = updateById(lotteryConf);
                log.info("无人参与 {}，开奖结果:{}", conf.getLotteryName(), result);
                if (result)
                    redisTemplate.delete(CommonConstant.LOTTERY_CACHE_KEY + conf.getLotteryId());
                continue;
            }
            log.info("参与活动的订单,joinList:{}", JSON.toJSONString(joinList));

            OrderGroup group = new OrderGroup();
            group.setBusinessId(conf.getLotteryId());
            group.setGroupStatus(1);
            group.setGroupType(2);
            ReturnData<Integer> data = orderFeignClient.completedGroupCount(group);
            if (null == data)
                continue;
            int compCnt = 0;
            if (data.getCode() == 1) {
                compCnt = data.getData();
            }
            //判断是否能开奖
            int joinCnt = compCnt * conf.getTuanBuildNum();
            log.info("自动开奖，要求人数:{},成团数:{}", conf.getNeedOpneNum(), joinCnt);

            Integer price = 1;
            ActiveGood ag = getActiveGood(conf.getLotteryId());
            if (null == ag) {
                log.error("活动 {} 没有商品，无法自动开奖,id:{}", conf.getLotteryName(), conf.getLotteryId());
                continue;
            }
            price = ag.getActivePrice();

            if (joinCnt < conf.getNeedOpneNum()) {
                // 活动未达到要求人数，退款
                log.info("活动【{}】未达到要求人数，执行退款", conf.getLotteryId());
                // 活动下的订单
                Set<String> orderNoSet = joinList.stream().map(UserLotteryDto::getOrderNo).collect(Collectors.toSet());

                log.info("活动名:{},退款订单:{}", conf.getLotteryName(), JSON.toJSONString(orderNoSet));
                refundService.batchRefund(orderNoSet, price);
                refundService.batchUpdateOrderStatus(joinList, OrderStatus.CLOSED.getStatus());
                refundService.batchSendRefundMsg(joinList, ag.getGoodName(), price);
                lotteryConf.setOpenFlag(2);
                boolean result = updateById(lotteryConf);
                //发送优惠券
                sendCoupon(conf.getCouponId(), joinList);
                log.info("【{}】要求人数未达到,不开奖，结果是: {} ", conf.getLotteryName(), result ? "成功" : "失败");
                if (result)
                    redisTemplate.delete(CommonConstant.LOTTERY_CACHE_KEY + conf.getLotteryId());
                continue;
            }

            //查询中奖人
            UserActive active = new UserActive();
            active.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
            active.setBusinessId(lotteryConf.getLotteryId());
            List<UserActive> userActiveList = userFeignClient.getActiveByCode(active);

            //中奖人
            UserActive winner = null;
            if (userActiveList != null && userActiveList.size() > 0) {
                log.info("活动 {} 选择中奖人", conf.getLotteryName());
                winner = userActiveList.get(genRandom(userActiveList.size()));
                log.info("活动名:{},中奖人:{}", conf.getLotteryName(), winner);
            }

            // 活动下的订单
            Set<String> orderNoSet = joinList.stream().map(UserLotteryDto::getOrderNo).collect(Collectors.toSet());
            // 活动下的参与人
            List<Long> userIdList = joinList.stream().map(UserLotteryDto::getUserId).collect(Collectors.toList());

            //非中奖人集合
            List<UserLotteryDto> unLotteryList = joinList;
            if (null != winner) {
                log.info("活动id:{}，中奖人:{}", conf.getLotteryId(), winner);
                conf.setOpenFlag(1);
                conf.setCheckMan(winner.getUserId());
                conf.setCheckCode(winner.getLotteryCode());
                conf.setOrderNo(winner.getOrderNo());

                // 订单改成待发货并且上传聚水潭
                boolean bool = orderFeignClient.toBeDelivered(winner.getOrderNo());
                log.info("自动开奖订单 {} 发货结果:{}", winner.getOrderNo(), bool);

                // TODO: 2019/8/22 目前先扣库存
                DecrGoodNum decrGoodNum = new DecrGoodNum();
                decrGoodNum.setNum(1);
                decrGoodNum.setSku(ag.getGoodSku());
                decrGoodNum.setOrderNo(winner.getOrderNo());
                ReturnData<Boolean> returnData = orderFeignClient.decr(decrGoodNum);
                log.info("开奖时扣减库存,{}", returnData);
                Assert.notNull(returnData, "扣减库存失败");
                Assert.isTrue(returnData.getData(), "开奖时扣库存失败");

                //剔除掉中奖订单号
                orderNoSet.remove(winner.getOrderNo());

                int userIdIndex = userIdList.indexOf(winner.getUserId());
                if (userIdIndex > -1)
                    //删除中奖人id
                    userIdList.remove(userIdIndex);

                int dex = -1;
                for (int i = 0; i < unLotteryList.size(); i++) {
                    if (!winner.getOrderNo().equals(unLotteryList.get(i).getOrderNo()))
                        continue;
                    dex = i;
                    break;
                }
                if (dex > -1)
                    unLotteryList.remove(dex);
            }

            //发送优惠券
            sendCoupon(conf.getCouponId(), unLotteryList);

            // 把未中奖的订单改成已开奖
            refundService.batchUpdateOrderStatus(unLotteryList, OrderStatus.CLOSED.getStatus());
            refundService.batchSendRefundMsg(joinList, ag.getGoodName(), price);

            List<OrderInfo> waitPayList = orderFeignClient.getLotteryWaitPay(lotteryConf.getLotteryId());
            refundService.batchCloseOrder(waitPayList, OrderStatus.CANCELLED.getStatus());

            boolean updated = updateById(conf);
            log.info("【{}】自动开奖结果:{}", conf.getLotteryName(), updated);
            if (updated)
                redisTemplate.delete(CommonConstant.LOTTERY_CACHE_KEY + conf.getLotteryId());
            refundService.batchRefund(orderNoSet, price);
        }
    }


    @Override
    public Map<String, Object> getLotteryGroup(String groupNo) {
        Integer actId = orderFeignClient.getLotteryId(groupNo);
        if (null == actId)
            return null;
        LotteryConf lottery = selectById(actId);
        if (null == lottery)
            return null;

        Map<String, Object> result = new HashMap<>();
        String couponId = lottery.getCouponId();
        String couponDesc = null;
        if (StringUtils.isNotEmpty(couponId)) {
            String[] idArr = couponId.split(",");
            if (idArr.length == 1) {
                CouponInfo info = couponInfoService.selectById(idArr[0]);
                couponDesc = info != null ? info.getCouponTitle() : null;
            } else if (idArr.length > 1) {
                BigDecimal money = BigDecimal.valueOf(0);
                for (String templateId : idArr) {
                    CouponInfo info = couponInfoService.selectById(Integer.valueOf(templateId));
                    if (info != null) {
                        money = money.add(new BigDecimal(info.getCouponAmount()));
                    }
                }
                couponDesc = money + "元组合优惠券";
            }
        }
        if (StringUtils.isNotEmpty(couponDesc)) {
            result.put("couponDesc", couponDesc); // 优惠券信息
        }


        result.put("isOpen", lottery.getOpenFlag());//是否开奖  0:未开奖; 1:开奖 2不开奖
        result.put("code", lottery.getCheckCode());
        result.put("url", lottery.getOpenDetail());
        result.put("isEnd", DateUtils.subInterval(new Date(), lottery.getEndTime()) > 0 ? 0 : 1);

        result.put("hasRedPacket", lottery.getTzRondHb());
        result.put("endTime", lottery.getEndTime());
        result.put("openTime", lottery.getOpenTime());
        result.put("rules", lottery.getLotteryRule());
        result.put("activityId", lottery.getLotteryId());
        result.put("shareTitle", lottery.getShardTitle());
        result.put("shareImage", lottery.getShardImage());

        //中奖人
        if (null == lottery.getCheckMan())
            return result;
        BaseUser user = userFeignClient.getUserById(lottery.getCheckMan());
        if (null == user)
            return result;

        log.info("查询到用户:{}", user);
        //查询中奖人信息
        result.put("winnerUserId", user.getUserId().toString());
        result.put("winnerNickName", user.getUserFullName());
        result.put("winnerAvatar", user.getImagesUrl());
        if (StringUtils.isNotEmpty(user.getUserMobile())) {
            try {
                String sub = user.getUserMobile().substring(3, 7);   //号码如果不足11位会报错
                result.put("winnerMobile", user.getUserMobile().replace(sub, "****"));
            } catch (Exception e) {
                result.put("winnerMobile", genRandMobile());
            }
        } else {
            //查询订单收货地址的手机号
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", user.getUserId());
            jsonObject.put("orderNo", lottery.getOrderNo());
            jsonObject.put("orderStatus", 1);
            String mobile = orderFeignClient.getMobile(jsonObject);
            log.info("查询到收货地址的手机号是:{}", mobile);
            if (StringUtils.isBlank(mobile)) {
                String m = genRandMobile();
                log.info("手机号是空，重新生成:{}", m);
                result.put("winnerMobile", m);
            } else {
                try {
                    String sub = mobile.substring(3, 7);   //号码如果不足11位会报错
                    result.put("winnerMobile", mobile.replace(sub, "****"));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    result.put("winnerMobile", genRandMobile());
                }
            }

        }
        return result;
    }

    private static String genRandMobile() {
        int randomNum = RandomUtil.randomInt(0, 19);
        return RANDOM_MOBILE_NUM.get(randomNum);
    }

    private static final List<String> RANDOM_MOBILE_NUM = Lists.newArrayListWithCapacity(20);

    static {
        RANDOM_MOBILE_NUM.add("136****1558");
        RANDOM_MOBILE_NUM.add("181****5735");
        RANDOM_MOBILE_NUM.add("170****6898");
        RANDOM_MOBILE_NUM.add("185****9426");
        RANDOM_MOBILE_NUM.add("132****8572");
        RANDOM_MOBILE_NUM.add("133****1788");
        RANDOM_MOBILE_NUM.add("188****7959");
        RANDOM_MOBILE_NUM.add("156****3288");
        RANDOM_MOBILE_NUM.add("183****3858");
        RANDOM_MOBILE_NUM.add("150****2641");
        RANDOM_MOBILE_NUM.add("156****1917");
        RANDOM_MOBILE_NUM.add("159****8094");
        RANDOM_MOBILE_NUM.add("178****3782");
        RANDOM_MOBILE_NUM.add("158****0827");
        RANDOM_MOBILE_NUM.add("135****3488");
        RANDOM_MOBILE_NUM.add("139****6611");
        RANDOM_MOBILE_NUM.add("134****0913");
        RANDOM_MOBILE_NUM.add("188****7959");
        RANDOM_MOBILE_NUM.add("159****6556");
        RANDOM_MOBILE_NUM.add("198****0107");

    }

    private void sendCoupon(String couponIds, List<UserLotteryDto> list) {
        if (StringUtils.isBlank(couponIds))
            return;
        String[] idArr = couponIds.split(",");
        List<Integer> couList = new ArrayList<>();
        for (String id : idArr) {
            couList.add(Integer.valueOf(id));
        }
        try {
            Integer[] array = couList.toArray(new Integer[couList.size()]);
            log.info("发送优惠券Id:{}", array);
            for (Integer id : array) {
                for (UserLotteryDto active : list) {
                    UserCouponVo vo = new UserCouponVo();
                    vo.setUserId(active.getUserId());
                    vo.setCouponId(id);
                    vo.setCouponSource(CouponConstants.CouponSource.LOTTERY);
                    couponUserFeignClient.receive(vo);
                }
            }
        } catch (Exception e) {
            log.error("开奖发送优惠券失败  " + e.getMessage(), e);
        }
    }

    private int genRandom(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }

    private ActiveGood getActiveGood(Integer businessId) {
        EntityWrapper<ActiveGood> wrapper = new EntityWrapper<>();
        wrapper.eq("ACTIVE_TYPE", ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
        wrapper.eq("BUSINESS_ID", businessId);
        ActiveGood ag = activeGoodService.selectOne(wrapper);
        Assert.notNull(ag, "抽奖活动商品不存在");
        return ag;
    }

    private void verify(LotteryConf confVo) {
        Assert.notNull(confVo, "活动不能为空");

        Assert.isTrue(StringUtils.isNotBlank(confVo.getLotteryName()), "活动名不能为空");
        Assert.notNull(confVo.getStartTime(), "活动开始时间不能为空");
        Assert.notNull(confVo.getEndTime(), "活动结束时间不能为空");
        Assert.notNull(confVo.getOpenTime(), "开奖时间不能为空");

        Assert.notNull(confVo.getMaxEveryone(), "单人最大参与次数不能为空");
        Assert.notNull(confVo.getMaxEveryone(), "开奖需参与人数不能为空");
        Assert.notNull(confVo.getTuanBuildNum(), "成团人数不能为空");

        Assert.isTrue(StringUtils.isNotBlank(confVo.getCouponId()), "优惠券不能为空");

        Assert.notNull(confVo.getShowFlag(), "是否显示在列表页不能为空");

        Assert.isTrue(StringUtils.isNotBlank(confVo.getLotteryRule()), "活动规则不能为空");
    }
}
