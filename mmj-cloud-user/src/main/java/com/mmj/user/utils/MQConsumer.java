package com.mmj.user.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.MQCommonTopic;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.MQTopicConstantDelay;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.DelayTaskDto;
import com.mmj.common.model.UserMerge;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.StringUtils;
import com.mmj.user.async.service.UserAsyncService;
import com.mmj.user.common.model.vo.OrderDetailVo;
import com.mmj.user.manager.model.UserActive;
import com.mmj.user.manager.service.UserActiveService;
import com.mmj.user.member.constant.MemberKingConstant;
import com.mmj.user.member.model.KingUser;
import com.mmj.user.member.model.UserKingLog;
import com.mmj.user.member.service.KingUserService;
import com.mmj.user.member.service.SaveMoneyService;
import com.mmj.user.member.service.UserKingLogService;
import com.mmj.user.recommend.service.UserShardService;
import com.mmj.user.shopCart.model.UserShopCart;
import com.mmj.user.shopCart.model.dto.ShopCartsDto;
import com.mmj.user.shopCart.service.UserShopCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class MQConsumer {

    //待付款
    private static final String RECOMMEND_SHARD_WAIT_PAY_TOPIC = "mmj-order-recommend-shard-wait-pay-topic";

    //确定收货
    private static final String RECOMMEND_SHARD_CONFIRM_GOOD_TOPIC = "mmj-order-recommend-shard-confirm-good-topic";

    //取消付款
    private static final String RECOMMEND_SHARD_CANCEL_PAY_TOPIC = "mmj-order-recommend-shard-cancel-pay-topic";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private UserShardService userShardService;

    @Autowired
    private SaveMoneyService saveMoneyService;

    @Autowired
    private UserActiveService userActiveService;

    @Autowired
    private UserKingLogService userKingLogService;

    @Autowired
    private KingUserService kingUserService;

    @Autowired
    private UserShopCartService userShopCartService;

    @Autowired
    private UserAsyncService userAsyncService;

    @KafkaListener(topics = {MQTopicConstant.WX_DELAY_TASK_ACCEPT})
    public void delayTask(List<String> paramsList) {
        log.info("延时任务接收:{}", paramsList);
        for (String params : paramsList) {
            DelayTaskDto delayTaskDto = JSONObject.parseObject(params, DelayTaskDto.class);
            if (MQTopicConstantDelay.COUPON_TIMEOUT_1.equals(delayTaskDto.getBusinessType())) {
                delayTaskDto.getBusinessId();//优惠券编码
                delayTaskDto.getBusinessData(); //优惠券信息
            } else if (MQTopicConstantDelay.COUPON_TIMEOUT_2.equals(delayTaskDto.getBusinessType())) {
                delayTaskDto.getBusinessId();//优惠券编码
                delayTaskDto.getBusinessData(); //优惠券信息
            }
        }
    }

    @KafkaListener(topics = {MQCommonTopic.RETURN_MMKING_TOPIC})
    public void returnMMKing(String params) {
        log.info("开始解绑买买金:{}", params);
        JSONObject object = JSONObject.parseObject(params);
        if (!object.containsKey("userId")) {
            log.error("解绑买买金时userId不存在:{}", object);
            return;
        }
        Long userId = object.getLong("userId");
        KingUser ku = kingUserService.getByUserId(userId);
        if (null == ku) {
            log.info("解绑买买金时发现买买金账户不存在,{}", ku);
            return;
        }
        if (!object.containsKey("kingNum")) {
            log.error("解绑买买金数量不存在:{}", object);
            return;
        }
        int kingNum = object.getInteger("kingNum");
        ku.setKingNum(ku.getKingNum() + kingNum);
        ku.setUpdateTime(new Date());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        kingUserService.updateById(ku);
        UserKingLog kingLog = new UserKingLog();
        kingLog.setKingNum(ku.getKingNum());
        kingLog.setUpdateNum(kingNum);
        kingLog.setCreateTime(new Date());
        kingLog.setUserId(userId);
        kingLog.setShareType(MemberKingConstant.ShareType.ORDER);
        kingLog.setKingContext("取消订单:" + object.getString("orderNo"));
        kingLog.setOrderNo(object.getString("orderNo"));
        boolean bool = userKingLogService.insert(kingLog);
        log.info("解绑买买金结果:{}", bool);
    }


    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_TO_ES_TOPIC})
    public void consumeOrder(String params) {
        OrdersMQDto dto = JSONObject.parseObject(params, OrdersMQDto.class);
        log.info("用户模块,生单队列处理:{}", dto);
        try {
            Integer logId = userKingLogService.procMMKing(dto);
        } catch (Exception e) {
            log.error("-->扣减买买金报错:{}", e.getMessage(), e);
        }
        try {
            this.removeShopCart(dto);
        } catch (Exception e) {
            log.error("-->清空购物车报错:{}", e.getMessage(), e);
        }
        try {
            this.waitPayUserRecommend(dto);
        } catch (Exception e) {
            log.error("-->推荐返现报错:{}", e.getMessage(), e);
        }
    }

    private void removeShopCart(OrdersMQDto dto) {
        log.info("清空购物车:{}", dto);
        if (0 != dto.getType())
            return; //非购物车下单，不请购物车
        List<OrdersMQDto.Goods> goods = dto.getGoods();
        if (null == goods || goods.size() == 0) {
            log.error("清空购物车异常，商品不存在");
            return;
        }

        UserShopCart shopCarts = new UserShopCart();
        shopCarts.setDeleteFlag(false);
        shopCarts.setModiefyId(dto.getUserId());

        List<String> skuIdList = new ArrayList<>();
        goods.forEach(good -> {
            skuIdList.add(good.getGoodSku());
        });
        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.in("GOOD_SKU", skuIdList);
        shopCartEntityWrapper.eq("CREATER_ID", dto.getUserId());
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);
        shopCartEntityWrapper.eq("GOOD_TYPE", 16);

        //执行逻辑删除之前查询出要删除的商品
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, dto.getUserId());
        List<UserShopCart> shopCartsList = userShopCartService.selectList(shopCartEntityWrapper);
        Vector<ShopCartsDto> vector = new Vector<>();
        shopCartsList.forEach(carts -> {
            vector.add(new ShopCartsDto(carts.getGoodId(), carts.getGoodSku()));
        });
        boolean result = userShopCartService.update(shopCarts, shopCartEntityWrapper);
        log.info("清空购物车结果:{}", result);
        //TODO 微信购物单-删除我的收藏 kafka实现
    }


    /**
     * 推荐返现- 待付款
     */
    public void waitPayUserRecommend(OrdersMQDto dto) {
        log.info("-->开始进入推荐返现,生成订单，待付款方法处理中:{}", JSONObject.toJSONString(dto));
        Long userId = dto.getUserId();
        String orderNo = dto.getOrderNo();
        String appId = dto.getAppId();
        userShardService.updateRecommendShared(userId, orderNo, appId, 1);
    }

    @KafkaListener(topics = {MQCommonTopic.LOTTERY_CODE_TOPIC})
    public void addLotteryCode(List<ConsumerRecord<?, ?>> records) {
        log.info("进入新增抽奖码处理方法:{}", records);
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent()) {
                continue;
            }
            Object message = record.value();
            UserActive active = JSONObject.parseObject(message.toString(), UserActive.class);
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, active.getUserId());
            boolean bool = userActiveService.insert(active);
            if (bool) {
                String payKey = CommonConstant.LOTTERY_CODE_COUNT_PREFIX + active.getBusinessId();
                redisTemplate.opsForValue().increment(payKey, 1);
            }
            log.info("创建抽奖码结果:{}", bool);
        }
    }


    @KafkaListener(topics = {MQCommonTopic.MEMBER_PREFERENTIAL})
    public void packageParse(List<ConsumerRecord<?, ?>> records) {
        log.info("进入统计会员省钱回调处理:{}", records);
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            if (!kafkaMessage.isPresent()) {
                continue;
            }
            Object message = record.value();
            if (null == message) {
                log.error("统计会员省钱报错，回调参数是空, {}" + message);
                continue;
            }
            log.info("开始处理统计会员省钱:{}", message);
            OrderDetailVo orderDetailVo = JSONObject.parseObject(message.toString(), OrderDetailVo.class);
            saveMoneyService.saveMoney(orderDetailVo);
        }
    }

    /**
     * 推荐返现 - 确定收货
     *
     * @param params
     */
    @KafkaListener(topics = {RECOMMEND_SHARD_CONFIRM_GOOD_TOPIC})
    public void ConfirmGoodListener(String params) {
        log.info("-->开始进入推荐返现确定收货,修改状态处理方法,请求参数:{}", JSONObject.toJSONString(params));
        if (!StringUtils.isEmpty(params)) {
            JSONObject jsonObject = JSON.parseObject(params);
            Long userid = jsonObject.getLong("userId");
            String orderNo = jsonObject.getString("orderNo");
            String appId = jsonObject.getString("appId");
            userShardService.updateConfirm(orderNo, userid, appId);
        }
    }

    /**
     * 推荐返现 - 取消付款
     *
     * @param params
     */
    @KafkaListener(topics = {RECOMMEND_SHARD_CANCEL_PAY_TOPIC})
    public void cancelPaylistener(String params) {
        log.info("-->开始进入推荐返现取消付款,修改状态处理方法,请求参数:{}", JSONObject.toJSONString(params));
        if (!StringUtils.isEmpty(params)) {
            JSONObject jsonObject = JSON.parseObject(params);
            Long userid = jsonObject.getLong("userId");
            String orderNo = jsonObject.getString("orderNo");
            userShardService.cancelOrder(userid, orderNo);
        }
    }

    @KafkaListener(topics = {MQCommonTopic.MMKING_TOPIC})
    public void addMMKing(String params) {
        log.info("进入新增买买金处理方法:{}", params);
        JSONObject object = JSONObject.parseObject(params);
        if (null == object) {
            log.info("参数为空:{}", object);
            return;
        }
        if (!object.containsKey("userId")) {
            log.info("用户id不存在:{}", object);
        }
        if (!object.containsKey("type")) {
            log.info("活动类型不存在:{}", object);
        }
        userKingLogService.actInsert(object.getLong("userId"),
                object.getString("type"));
    }


    @KafkaListener(topics = {MQTopicConstant.TOPIC_USER_MERGE})
    public void merge(String data) {
        log.info("-->user模块监听到用户合并的主题消息：{}", data);
        UserMerge userMerge = JSONObject.parseObject(data, UserMerge.class);

        //合并会员表相关数据
        userAsyncService.mergeUserMemberTables(userMerge);

        //合并收货地址管理表相关数据
        userAsyncService.mergeBaseUserAddrTables(userMerge);

        //合并商品推荐相关表数据
        userAsyncService.mergeUserRecommend(userMerge);

        //合并推荐返现相关表数据
        userAsyncService.mergeUserShard(userMerge);

        //合并流量池关注信息
        userAsyncService.mergeUserFocus(userMerge);

        //合并买买金用户
        userAsyncService.mergeMMKing(userMerge);

        // TODO 其它模块的
    }

    /**
     * 商品信息变更同步购物车
     *
     * @param jsonString
     */
    @KafkaListener(topics = {MQTopicConstant.GOOD_SALE_UPDATE})
    public void goodSaleUpdate(String jsonString) {
        if (jsonString != null && jsonString.length() != 0) {
            JSONArray goodSales = JSON.parseArray(jsonString);
            if (goodSales != null && !goodSales.isEmpty()) {
                for (int i = 0; i < goodSales.size(); i++) {
                    JSONObject goodSale = goodSales.getJSONObject(i);
                    String goodSku = goodSale.getString("goodSku");
                    Integer basePrice = goodSale.getInteger("basePrice");
                    Integer shopPrice = goodSale.getInteger("shopPrice");
                    Integer memberPrice = goodSale.getInteger("memberPrice");
                    if (goodSku != null && goodSku.length() != 0 && basePrice != null) {
                        EntityWrapper<UserShopCart> userShopCartEntityWrapper = new EntityWrapper<>();
                        userShopCartEntityWrapper.eq("GOOD_SKU", goodSku);
                        UserShopCart userShopCart = new UserShopCart();
                        userShopCart.setBasePrice(BigDecimal.valueOf(PriceConversion.longToDouble(basePrice.longValue())));
                        userShopCart.setGoodPrice(BigDecimal.valueOf(PriceConversion.longToDouble(shopPrice.longValue())));
                        userShopCart.setMemberPrice(BigDecimal.valueOf(PriceConversion.longToDouble(memberPrice.longValue())));
                        try {
                            for (int x = 0; x < 10; x++) {
                                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, x);
                                userShopCartService.update(userShopCart, userShopCartEntityWrapper);
                            }
                        } catch (Exception e) {
                            log.error("MQ商品销售信息更新消息失败：", e);
                        }
                    }
                }
            }
        }
    }

}
