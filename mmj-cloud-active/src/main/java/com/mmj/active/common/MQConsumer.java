package com.mmj.active.common;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.mmj.active.callCharge.service.CallChargeRecordService;
import com.mmj.active.common.feigin.WxMessageFeignClient;
import com.mmj.active.common.model.FocusInfo;
import com.mmj.active.common.model.SMSInfoDto;
import com.mmj.active.common.service.FocusInfoService;
import com.mmj.active.coupon.service.CouponRedeemCodeService;
import com.mmj.active.cut.service.CutSponsorService;
import com.mmj.active.homeManagement.common.RedisUtils;
import com.mmj.active.limit.model.ActiveLimit;
import com.mmj.active.limit.model.ActiveLimitDetail;
import com.mmj.active.limit.model.ActiveLimitEx;
import com.mmj.active.limit.service.ActiveLimitDetailService;
import com.mmj.active.limit.service.ActiveLimitService;
import com.mmj.active.seckill.service.SeckillInfoService;
import com.mmj.active.threeSaleTenner.constant.ThreeSaleTennerStatus;
import com.mmj.active.threeSaleTenner.service.ThreeSaleTennerService;
import com.mmj.common.constants.*;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.*;
import com.mmj.common.model.order.OrderStore;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.*;
import com.xiaoleilu.hutool.system.UserInfo;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.async.service.ActiveAsyncService;
import com.mmj.active.common.config.GzhNotifyConfig;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.GoodFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.GoodFile;
import com.mmj.active.common.model.dto.DecrGoodNum;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.common.service.ShareGoodService;
import com.mmj.active.common.service.WatermarkConfigureService;
import com.mmj.active.homeManagement.common.CodeUtils;
import com.mmj.active.homeManagement.constant.RedisKey;
import com.mmj.active.homeManagement.model.WebShow;
import com.mmj.active.homeManagement.service.WebShowService;
import com.mmj.active.prizewheels.service.PrizewheelsFacadeService;
import com.mmj.active.seckill.constants.SeckillConstants;
import com.mmj.active.threeSaleTenner.service.ThreeSaleFissionService;
import com.mmj.common.model.order.OrderStatusMQDto;
import com.mmj.common.model.order.OrdersMQDto;
import com.xiaoleilu.hutool.collection.CollectionUtil;

@Slf4j
@Component
public class MQConsumer {

    //十元三件 - 确定收货
    private static final String THREE_SALE_CONFIRM_GOOD_TOPIC = "mmj-order-three-sale-confirm-good-topic";

    //十元三件 - 取消订单
    private static final String THREE_SALE_CANCEL_PAY_TOPIC = "mmj-order-three-sale-cancel-pay-topic";

    @Autowired
    private ThreeSaleFissionService threeSaleFissionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private GzhNotifyConfig gzhNotifyConfig;

    @Autowired
    private WatermarkConfigureService watermarkConfigureService;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private ActiveGoodService activeGoodService;

    @Autowired
    private PrizewheelsFacadeService prizewheelsFacadeService;

    @Autowired
    private ActiveAsyncService activeAsyncService;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private ShareGoodService shareGoodService;

    @Autowired
    private ThreeSaleTennerService threeSaleTennerService;

    @Autowired
    private FocusInfoService focusInfoService;

    @Autowired
    private WxMessageFeignClient wxMessageFeignClient;

    @Autowired
    private CallChargeRecordService callChargeRecordService;

    @Autowired
    private ActiveLimitService activeLimitService;

    @Autowired
    private CouponRedeemCodeService couponRedeemCodeService;

    @Autowired
    private SeckillInfoService seckillInfoService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private CutSponsorService cutSponsorService;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 流量池发送模板消息
     *
     * @param params
     */
    @KafkaListener(topics = MQTopicConstant.FP1902_MSG)
    public void userFocusListen(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String param : params) {
                JSONObject o = JSON.parseObject(param);
                log.info("-----------userFocusListen---" + param);
                String userId = o.getString("userId");
                String openId = o.getString("openId");
                String msg = "";
                if (userId == null || userId.length() == 0 || openId == null || openId.length() == 0) {
                    log.error("userFocusListen:流量池发送关注模板消息失败，userid/openId为空！");
                    return;
                }
                Integer module = o.getInteger("module");
                Integer type = o.getInteger("type");
                if (module == 1 && type == 1) {
                    //秒杀-订阅秒杀提醒
                    String startTime = o.getString("startTime");
                    String goodName = o.getString("goodName");
                    msg = gzhNotifyConfig.bookSend1(openId, startTime, goodName);
                }

                if (module == 2 && type == 1) {
                    //砍价-订阅砍价进度(首次砍价进度模板消息进入页面)
                    String logId = o.getString("logId");
                    msg = gzhNotifyConfig.bookSend2(openId, simpleDateFormat.format(new Date()), logId);
                }

                if (module == 3) {
                    if (type == 1) {
                        //抽奖-开奖公示区开启开奖提醒
                        msg = gzhNotifyConfig.bookSend4(openId, simpleDateFormat.format(new Date()));
                    } else if (type == 2) {
                        //抽奖-开奖区开启抽奖提醒
                        msg = gzhNotifyConfig.bookSend3(openId, simpleDateFormat.format(new Date()));
                    } else if (type == 3) {
                        // TODO: 2019/8/26 关注成功，发送优惠券
                        //抽奖-领取优惠券
                        Integer activityid = o.getInteger("activityid");
                        String couponName = o.getString("couponName");
                        msg = gzhNotifyConfig.bookSend5(openId, couponName, simpleDateFormat.format(new Date()));
                    }
                }

                if (module == 4) {
                    if (type == 1) {
                        //签到-订阅签到提醒
                        msg = gzhNotifyConfig.bookSend9(openId, simpleDateFormat.format(new Date()));
                    } else if (type == 1 || type == 2) {
                        //签到-签到后获得一次签到机会
                        String userName = o.getString("userName");
                        msg = gzhNotifyConfig.bookSend10(openId, userName, simpleDateFormat.format(new Date()));
                    }
                }

                if (module == 5 && type == 1) {
                    //十元三件-关注后获得购买机会
                    String key = "3sale10";
                    String attributeKey = "3sale10_" + userId;
                    redisTemplate.opsForHash().increment(key, attributeKey, 1);
                    msg = gzhNotifyConfig.bookSend8(openId, simpleDateFormat.format(new Date()));
                }

                if (module == 6) {
                    if (type == 1) {
                        //转盘-转盘签到订阅提醒
                        msg = gzhNotifyConfig.bookSend6(openId, simpleDateFormat.format(new Date()));
                    } else if (type == 2) {
                        //转盘-转盘十元结果翻倍
                        prizewheelsFacadeService.sendDoubleRewardOfTenYuan(Long.valueOf(userId));
                        msg = gzhNotifyConfig.bookSend7(openId, simpleDateFormat.format(new Date()));
                    }
                }

                if (module == 7 && type == 1) {
                    //店铺订单-订阅物流进度提醒
                    String orderNo = o.getString("orderNo");
                    msg = gzhNotifyConfig.bookSend11(openId, simpleDateFormat.format(new Date()), orderNo);
                }

                if (msg != null && msg.length() != 0) {
                    log.info("---------------------userFocusListen-sendMsg----" + msg);
                    wxMessageFeignClient.sendTemplate(msg);
                }
            }
        }
    }

    /**
     * 十元三件红包裂变 - 支付成功
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.WX_ORDER_TOPIC})
    public void payBackListener(List<String> params) {
        if (params != null && !params.isEmpty()) {
            log.info("-->进入支付回调处理方法,请求参数:{}", JSONObject.toJSONString(params));
            for (String param : params) {
                if (StringUtil.isNotEmpty(param)) {
                    JSONObject jsonObject = JSON.parseObject(param);
                    String orderNo = jsonObject.getString("outTradeNo");
                    if (!StringUtils.isEmpty(orderNo)) {
                        int orderType = OrderUtils.getOrderType(orderNo);
                        if (orderType == OrderType.RECHARGE) {
                            log.info("=> 话费订单支付处理 content:{}", jsonObject.toJSONString());
                            callChargeRecordService.paySuccess(orderNo, jsonObject);
                        } else if (orderType == OrderType.TEN_FOR_THREE_PIECE) {
                            log.info("-->开始进入十元三件红包裂变,支付状态修改中:{}", orderNo);
                            threeSaleFissionService.updatePay(orderNo);
                        } else if (orderType == OrderType.SPIKE) {
                            log.info("-->秒杀订单支付成功:{}", orderNo);
                            seckillInfoService.paySuccess(orderNo);
                        }
                    }
                }
            }
        }
    }


    /**
     * 十元三件红包裂变 - 确定收货
     *
     * @param params
     */
    @KafkaListener(topics = {THREE_SALE_CONFIRM_GOOD_TOPIC})
    public void orderConfirmGoodsListener(List<String> params) {
        if (params != null && !params.isEmpty()) {
            log.info("进入十元三件订单确定收货修改订单状态处理方法,请求参数:{}", JSONObject.toJSONString(params));
            for (String param : params) {
                if (StringUtil.isNotEmpty(param)) {
                    JSONObject jsonObject = JSON.parseObject(param);
                    String orderNo = jsonObject.getString("orderNo");
                    String appId = jsonObject.getString("appId");
                    if (!StringUtils.isEmpty(orderNo)) {
                        log.info("开始进入十元三件红包裂变,订单状态修改中:{},用户APPId:{}", orderNo, appId);
                        threeSaleFissionService.updateConfirm(orderNo, appId);
                    }
                }
            }
        }
    }

    /**
     * 十元三件红包裂变 - 取消订单
     *
     * @param params
     */
    @KafkaListener(topics = {THREE_SALE_CANCEL_PAY_TOPIC})
    public void orderCancelPayListener(List<String> params) {
        if (params != null && !params.isEmpty()) {
            log.info("-->进入十元三件订单取消订单修改订单状态处理方法,请求参数:{}", JSONObject.toJSONString(params));
            for (String param : params) {
                if (StringUtil.isNotEmpty(param)) {
                    JSONObject jsonObject = JSON.parseObject(param);
                    String orderNo = jsonObject.getString("orderNo");
                    if (!StringUtils.isEmpty(orderNo)) {
                        try {
                            log.info("-->开始进入十元三件红包裂变,订单状态修改中:{}", orderNo);
                            threeSaleFissionService.cancelled(orderNo);
                        } catch (Exception e) {
                            log.info("-->十元三件红包裂变订单状态修改中失败");
                        }
                        try {
                            log.info("-->开始进入十元三件,修改分享时间(取消订单):{}", JSON.toJSONString(param));
                            Long userId = jsonObject.getLong("userId");
                            threeSaleTennerService.addShareTime(userId, ThreeSaleTennerStatus.CANCEL_PAY, orderNo);
                        } catch (Exception e) {
                            log.info("-->十元三件,修改分享时间失败(取消订单)");
                        }
                    }
                }
            }
        }
    }

    /**
     * 商品基本信息更新消息
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.GOOD_INFO_UPDATE})
    public void goodInfoUpdate(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String jsonString : params) {
                //生成水印图片
                JSONObject goodInfo = JSON.parseObject(jsonString);
                String url = goodInfo.getString("url");
                Integer goodId = goodInfo.getInteger("goodId");
                if (goodId != null && url != null && url.length() != 0) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("url", url);//分享图url
                    jsonObject.put("classify", 1);//水印类型：1、商品分享图，2、抽奖分享图
                    String markUrl = watermarkConfigureService.createMark(jsonObject.toJSONString());
                    if (markUrl != null && markUrl.length() != 0) {
                        List<GoodFile> goodFiles = new ArrayList<>();
                        GoodFile goodFile1 = new GoodFile();//小程序
                        goodFile1.setGoodId(goodId);
                        goodFile1.setFileServer("FILE_SERVER");
                        goodFile1.setFileType("WECHAT");
                        goodFile1.setFileUrl(markUrl);
                        goodFile1.setActiveType(ActiveGoodsConstants.ActiveType.SHOP_GOOD);
                        GoodFile goodFile2 = new GoodFile();//H5
                        goodFile2.setGoodId(goodId);
                        goodFile2.setFileServer("FILE_SERVER");
                        goodFile2.setFileType("H5");
                        goodFile2.setFileUrl(markUrl);
                        goodFile2.setActiveType(ActiveGoodsConstants.ActiveType.SHOP_GOOD);
                        goodFiles.add(goodFile1);
                        goodFiles.add(goodFile2);
                        goodFeignClient.saveFile(goodFiles);
                    }
                }
                //更新商品信息
                if (goodId != null) {
                    EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
                    entityWrapper.eq("GOOD_ID", goodId);
                    ActiveGood activeGood = new ActiveGood();
                    activeGood.setMemberFlag(goodInfo.getInteger("memberFlag"));
                    activeGood.setVirtualFlag(goodInfo.getInteger("virtualFlag"));
                    activeGood.setCombinaFlag(goodInfo.getInteger("combinaFlag"));
                    activeGood.setShortName(goodInfo.getString("shortName"));
                    activeGood.setGoodSpu(goodInfo.getString("goodSpu"));
                    activeGood.setGoodClass(goodInfo.getString("goodClass"));
                    activeGoodService.update(activeGood, entityWrapper);
                }

                try {
                    redisUtils.updateWebShowcaseCode();
                } catch (Exception e) {
                    log.error("-->商品基本信息更新,修改橱窗版本号报错:{}", e);
                }
            }
        }
    }


    /**
     * 商品销售信息更新消息
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.GOOD_SALE_UPDATE})
    public void goodSaleUpdate(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String jsonString : params) {
                if (jsonString != null && jsonString.length() != 0) {
                    JSONArray goodSales = JSON.parseArray(jsonString);
                    if (goodSales != null && !goodSales.isEmpty()) {
                        for (int i = 0; i < goodSales.size(); i++) {
                            JSONObject goodSale = goodSales.getJSONObject(i);
                            String goodSku = goodSale.getString("goodSku");
                            Integer basePrice = goodSale.getInteger("basePrice");
                            Integer memberPrice = goodSale.getInteger("memberPrice");
                            if (goodSku != null && goodSku.length() != 0 && basePrice != null) {
                                EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
                                entityWrapper.eq("GOOD_SKU", goodSku);
                                ActiveGood activeGood = new ActiveGood();
                                activeGood.setBasePrice(basePrice);
                                activeGood.setMemberPrice(memberPrice);
                                try {
                                    activeGoodService.update(activeGood, entityWrapper);
                                } catch (Exception e) {
                                    log.error("MQ商品销售信息更新消息失败：", e);
                                }
                            }
                        }
                    }
                }

                try {
                    redisUtils.updateWebShowcaseCode();
                } catch (Exception e) {
                    log.error("-->商品销售信息更新,修改橱窗版本号报错:{}", e);
                }
            }
        }

    }

    /**
     * 商品状态变更消息
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.GOOD_STATUS_UPDATE})
    public void goodStatusUpdate(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String jsonString : params) {
                if (jsonString != null && jsonString.length() != 0) {
                    JSONObject object = JSON.parseObject(jsonString);
                    JSONArray goodIds = object.getJSONArray("goodIds");
                    String goodStatus = object.getString("goodStatus");
                    EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
                    entityWrapper.in("GOOD_ID", goodIds);
                    ActiveGood activeGood = new ActiveGood();
                    activeGood.setGoodStatus(goodStatus);
                    activeGoodService.update(activeGood, entityWrapper);
                }

                try {
                    redisUtils.updateWebShowcaseCode();
                } catch (Exception e) {
                    log.error("-->商品状态变更,修改橱窗版本号报错:{}", e);
                }
            }
        }
    }

    /**
     * 主订单状态变更消息
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_STATUS_TO_ES_TOPIC})
    public void listenOrderStatus(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String data : params) {
                OrderStatusMQDto orderStatusMQDto = JSONObject.parseObject(data, OrderStatusMQDto.class);
                String orderNo = orderStatusMQDto.getOrderNo();
                Integer orderStatus = orderStatusMQDto.getOrderStatus();
                int orderType = OrderUtils.getOrderType(orderNo);
                log.info("SYNC_ORDER_STATUS_TO_ES_TOPIC:{}_{}_{}", orderNo, orderStatus, orderType);
                //秒杀订单处理
                if (orderStatus == OrderStatus.CANCELLED.getStatus()) {
                    if (orderType == OrderType.SPIKE) {
                        seckillInfoService.payCancelled(orderNo);
                        seckillInfoService.sendFlashSaleSMS(orderNo);
                    } else if (orderType == OrderType.TEN_FOR_THREE_PIECE || orderType == ActiveGoodsConstants.ActiveType.GROUP_LOTTERY ||
                            orderType == ActiveGoodsConstants.ActiveType.CUT || orderType == ActiveGoodsConstants.ActiveType.TUAN) {
                        //produceLimit(orderNo, true);
                    }
                } else if (orderStatus == OrderStatus.CLOSED.getStatus()) {
                    if (orderType == OrderType.SPIKE) {
                        seckillInfoService.paydClosed(orderNo);
                    } else if (orderType == OrderType.TEN_FOR_THREE_PIECE || orderType == ActiveGoodsConstants.ActiveType.GROUP_LOTTERY ||
                            orderType == ActiveGoodsConstants.ActiveType.CUT || orderType == ActiveGoodsConstants.ActiveType.TUAN) {
                        //produceLimit(orderNo, true);
                    }
                }

                if (orderType == OrderType.BARGAIN) {
                    cutSponsorService.editOrderStatusByOrderNo(orderNo, orderStatus);
                }
            }
        }
    }

    /**
     * 生成订单成功
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_TO_ES_TOPIC})
    public void listen(List<String> params) {
        log.info("=>kafka-listenOrders -> {}", params);
        if (params != null && !params.isEmpty()) {
            for (String jsonString : params) {
                OrdersMQDto ordersMQDto = JSONObject.parseObject(jsonString, OrdersMQDto.class);
                String orderNo = ordersMQDto.getOrderNo();
                Integer orderType = ordersMQDto.getOrderType();
                if (orderType == OrderType.TEN_FOR_THREE_PIECE || orderType == ActiveGoodsConstants.ActiveType.GROUP_LOTTERY ||
                        orderType == ActiveGoodsConstants.ActiveType.CUT || orderType == ActiveGoodsConstants.ActiveType.TUAN) {
                    //produceLimit(orderNo, false);
                } else if (orderType == OrderType.BARGAIN) {
                    String passingData = ordersMQDto.getPassingData();
                    JSONObject jsonObject = JSONObject.parseObject(passingData);
                    String cutNo = jsonObject.getString("cutNo");
                    cutSponsorService.addOrderInfoByCutNo(cutNo, orderNo, ordersMQDto.getOrderStatus(), ordersMQDto.getUserId());
                }
            }
        }
    }

    /**
     * 下单失败
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_PRODUCE_ORDER_FAIL})
    public void produceOrderFail(List<String> params) {
        if (params != null && !params.isEmpty()) {
            log.info("SYNC_PRODUCE_ORDER_FAIL:" + params);
            for (String data : params) {
                if (data != "null" && !"".equals(data)) {
                    String orderNo = JSON.parseObject(data).getString("orderNo");
                    int orderType = OrderUtils.getOrderType(orderNo);
                    //秒杀订单处理
                    if (orderType == OrderType.SPIKE) {
                        seckillInfoService.orderFail(orderNo);
                    }
                }
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.TOPIC_USER_MERGE})
    public void merge(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String data : params) {
                log.info("-->active模块监听到用户合并的主题消息：{}", data);
                UserMerge userMerge = JSONObject.parseObject(data, UserMerge.class);

                //合并用户转盘相关数据
                activeAsyncService.mergePrizewheelsActiveTables(userMerge);

                //合并十元三件红包勒边相关数据
                activeAsyncService.mergeThreeSaleFission(userMerge);

                //合并流量池
                activeAsyncService.mergeFocusInfo(userMerge);

                // TODO 其它模块的
                // activeAsyncService.xxxxx
            }
        }
    }

    private long getRemainTime(LocalDateTime localDateTime) {
        LocalDateTime midnight = localDateTime.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
    }


    /**
     * 限购处理 同一个订单不可能有多种类型商品
     *
     * @param orderNo
     * @param flag
     */
    public void produceLimit(String orderNo, boolean flag) {
        String orderKey = ActiveGoodsConstants.ACTIVE_ORDER + orderNo; //set
        String orderSkuKey = ActiveGoodsConstants.ACTIVE_ORDER_SKU; //hash
        Set<Object> skus = redisTemplate.opsForSet().members(orderKey);
        if (skus != null && !skus.isEmpty()) {
            ActiveLimitEx activeLimit = null;
            boolean limitFlag = false;
            Iterator<Object> i = skus.iterator();
            while (i.hasNext()) {
                String sku = String.valueOf(i.next());
                Map<Object, Object> entries = redisTemplate.opsForHash().entries(orderSkuKey + sku);
                String activeType = String.valueOf(entries.get("activeType"));
                String userId = String.valueOf(entries.get("userId"));
                String goodId = String.valueOf(entries.get("goodId"));
                Integer goodNum;
                if (flag) {
                    goodNum = -(Integer) entries.get("goodNum");
                    redisTemplate.delete(orderSkuKey + sku);
                } else {
                    goodNum = (Integer) entries.get("goodNum");
                }
                //限购处理
                if (activeLimit == null && !limitFlag) {
                    activeLimit = activeLimitService.queryLimit(activeType, Integer.valueOf(goodId));
                    limitFlag = true;
                }
                if (activeLimit != null) {
                    List<ActiveLimitDetail> activeLimitDetails = activeLimit.getActiveLimitDetails();
                    if (activeLimitDetails != null && !activeLimitDetails.isEmpty()) {
                        for (ActiveLimitDetail activeLimitDetail : activeLimitDetails) {
                            if (activeLimitDetail.getLimitType() == 2) {
                                //每人每天限购数量 + 时间 + 用户 + 商品
                                StringBuilder sb = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_RT);
                                sb.append(activeLimit.getLimitId()).append(DateUtils.SDF10.format(new Date())).append(":").append(userId).append(":").append(goodId);
                                redisTemplate.opsForValue().increment(sb.toString(), goodNum);
                                redisTemplate.expire(sb.toString(), getRemainTime(LocalDateTime.now()), TimeUnit.SECONDS);
                            } else if (activeLimitDetail.getLimitType() == 3) {
                                //每人限购数量 + 用户 + 商品
                                StringBuilder sb1 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_R);
                                sb1.append(activeLimit.getLimitId()).append(":").append(userId).append(":").append(goodId);
                                redisTemplate.opsForValue().increment(sb1.toString(), goodNum);
                                //获取限购截止时间
                                LocalDateTime localDateTime1 = activeLimit.getLimitTimeEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                                redisTemplate.expire(sb1.toString(), getRemainTime(localDateTime1), TimeUnit.SECONDS);
                            }
                        }
                    }

                    //限购数量 + 商品
                    if (activeLimit.getLimitNum() != null) {
                        StringBuilder sb2 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_TOTAL);
                        sb2.append(activeLimit.getLimitId()).append(":").append(goodId);
                        redisTemplate.opsForValue().increment(sb2.toString(), goodNum);
                        //获取限购截止时间
                        LocalDateTime localDateTime2 = activeLimit.getLimitTimeEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        redisTemplate.expire(sb2.toString(), getRemainTime(localDateTime2), TimeUnit.SECONDS);
                    }

                    //款式限定 + 类型 + 商品
                    if (activeLimit.getLimitGood() != null && activeLimit.getActiveGoods() != null && !activeLimit.getActiveGoods().isEmpty()) {
                        StringBuilder sb3 = new StringBuilder(ActiveGoodsConstants.LimitType.GOOD_LIMIT_GOODID);
                        sb3.append(activeLimit.getLimitId()).append(":").append(userId);
                        redisTemplate.opsForSet().add(sb3.toString(), activeType + goodId);
                        redisTemplate.expire(sb3.toString(), getRemainTime(LocalDateTime.now()), TimeUnit.SECONDS);
                    }
                }
            }
            if (flag) {
                redisTemplate.delete(orderKey);
            }
        }
    }

    @KafkaListener(topics = {MQTopicConstant.TOPIC_PRIZEWHEELS_INVITE})
    public void clickFriendsPrizewheelsShare(List<String> list) {
        for (String json : list) {
            PrizewheelsShare entity = JSONObject.parseObject(json, PrizewheelsShare.class);
            log.info("-->active模块监听到用户{}点击好友{}转盘分享邀请的消息", entity.getUserId(), entity.getShareUserId());
            prizewheelsFacadeService.clickFriendShare(entity.getUserId(), entity.getShareUserId());
        }
    }

    @KafkaListener(topics = {MQTopicConstant.TOPIC_GOODSHARE})
    public void clickFriendsGoodShare(List<String> list) {
        for (String json : list) {
            GoodShare entity = JSONObject.parseObject(json, GoodShare.class);
            log.info("-->active模块监听到用户{}点击好友{}商品分享的消息，商品ID：{}", entity.getUserId(), entity.getShareUserId(), entity.getGoodId());
            shareGoodService.shareGood(entity.getUserId(), entity.getShareUserId(), entity.getGoodId());
        }
    }

    /**
     * 处理公众号消息
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.WX_H5_MSG})
    public void handleOfficalAccountMsg(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String data : params) {
                log.info("-->active模块监听到用户回复公众号的主题消息：{}", data);
                JSONObject jsonObject = JSONObject.parseObject(data);
                // 调用转盘的方法
                activeAsyncService.handleOfficalAccountReplyForPrizewheels(jsonObject);
                //优惠券兑换码
                couponRedeemCodeService.kafkaExchangeCoupon(jsonObject);
                //TODO 其它模块的调用(异步)
            }
        }
    }

    /**
     * 同步关注信息-流量池
     *
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_USER_FOCUS_INFO})
    public void syncUserFocusInfo(List<String> params) {
        if (params != null && !params.isEmpty()) {
            for (String data : params) {
                List<FocusInfo> userInfos = JSON.parseArray(data, FocusInfo.class);
                focusInfoService.insertOrUpdateBatch(userInfos);
            }
        }
        EntityWrapper<FocusInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.ne("STATUS", 1);
        focusInfoService.delete(entityWrapper);
    }

    @KafkaListener(topics = {MQTopicConstant.SYNC_RECHARGE_RESULT})
    public void rechargeResult(String params) {
        RechargeDto rechargeDto = JSONObject.parseObject(params, RechargeDto.class);
        callChargeRecordService.callback(rechargeDto);
    }
}
