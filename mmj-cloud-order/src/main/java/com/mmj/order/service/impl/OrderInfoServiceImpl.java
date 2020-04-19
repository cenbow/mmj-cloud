package com.mmj.order.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.OrderClassify;
import com.mmj.common.constants.OrderStatus;
import com.mmj.common.constants.TemplateIdConstants;
import com.mmj.common.constants.UserConstant;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.BaseUser;
import com.mmj.common.model.Details;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;
import com.mmj.common.model.order.OrderProduceDto;
import com.mmj.common.model.order.OrderSearchConditionDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import com.mmj.common.model.order.OrderStore;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DateUtils;
import com.mmj.common.utils.OrderStoreUtils;
import com.mmj.common.utils.OrderUtils;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.common.utils.UserMergeProcessor;
import com.mmj.order.common.feign.ActiveFeignClient;
import com.mmj.order.common.feign.AfterSaleFeignClient;
import com.mmj.order.common.feign.ESearchFeignClient;
import com.mmj.order.common.feign.GoodFeignClient;
import com.mmj.order.common.feign.JunshuitanFeignClient;
import com.mmj.order.common.feign.PayFeignClient;
import com.mmj.order.common.feign.UserFeignClient;
import com.mmj.order.common.model.ActiveGood;
import com.mmj.order.common.model.dto.OrderAfterSaleDto;
import com.mmj.order.common.model.dto.OrderCheckDto;
import com.mmj.order.common.model.dto.OrderPaySuccessDto;
import com.mmj.order.common.model.vo.AddAfterSaleVo;
import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import com.mmj.order.common.model.vo.RedPackageUserVo;
import com.mmj.order.common.model.vo.ShopCartsAddVo;
import com.mmj.order.common.model.vo.UseUserCouponVo;
import com.mmj.order.common.model.vo.UserRecommendOrder;
import com.mmj.order.common.model.vo.UserShardVo;
import com.mmj.order.common.model.vo.WxpayOrderEx;
import com.mmj.order.constant.AfterSalesStatus;
import com.mmj.order.constant.DelFlagStatus;
import com.mmj.order.constant.MMKingShareType;
import com.mmj.order.constant.OrderGroupStatus;
import com.mmj.order.constant.OrderGroupType;
import com.mmj.order.constant.OrderQueryCategory;
import com.mmj.order.constant.OrderType;
import com.mmj.order.constant.OrderTypeStatus;
import com.mmj.order.constant.RedPackageType;
import com.mmj.order.mapper.OrderInfoMapper;
import com.mmj.order.mapper.OrderPaymentMapper;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderGroup;
import com.mmj.order.model.OrderGroupJoin;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderKing;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.OrderPackage;
import com.mmj.order.model.OrderPackageLog;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.dto.BossDetailDto;
import com.mmj.order.model.dto.GroupDto;
import com.mmj.order.model.dto.GroupInfoDto;
import com.mmj.order.model.dto.OrderDetailGroupDto;
import com.mmj.order.model.dto.OrderDetaisMemberDto;
import com.mmj.order.model.dto.OrderDetaislDto;
import com.mmj.order.model.dto.OrderGoodsDto;
import com.mmj.order.model.dto.OrderListDto;
import com.mmj.order.model.dto.OrderListLogisticsDto;
import com.mmj.order.model.dto.OrderLogisticsDto;
import com.mmj.order.model.dto.OrderPackageDto;
import com.mmj.order.model.dto.OrderPackageLogDto;
import com.mmj.order.model.dto.OrderPayinfoDto;
import com.mmj.order.model.dto.OrderStatsDto;
import com.mmj.order.model.dto.PassingDataDto;
import com.mmj.order.model.dto.PayInfoDto;
import com.mmj.order.model.dto.SMSInfoDto;
import com.mmj.order.model.dto.SaveOrderDto;
import com.mmj.order.model.dto.ShippingDto;
import com.mmj.order.model.request.OrdersUploadRequest;
import com.mmj.order.model.vo.BossDetailVo;
import com.mmj.order.model.vo.BossListVo;
import com.mmj.order.model.vo.CancelVo;
import com.mmj.order.model.vo.ConsignessVo;
import com.mmj.order.model.vo.DecrGoodNum;
import com.mmj.order.model.vo.LogisticsQueryVo;
import com.mmj.order.model.vo.LogisticsVo;
import com.mmj.order.model.vo.MemberOrderVo;
import com.mmj.order.model.vo.OrderAddressVo;
import com.mmj.order.model.vo.OrderAfterVo;
import com.mmj.order.model.vo.OrderDetailVo;
import com.mmj.order.model.vo.OrderFinishGoodVo;
import com.mmj.order.model.vo.OrderGoodVo;
import com.mmj.order.model.vo.OrderInfoGoodVo;
import com.mmj.order.model.vo.OrderListVo;
import com.mmj.order.model.vo.OrderSaveVo;
import com.mmj.order.model.vo.PollQueryResponse;
import com.mmj.order.model.vo.ReceiveOrderVo;
import com.mmj.order.model.vo.RemoveOrderVo;
import com.mmj.order.model.vo.UpdateStatusVo;
import com.mmj.order.model.vo.UploadErpVo;
import com.mmj.order.model.vo.UserOrderVo;
import com.mmj.order.service.GoodsStockService;
import com.mmj.order.service.OrderGoodService;
import com.mmj.order.service.OrderGroupJoinService;
import com.mmj.order.service.OrderGroupService;
import com.mmj.order.service.OrderInfoService;
import com.mmj.order.service.OrderKingService;
import com.mmj.order.service.OrderLogisticsService;
import com.mmj.order.service.OrderPackageLogService;
import com.mmj.order.service.OrderPackageService;
import com.mmj.order.service.OrderPaymentService;
import com.mmj.order.tools.OrderStockException;
import com.mmj.order.utils.MQProducer;
import com.mmj.order.utils.MessageUtils;
import com.mmj.order.utils.OrderGroupUtils;
import com.mmj.order.utils.OrderNoUtils;
import com.mmj.order.utils.OrderSearchSynchronizer;
import com.mmj.order.utils.PassingDataUtil;
import com.mmj.order.utils.PriceConversion;
import com.mmj.order.utils.RedPackCodeUtils;
import com.mmj.order.utils.SMSProcessor;
import com.xiaoleilu.hutool.util.RandomUtil;

/**
 * <p>
 * 订单信息表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-04
 */
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    private static final List<String> RANDOM_USER_PIC = Lists.newArrayListWithCapacity(10);
    private static final String USER_IDENTITY = "new";

    static {
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000034_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000048_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000058_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000070_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000076_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000077_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100000084_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100040468_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100040478_640_640.jpg");
        RANDOM_USER_PIC.add("https://dxs-mmj-userinfo-1257049906.cos.ap-guangzhou.myqcloud.com/100040480_640_640.jpg");
    }

    @Autowired
    private MQProducer mQProducer;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderGoodService orderGoodService;

    @Autowired
    private OrderPackageService orderPackageService;

    @Autowired
    private OrderPackageLogService orderPackageLogService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderGroupService orderGroupService;

    @Autowired
    private OrderGroupJoinService orderGroupJoinService;

    @Autowired
    private JunshuitanFeignClient junshuitanFeignClient;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private GoodsStockService goodsStockService;

    @Autowired
    private OrderPaymentMapper orderPaymentMapper;

    @Autowired
    private AfterSaleFeignClient afterSaleFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PayFeignClient payFeignClient;

    @Autowired
    private OrderLogisticsService orderLogisticsService;

    @Autowired
    private PayFeignClient payFeignClientl;

    @Autowired
    private OrderKingService okService;

    @Autowired
    private ActiveFeignClient activeFeignClient;

    @Autowired
    private ESearchFeignClient eSearchFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderSearchSynchronizer orderSearchSynchronizer;

    @Autowired
    private UserMergeProcessor userMergeProcessor;

    @Autowired
    private OrderNoUtils orderNoUtils;

    @Autowired
    private OrderStoreUtils orderStoreUtils;

    @Autowired
    private SMSProcessor smsProcessor;

    @Autowired
    private MessageUtils messageUtils;

    /**
     * 通过订单号取模
     *
     * @param orderNo
     */
    private void shardingKey(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
    }

    @Override
    public OrderInfo getByOrderNo(String orderNo) {
        shardingKey(orderNo);
        OrderInfo info = new OrderInfo();
        info.setOrderNo(orderNo);
        EntityWrapper wrapper = new EntityWrapper(info);
        info = selectOne(wrapper);
        return info;
    }

    /**
     * 获取当前用户/并验证用户是否存在
     *
     * @return
     */
    public JwtUserDetails currentUserDetails() {
        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUser, "未找到用户信息");
        return jwtUser;
    }

    /**
     * 生成订单
     *
     * @param orderSaveVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SaveOrderDto produce(OrderSaveVo orderSaveVo) {
        JwtUserDetails userDetails = currentUserDetails();
        Long userId = userDetails.getUserId();
        orderSaveVo.setAppId(userDetails.getAppId());
        orderSaveVo.setOpenId(userDetails.getOpenId());
        RLock fairLock = redissonClient.getFairLock("ORDER:PRODUCE:" + userId);
        fairLock.lock(3, TimeUnit.SECONDS);
        String orderNo = null;
        try {
            // 分表分库
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            //生成订单号
            orderNo = OrderUtils.gainOrderNo(userId, orderSaveVo.getOrderType(), orderSaveVo.getSource(), OrderClassify.MAIN);
//            BaseContextHandler.set(SecurityConstants.PRODUCE_FAIL_ORDER_NO, orderNo);
            //下单验证团订单参数
            OrderCheckDto orderCheckDto = checkGroupOrder(orderSaveVo, userId);
            // 分表分库-防止验证团信息时，分表标识被切换
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            // 效验库存和封装商品信息
            List<OrderGood> orderGoodList = checkGoods(orderSaveVo, orderCheckDto);
            //虚拟商品和非虚拟商品不能同时下单
            int virtualNum = 0;
            int goodsNum = 0;
            for (OrderGood orderGood : orderGoodList) {
                if ("1".equals(orderGood.getVirtualFlag())) {
                    virtualNum++;
                } else {
                    goodsNum++;
                }
            }
            Date createTime = new Date(); //下单时间
            if (virtualNum > 0) {
                Assert.isTrue(virtualNum == 1 && goodsNum == 0, "虚拟商品不可与实物商品同时结算，请调整下单商品");
            } else {
                //锁定库存15分钟
                orderGoodService.occupyStock(orderNo, orderSaveVo.getOrderType(), createTime, orderGoodList);
            }
            // 价格计算
            CartOrderGoodsDetails cartOrderGoodsDetails = checkOrderPrice(orderNo, orderSaveVo, orderGoodList, userId);
            Assert.notNull(cartOrderGoodsDetails, "价格计算异常");
            log.info("=> 价格计算 orderNo:{}，result:{}", orderNo, cartOrderGoodsDetails);
            // 保存订单信息
            SaveOrderDto saveOrderDto = saveOrderInfo(orderSaveVo, createTime, orderGoodList, orderNo, userId, cartOrderGoodsDetails, orderCheckDto);
            // 下单时使用优惠券
            if (StringUtils.isNotBlank(orderSaveVo.getCouponCode())) {
                log.info("当前用户:{},使用优惠券:{}", userId, orderSaveVo.getCouponCode());
                useCoupon(userId, orderNo, Integer.valueOf(orderSaveVo.getCouponCode()), true);
            }
            writeCache(saveOrderDto, userDetails.getUserFullName());
            //支付成功后拆单
            //同步订单到消息队列
            orderSearchSynchronizer.send(saveOrderDto);
            Date expirtTime = saveOrderDto.getOrderInfo().getExpirtTime();
            mQProducer.orderTimeout(saveOrderDto.getOrderInfo().getOrderNo(), expirtTime);
            //下单但未支付后半小时提醒
            mQProducer.orderWaitePayMsg(saveOrderDto.getOrderInfo().getOrderNo(), orderGoodList.get(0).getGoodName());
            return saveOrderDto;
        } catch (OrderStockException e) {
            if (Objects.nonNull(e.getOrderGoodList()) && e.getOrderGoodList().size() > 0) {
                orderGoodService.relieveStock(orderNo, orderSaveVo.getOrderType(), e.getOrderGoodList());
            }
            throw new IllegalArgumentException("下单失败:库存扣减失败");
        } catch (Exception e) {
            if (Objects.nonNull(orderNo)) {
                mQProducer.produceFail(orderNo);
            }
            log.error("=> 下单失败:{}", e.toString());
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 下单成功后将订单信息写入缓存
     *
     * @param saveOrderDto
     * @param userFullName
     */
    private void writeCache(SaveOrderDto saveOrderDto, String userFullName) {
        try {
            OrderInfo orderInfo = saveOrderDto.getOrderInfo();
            OrderLogistics orderLogistics = saveOrderDto.getOrderLogistics();
            OrderStore orderStore = new OrderStore();
            BeanUtils.copyProperties(orderInfo, orderStore);
            orderStore.setOrderNo(orderInfo.getOrderNo());
            orderStore.setUserId(orderInfo.getCreaterId());
            orderStore.setUserFullName(userFullName);
            List<OrderGood> orderGoodList = saveOrderDto.getOrderGoods();
            List<OrderStore.Goods> goodsList = Lists.newArrayListWithCapacity(orderGoodList.size());
            orderGoodList.forEach(orderGood -> {
                goodsList.add(new OrderStore.Goods(orderGood.getGoodName(), orderGood.getModelName(), orderGood.getGoodId(), orderGood.getGoodSpu(), orderGood.getSaleId(), orderGood.getGoodSku(), orderGood.getPriceType(), orderGood.getGoodPrice(), orderGood.getGoodAmount(), orderGood.getGoodNum()));
            });
            orderStore.setGoodsList(goodsList);
            orderStore.setConsignee(new OrderStore.Consignee(orderLogistics.getConsumerName(), orderLogistics.getConsumerMobile(), orderLogistics.getProvince(), orderLogistics.getCity(), orderLogistics.getArea(), orderLogistics.getConsumerAddr()));
            orderStoreUtils.write(orderStore);
        } catch (Exception e) {
            log.error("=> 下单成功后将订单信息写入缓存错误 error:{}", e.toString());
        }
    }

    /**
     * 活动订单下单参数验证
     *
     * @param orderSaveVo
     * @return
     */
    public OrderCheckDto checkGroupOrder(OrderSaveVo orderSaveVo, Long userId) {
        return orderGroupService.checkGroupOrder(orderSaveVo, userId);
    }


    /**
     * 使用与解绑优惠券
     *
     * @param userId
     * @param orderNo
     * @param couponCode
     * @param flag
     */
    public void useCoupon(Long userId, String orderNo, Integer couponCode, boolean flag) {
        UseUserCouponVo useUserCouponVo = new UseUserCouponVo();
        useUserCouponVo.setUserId(userId);
        useUserCouponVo.setOrderNo(orderNo);
        useUserCouponVo.setCouponCode(couponCode);
        useUserCouponVo.setUseStatus(flag);
        ReturnData returnData = userFeignClient.use(useUserCouponVo);
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode(), "优惠券处理失败");
    }

    /**
     * 下单价格校验
     *
     * @param orderSaveVo
     */
    public CartOrderGoodsDetails checkOrderPrice(String orderNo, OrderSaveVo orderSaveVo, List<OrderGood> orderGoodList, Long userId) {
        CartOrderGoodsDetails cartOrderGoodsDetails = new CartOrderGoodsDetails();
        int goodAmount = 0;
        List<Details> detailsList = new ArrayList<>();

        if (Objects.nonNull(orderSaveVo.getKingSelected())
                && orderSaveVo.getKingSelected()
                && Objects.nonNull(orderSaveVo.getUseKingNum())
                && orderSaveVo.getUseKingNum() > 0) {
            Assert.notNull(orderSaveVo.getExchangeMoney(), "缺少买买金兑换金额");
            //查询冻结的买买金
            ReturnData<Boolean> data = userFeignClient.verify(userId, orderSaveVo.getUseKingNum());
            Assert.notNull(data, "使用买买金异常");
            if (data.getCode() == 0)
                Assert.isTrue(false, data.getDesc());
            Assert.isTrue(data.getData(), "买买金不足");
            cartOrderGoodsDetails.setUseKingNum(orderSaveVo.getUseKingNum());
            cartOrderGoodsDetails.setExchangeMoney(Double.parseDouble(orderSaveVo.getExchangeMoney()));
            cartOrderGoodsDetails.setKingSelected(orderSaveVo.getKingSelected());
        }

        if (OrderType.MM_KING == orderSaveVo.getOrderType()) {
            //查询冻结的买买金
            ReturnData<Boolean> data = userFeignClient.verify(userId, orderSaveVo.getUseKingNum());
            Assert.notNull(data, "使用买买金异常!");
            if (data.getCode() == 0)
                Assert.isTrue(false, data.getDesc());
            Assert.isTrue(data.getData(), "买买金不足!");
            cartOrderGoodsDetails.setUseKingNum(orderSaveVo.getUseKingNum());
            cartOrderGoodsDetails.setExchangeMoney(Double.parseDouble(orderSaveVo.getExchangeMoney()));
            cartOrderGoodsDetails.setKingSelected(orderSaveVo.getKingSelected());
        }
        cartOrderGoodsDetails.setOrderNo(orderNo);
        cartOrderGoodsDetails.setUserid(userId);
        cartOrderGoodsDetails.setCouponCode(orderSaveVo.getCouponCode());
        cartOrderGoodsDetails.setOrderType(orderSaveVo.getOrderType());
        cartOrderGoodsDetails.setPassingData(orderSaveVo.getPassingData());
        cartOrderGoodsDetails.setBusinessId(orderSaveVo.getBusinessId());
        OrderGood orderGood;
        for (int i = 0, size = orderGoodList.size(); i < size; i++) {
            Details details = new Details();
            orderGood = orderGoodList.get(i);
            details.setOrderType(orderSaveVo.getOrderType());
            details.setSaleId(String.valueOf(orderGood.getSaleId()));
            details.setCount(orderGood.getGoodNum());
            int priceType = Objects.isNull(orderGood.getPriceType()) ? 0 : orderGood.getPriceType();
            switch (priceType) {
                case 2: //原价
                    orderGood.setGoodPrice(orderGood.getGoodAmount());
                    break;
                case 1: //会员价
                    orderGood.setGoodPrice(orderGood.getMemberPrice());
                    break;
                default:
                    break;
            }
            details.setUnitPrice(Double.parseDouble(PriceConversion.intToString(orderGood.getGoodPrice())));
            details.setMemberPrice(Double.parseDouble(PriceConversion.intToString(orderGood.getMemberPrice())));
            details.setGoodId(orderGood.getGoodId());
            details.setGoodSku(orderGood.getGoodSku());
            details.setBusinessId(orderSaveVo.getBusinessId());
            detailsList.add(details);
            goodAmount += orderGood.getGoodPrice() * orderGood.getGoodNum();
        }
        cartOrderGoodsDetails.setGoodsTotalPrice(Double.parseDouble(PriceConversion.intToString(goodAmount)));
        cartOrderGoodsDetails.setDetails(detailsList.toArray(new Details[detailsList.size()]));
        log.info("价格计算请求参数：{}", JSON.toJSONString(cartOrderGoodsDetails));
        ReturnData<CartOrderGoodsDetails> returnData = payFeignClient.calcFinalPrice(cartOrderGoodsDetails);
        Assert.isTrue(returnData != null && returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData() != null, returnData.getDesc());
        log.info("价格计算返回数据：{}", JSON.toJSONString(returnData.getData()));
        return returnData.getData();
    }

    /**
     * 商品封装及商品库存校验
     */
    public List<OrderGood> checkGoods(OrderSaveVo orderSaveVo, OrderCheckDto groupOrderCheckDto) {
        return orderGoodService.checkOrderGoods(orderSaveVo, groupOrderCheckDto);
    }

    /**
     * 保存订单信息
     *
     * @param orderSaveVo
     * @param orderNo
     */
    @Transactional(rollbackFor = Exception.class)
    public SaveOrderDto saveOrderInfo(OrderSaveVo orderSaveVo, Date createTime, List<OrderGood> orderGoodList, String orderNo, Long userId, CartOrderGoodsDetails cartOrderGoodsDetails, OrderCheckDto orderCheckDto) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        SaveOrderDto saveOrderDto = new SaveOrderDto();
        Date expireDate;
        if (orderSaveVo.getOrderType() == OrderType.SPIKE && Objects.nonNull(orderSaveVo.getBusinessId())) {
            Object spikeExpireTime = redisTemplate.opsForValue().get("GOOD:STOCK:OCCUPY:BUSINESS:" + orderSaveVo.getBusinessId());
            if (Objects.nonNull(spikeExpireTime)) {
                expireDate = new Date(Long.parseLong(spikeExpireTime.toString()));
            } else {
                expireDate = new Date(createTime.getTime() + 86400000); //一天过期时间
            }
        } else if (Objects.nonNull(orderCheckDto)) {
            expireDate = orderCheckDto.getExpireDate();
        } else {
            expireDate = new Date(createTime.getTime() + 86400000); //一天过期时间
        }

        if (null == expireDate)
            expireDate = new Date(createTime.getTime() + 86400000); //一天过期时间

        log.info("当前用户{},订单号{}，已经进入订单保存信息...", userId, orderNo);

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setOrderType(orderSaveVo.getOrderType());
        orderInfo.setOrderSource(orderSaveVo.getSource());
        orderInfo.setOrderStatus(OrderStatus.PENDING_PAYMENT.getStatus());
        orderInfo.setOrderAmount(PriceConversion.stringToInt(orderSaveVo.getOrderAmount()));
        orderInfo.setGoodAmount(PriceConversion.doubleToInt(cartOrderGoodsDetails.getGoodsTotalPrice()));
        orderInfo.setExpressAmount(PriceConversion.doubleToInt(cartOrderGoodsDetails.getFreight()));
        orderInfo.setOpenId(orderSaveVo.getOpenId());
        orderInfo.setAppId(orderSaveVo.getAppId());
        orderInfo.setOrderChannel(orderSaveVo.getChannel());
//        if (OrderType.BARGAIN == orderInfo.getOrderType()) {
//            orderInfo.setCouponAmount(0);   // 优惠金额
//            orderInfo.setDiscountAmount(PriceConversion.doubleToInt(cartOrderGoodsDetails.getPreferentialMoney())); // 折扣金额
//        } else {
//        }
        orderInfo.setCouponAmount(PriceConversion.doubleToInt(cartOrderGoodsDetails.getPreferentialMoney()));
        orderInfo.setDiscountAmount(PriceConversion.doubleToInt(cartOrderGoodsDetails.getDiscountAmount()));

        Double payPrice = cartOrderGoodsDetails.getPayPrice();
        log.info("-->支付金额两处校验，用户ID:{}, payPrice:{}, orderAmount:{}", userId, payPrice, orderSaveVo.getOrderAmount());
        if (orderSaveVo.getOrderType() != OrderType.MM_KING) {
            Assert.isTrue(payPrice >= 0 && payPrice.equals(Double.valueOf(orderSaveVo.getOrderAmount())), "下单失败：订单金额错误");
        }
        saveOrderDto.setOrderAmount(PriceConversion.intToString(PriceConversion.doubleToInt(payPrice)));
        Details[] details = cartOrderGoodsDetails.getDetails();
        orderGoodList.forEach(orderGood -> {
            List<Details> detailsList = Stream.of(details).filter(detail ->
                    detail.getGoodSku().equals(orderGood.getGoodSku())).collect(Collectors.toList());
            log.info("=> 价格计算，匹配商品 orderNo:{},sku:{},detailsList:{}", orderNo, orderGood.getGoodSku(), detailsList);
            if (Objects.nonNull(detailsList) && detailsList.size() > 0) {
                detailsList.forEach(detail -> {
                    orderGood.setLogisticsAmount(orderGood.getLogisticsAmount() + PriceConversion.doubleToInt(detail.getFreight()));
                    orderGood.setGoldPrice(orderGood.getGoldPrice() + PriceConversion.doubleToInt(detail.getExchangeMoney()));
                    orderGood.setDiscountAmount(orderGood.getDiscountAmount() + PriceConversion.doubleToInt(detail.getDiscountAmount()));
                    orderGood.setCouponAmount(orderGood.getCouponAmount() + PriceConversion.doubleToInt(detail.getPreferentialMoney()));
                });
            }
        });
        if (isGroupOrder(orderSaveVo.getOrderType())) {
            PassingDataDto passingData = PassingDataUtil.disPassingData(orderSaveVo.getPassingData());
            if (null == passingData || StringUtils.isBlank(passingData.getGroupNo())) {
                //团主
                //生成团号
                /**
                 * 1.保存团号到passingData
                 * 2.返回团号给前端
                 */
                String groupNo = OrderGroupUtils.genGroupNo(userId);
                passingData = null == passingData ? new PassingDataDto() : passingData;
                passingData.setBindGroupNo(groupNo);
                saveOrderDto.setGroupNo(groupNo);
                orderSaveVo.setPassingData(JSONObject.toJSONString(passingData));
            } else {
                saveOrderDto.setGroupNo(passingData.getGroupNo());
            }
        }
        // 买买金
        orderInfo.setGoldPrice(PriceConversion.doubleToInt(cartOrderGoodsDetails.getExchangeMoney()));
        orderInfo.setGoldNum(cartOrderGoodsDetails.getUseKingNum());
        orderInfo.setCreaterId(userId);
        orderInfo.setCreaterTime(createTime);
//        orderInfo.setModifyTime(date);
//        orderInfo.setConsumerDesc();
        orderInfo.setExpirtTime(expireDate);
        orderInfo.setDelFlag(1);  // 0 无效 ，1 有效
        orderInfo.setBusinessId(orderSaveVo.getBusinessId());
        orderInfo.setPassingData(orderSaveVo.getPassingData());
        orderInfo.setMemberOrder(orderSaveVo.getMemberOrder());
        orderInfo.setHasAfterSale(false);
        boolean result = insert(orderInfo);
        Assert.isTrue(result, "保存订单信息失败");
        log.info("当前用户:{},保存订单成功,订单号为:{},订单ID为:{}", userId, orderNo, orderInfo.getOrderId());
        //保存订单包裹信息
        OrderPackage orderPackage = orderPackageService.saveOrderPackage(orderInfo, null, 0, cartOrderGoodsDetails.getFreightFreeDesc());
        orderGoodList.forEach(orderGood -> {
            orderGood.setOrderId(orderInfo.getOrderId());
            orderGood.setOrderNo(orderNo);
            orderGood.setPackageNo(orderPackage.getPackageNo());
            orderGood.setCreaterId(userId);
        });
        //保存订单商品信息
        orderGoodService.saveOrderGoods(orderInfo, orderGoodList);
        //保存收货人信息
        OrderLogistics orderLogistics = orderLogisticsService.saveOrderLogistics(orderInfo, orderPackage, orderSaveVo.getConsigness());
        if (OrderType.LOTTERY == orderSaveVo.getOrderType()) {
            String key = CommonConstant.LOTTERY_JOIN_COUNT_PREFIX + orderSaveVo.getBusinessId() + ":" + orderInfo.getCreaterId();
            if (null == redisTemplate.opsForValue().get(key)) {
                redisTemplate.opsForValue().set(key, 1, CommonConstant.LOTTERY_CACHE_TIME, TimeUnit.HOURS);
            } else {
                redisTemplate.opsForValue().increment(key, 1);
            }
        }
        saveOrderDto.setOrderId(String.valueOf(orderInfo.getOrderId()));
        saveOrderDto.setOrderNo(orderNo);
        saveOrderDto.setOrderType(orderInfo.getOrderType());
        saveOrderDto.setUserId(orderInfo.getCreaterId());
        saveOrderDto.setOrderSaveVo(orderSaveVo);
        saveOrderDto.setOrderInfo(orderInfo);
        saveOrderDto.setOrderGoods(orderGoodList);
        saveOrderDto.setOrderLogistics(orderLogistics);
        return saveOrderDto;
    }

    /**
     * @param orderNo   买买家订单号
     * @param payAmount 订单金额
     * @param serialNum 外部支付单号
     * @param payDate   json数据
     * @desc 支付回调逻辑
     */
    @Override
    public void addOrderPayInfo(String orderNo, Integer payAmount, String serialNum, Date payDate, Long payUserId, String appId, String openId) {
        if (OrderType.RECHARGE == OrderUtils.getOrderType(orderNo)) {
            log.info("=> 话费订单忽略 orderNo:{},payAmount:{},serialNum:{}", orderNo, payAmount, serialNum);
            return;
        }
        OrderInfo order = this.getByOrderNo(orderNo);
        Long userId = order.getCreaterId();

        try {
            //查询用户支付之前的身份
            boolean oldUser = this.checkOldUser(Objects.isNull(payUserId) ? userId : payUserId);
            if (!oldUser) { //新用户
                activeFeignClient.updateIndexCode(USER_IDENTITY);   //修改首页版本号
                log.info("--> 新用户成为老用户，清除首页缓存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("--> 新用户成为老用户，清除首页缓存成功");
        }

        if (order == null) {
            log.error("=> 支付时订单未找到 orderNo:{}", orderNo);
            return;
        }
        log.info("当前订单号:{},状态为:{}", order.getOrderNo(), order.getOrderStatus());
        if (order.getOrderStatus() > OrderStatus.PENDING_PAYMENT.getStatus()) {
            log.error("订单已支付,orderNo:{},状态为:{}", orderNo, order.getOrderStatus());
            return;
        }
        // 支付回调，用户就为老用户
        Object obj = redisTemplate.opsForValue().get(UserConstant.IS_OLD_USER + userId);
        if ((Objects.isNull(obj) || !new Boolean(obj.toString()).booleanValue()) && OrderType.LOTTERY != order.getOrderType()) {
            redisTemplate.opsForValue().set(UserConstant.IS_OLD_USER + userId, true);
        }
        // 删除历史消费金额缓存
        redisTemplate.delete("COUSUMEMONEY:" + userId);
        OrderPayment payment = new OrderPayment();
        payment.setOrderNo(order.getOrderNo());
        payment.setCreaterTime(new Date());
        payment.setCreaterId(Objects.isNull(payUserId) ? userId : payUserId);
        payment.setOrderId(order.getOrderId());
        payment.setPayAmount(payAmount);
        payment.setPayStatus(1);    //1支付成功
        payment.setPayType("1");  //微信
        payment.setPayDesc("");
        payment.setPayTime(payDate);
        payment.setPayNo(serialNum);
        int cnt = countOrderPayment(orderNo);
        if (cnt == 0) {
            log.warn("当前订单号:{},已存在支付记录", orderNo);
        }
        boolean result = orderPaymentService.insert(payment);
        if (!result) {
            log.warn("当前订单号：{},支付金额:{},流水号:{},支付时间:{},支付失败", orderNo, payAmount, serialNum, payDate);
            return;
        }
        orderSearchSynchronizer.payOrder(orderNo, payAmount, new Date(), 1);
        log.info("当前订单号：{},支付金额:{},流水号:{},支付时间:{},支付成功", orderNo, payAmount, serialNum, payDate);
        List<String> orderNoList = Lists.newArrayListWithCapacity(1);
        orderNoList.add(orderNo);
        batchUpdateOrderStatus(orderNoList, OrderStatus.PAYMENTED.getStatus());
        boolean isSend = false;
        if (isGroupOrder(order.getOrderType())) {
            //处理拼团订单
            orderSearchSynchronizer.handlerGroupOrder(order);
        } else {
            isSend = true;
            mQProducer.sendPackageParse(order);
        }

        this.orderHandler(order);

        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        List<OrderGood> goods = orderGoodService.selectByMap(map);
        // 扣除库存
        dearGoodStock(order, goods, false);


        //  上传聚水潭标识
//        if (!isGroup)
//            abnormalOrder(order, goods, payDate, payAmount);

        // todo 生产环境时需要放开
//        groceryLlistUtils.addOrder(orderNo,3);
//        mQProducer.sendAllByPayCallBack(getOrderByPayCallBack(userId, orderNo));

        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setUserId(String.valueOf(userId));
        orderDetailVo.setOrderNo(orderNo);
        orderDetailVo.setAppId(appId);
        orderDetailVo.setOpenId(openId);
        //统计会员省钱、写入买送记录
        log.info("开始放入会员省钱kafka：{}", orderDetailVo.toString());
        mQProducer.addMoneyItem(orderDetailVo);
        //放入会员正在处理标识 MemberOngoing
        redisTemplate.opsForValue().set(UserConstant.ISMEMBERONGOING + userId, false);
        //删除订单支付成功后响应的标识
        redisTemplate.delete("wx_order:" + orderNo);
        this.sendOrderSMS(order, goods);
        //发送拼团模板消息
        if (isSend) {
            if (null == goods || goods.size() == 0) {
                log.info("订单商品为空，不发送模板消息");
                return;
            }
            OrderPaySuccessDto paySuccessDto = new OrderPaySuccessDto(order.getCreaterId(),
                    order.getOrderNo(), goods.get(0).getGoodName());
            paySuccessDto.setAmount(PriceConversion.intToString(payAmount));
            paySuccessDto.setOrderType(order.getOrderType());
            messageUtils.paySuccess(paySuccessDto);
        }
    }

    private void sendOrderSMS(OrderInfo order, List<OrderGood> goods) {
        if (null == goods || goods.size() == 0)
            return;

        log.info("支付成功后发送短信:{},{}", order.getOrderNo(), order.getOrderType());

        if (order.getOrderType() == OrderType.TEN_YUAN_SHOP) {
            SMSInfoDto infoDto = new SMSInfoDto(order.getCreaterId(),
                    order.getOrderNo(), goods.get(0).getGoodName());
            smsProcessor.sendTenShopPaidSMS(infoDto);
        }

        if (order.getOrderType() == OrderType.BARGAIN) {
            SMSInfoDto infoDto = new SMSInfoDto(order.getCreaterId(),
                    order.getOrderNo(), goods.get(0).getGoodName());
            smsProcessor.sendBargainPaidSMS(infoDto);
        }
        if (order.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
            SMSInfoDto infoDto = new SMSInfoDto(order.getCreaterId(),
                    order.getOrderNo(), goods.get(0).getGoodName());
            smsProcessor.sendTenPricePaidSMS(infoDto);
        }
        if (order.getOrderType() == OrderType.SPIKE) {
            SMSInfoDto infoDto = new SMSInfoDto(order.getCreaterId(),
                    order.getOrderNo(), goods.get(0).getGoodName());
            smsProcessor.sendFlashSalePaidSMS(infoDto);
        }
    }


    private void orderHandler(OrderInfo order) {
        switch (order.getOrderType()) {
            case OrderType.LOTTERY:
                mQProducer.addMMKing(order.getCreaterId(), MMKingShareType.LOTTERY);
                //模板消息
                break;
            case OrderType.TEN_YUAN_SHOP:
                break;
            case OrderType.TEN_FOR_THREE_PIECE:
                break;
            case OrderType.BARGAIN:
                break;
            case OrderType.ZERO_SHOPPING:
                break;
            case OrderType.RELAY_LOTTERY:
                mQProducer.addMMKing(order.getCreaterId(), MMKingShareType.LOTTERY);
                break;
            case OrderType.NEW_CUSTOMER_FREE_POST:
                break;
            case OrderType.OTHER_CHANNELS:
                break;
            case OrderType.FREE_ORDER:
                freeOrderPayHandler(order);
                break;
            case OrderType.MM_KING:
                break;
        }
    }

    private void freeOrderPayHandler(OrderInfo order) {
        addFreeOrder(order);

        UserShardVo shardVo = null;
        try {
            ReturnData<UserShardVo> data = userFeignClient.getFreeOrderRelation(order.getCreaterId());
            if (data.getCode() == 1) {
                shardVo = data.getData();
            } else {
                log.info("查询免费送关系异常:{}", data.getDesc());
            }
        } catch (Exception e) {
            log.error("查询免费送邀请人报错: " + e.getMessage(), e);
            return;
        }

        if (null == shardVo || null == shardVo.getOrderNo()
                || null == shardVo.getShardFrom())
            //未绑定关系
            return;


        if (shardVo.getShardFrom().longValue() == order.getCreaterId()) //自己不用发送模板消息
            return;

        if (1 == shardVo.getUserFlag()) {
            //老用户
            userFeignClient.delFreeOrderRelation(order.getCreaterId());
            // TODO: 2019/7/3 发送老用户加入模板消息
        }
    }

    private void addFreeOrder(OrderInfo orderInfo) {
        log.info("免费送订单支付:{}", orderInfo);
        JSONObject object = JSONObject.parseObject(orderInfo.getPassingData());
        if (!object.containsKey("groupPeople"))
            return;
        int groupPeople = object.getInteger("groupPeople");
        OrderGroup group = new OrderGroup();
        group.setGroupStatus(OrderGroupStatus.JOINING.getStatus());
        group.setGroupType(OrderGroupType.FREE_ORDER.getType());
        group.setBusinessId(orderInfo.getBusinessId());
        group.setLaunchUserId(orderInfo.getCreaterId());
        group.setLaunchOrderNo(orderInfo.getOrderNo());
        group.setGroupNo(OrderGroupUtils.genMFSGroupNo());
        group.setDeleteFlag(1);
        group.setCreaterTime(new Date());
        group.setPassingData(orderInfo.getPassingData());
        group.setCreaterId(orderInfo.getCreaterId());
        group.setCurrentPeople(0);
        group.setGroupPeople(groupPeople);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, group.getGroupNo());
        boolean result = orderGroupService.insert(group);
        log.info("创建免费送团:{},团信息:{}", result, group);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, null);
    }

    /**
     * 扣除库存
     *
     * @param orderInfo
     * @param goods
     * @param isAdd     是否增家库存 true返回库存 false扣减库存
     */
    public void dearGoodStock(OrderInfo orderInfo, List<OrderGood> goods, boolean isAdd) {
        log.info("支付回调已经开始进入扣除库存方法中.....");
        switch (orderInfo.getOrderType()) {
            case OrderType.RELAY_LOTTERY:
            case OrderType.LOTTERY:
            case OrderType.BARGAIN:
                log.info("不扣取库存!");
                break;
            default:
                if (isAdd) {
                    orderGoodService.relieveStock(orderInfo.getOrderNo(), orderInfo.getOrderType(), goods);
                } else {
                    orderGoodService.deductStock(orderInfo.getOrderNo(), orderInfo.getOrderType(), goods);
                }
                break;
        }
    }

    /**
     * 聚水潭取消订单
     *
     * @param map
     */
    @Override
    public void jstCancelOrder(Map<String, String> map) {
//        String packageNo = map.get("packageNo");
//        String orderNo;
//        if (packageNo.indexOf("-") != -1) {
//            orderNo = packageNo.split("-")[0];
//        } else {
//            OrderPackage orderPackage = orderPackageService.selectByPackageNo(packageNo);
//            if (Objects.nonNull(orderPackage)) {
//                orderNo = orderPackage.getOrderNo();
//            } else {
//                log.error("=> 聚水潭取消订单错误 result:{}", map);
//                return;
//            }
//        }
//
//        log.info("聚水潭取消订单--当前包裹号为:{},订单号为:{}", packageNo, orderNo);
//        OrderInfo orderInfo = orderInfoMapper.selectByOrderNo(orderNo);
//        log.info("聚水潭取消订单--当前用户id为:{}", orderInfo.getCreaterId());
//        if (orderInfo == null) {
//            return;
//        } else {
//            Long userId = orderInfo.getCreaterId();
//            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
//            // 修改订单状态
//            updateOrderStatus(OrderStatus.CANCELLED.getStatus(), orderNo);
//        }
    }

    @Override
    public void toBeDelivered(String... orderNos) {
        log.info("=> 订单发货，订单号:{}", orderNos);
        if (Objects.isNull(orderNos))
            return;
        Map<Long, List<String>> moldMap = handlerOrderDelivery(orderNos);
        moldMap.forEach((k, v) -> orderPackageService.orderToBeDelivered(v));
    }

    @Override
    public void toBeAwarded(String... orderNos) {
        log.info("=> 团订单开奖，订单号:{}", orderNos);
        if (Objects.isNull(orderNos))
            return;
        Map<Long, List<String>> moldMap = handlerOrderDelivery(orderNos);
        moldMap.forEach((k, v) -> {
            boolean result = batchUpdateOrderStatus(v, OrderStatus.TO_BE_AWARDED.getStatus());
            result = orderPackageService.batchUpdateOrderPackageStatusByOrderNo(v, OrderStatus.TO_BE_AWARDED.getStatus());
        });
    }

    @Override
    public void pendingSharing(String... orderNos) {
        log.info("=> 团订单待分享，订单号:{}", orderNos);
        if (Objects.isNull(orderNos))
            return;
        Map<Long, List<String>> moldMap = handlerOrderDelivery(orderNos);
        moldMap.forEach((k, v) -> {
            boolean result = batchUpdateOrderStatus(v, OrderStatus.TO_BE_A_GROUP.getStatus());
            result = orderPackageService.batchUpdateOrderPackageStatusByOrderNo(v, OrderStatus.TO_BE_A_GROUP.getStatus());
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void closeOrder(String... orderNos) {
        log.info("=> 关闭订单，订单号:{}", orderNos);
        if (Objects.isNull(orderNos))
            return;
        Map<Long, List<String>> moldMap = handlerOrderDelivery(orderNos);
        moldMap.forEach((k, v) -> {
            boolean result = batchUpdateOrderStatus(v, OrderStatus.CLOSED.getStatus());
            result = orderPackageService.batchUpdateOrderPackageStatusByOrderNo(v, OrderStatus.CLOSED.getStatus());
            v.forEach(orderNo -> {
                OrderInfo orderInfo = getByOrderNo(orderNo);
                List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderNo);

                if (Objects.nonNull(orderInfo.getCouponAmount()) && orderInfo.getCouponAmount() > 0) {
                    //解绑优惠券
                    useCoupon(orderInfo.getCreaterId(), orderInfo.getOrderNo(), null, false);
                }
                // 扣除库存;
//                dearGoodStock(orderInfo, orderGoodList, true);
                orderGoodService.rollbackStock(orderInfo.getOrderNo(), orderInfo.getOrderType(), orderGoodList);
            });
        });
    }

    /**
     * 订单号批量取模归类
     *
     * @param orderNos
     * @return
     */
    private Map<Long, List<String>> handlerOrderDelivery(String... orderNos) {
        Map<Long, List<String>> moldMap = Maps.newHashMapWithExpectedSize(orderNos.length);
        Arrays.stream(orderNos).forEach(orderNo -> {
            long mold = orderNoUtils.getDelivery(orderNo);
            List<String> orderNoList = moldMap.get(mold);
            if (Objects.isNull(orderNoList)) {
                orderNoList = Lists.newArrayListWithCapacity(orderNos.length);
            }
            orderNoList.add(orderNo);
            moldMap.put(mold, orderNoList);
        });
        return moldMap;
    }

    /**
     * 修改订单状态（主订单/包裹订单）
     *
     * @param orderStatus
     * @param orderNos
     * @return
     */
    @Override
    public boolean updateOrderStatus(Integer orderStatus, String... orderNos) {
        List<String> orderNoList = Stream.of(orderNos).collect(Collectors.toList());
        boolean result = batchUpdateOrderStatus(orderNoList, orderStatus);
        if (result)
            return orderPackageService.batchUpdateOrderPackageStatusByOrderNo(orderNoList, orderStatus);
        return false;
    }

    /**
     * 批量修改主订单状态
     *
     * @param orderNos
     * @param orderStatus
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateOrderStatus(List<String> orderNos, Integer orderStatus) {
        log.info("=> 修改主订单状态 订单号:{}，修改状态值:{}", orderNos, orderStatus);
        orderNoUtils.shardingKey(orderNos.get(0));
        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setOrderStatus(orderStatus);
        EntityWrapper<OrderInfo> orderInfoEntityWrapper = new EntityWrapper<>();
        orderInfoEntityWrapper.in("ORDER_NO", orderNos);
        boolean result = update(updateOrderInfo, orderInfoEntityWrapper);
        if (result) {
            orderNos.forEach(orderNo -> {
                orderSearchSynchronizer.updateStatus(orderNo, orderStatus);
            });
        }
        return result;
    }

    private int countOrderPayment(String orderNo) {
        OrderPayment payment = new OrderPayment();
        payment.setOrderNo(orderNo);
        payment.setPayStatus(1);
        EntityWrapper wrapper = new EntityWrapper(payment);
        return orderPaymentService.selectCount(wrapper);
    }

    /**
     * 判断是否团订单
     *
     * @param orderType
     * @return
     */
    private boolean isGroupOrder(Integer orderType) {
        return OrderUtils.isGroupOrder(orderType);
    }

    /**
     * 获取小程序订单列表
     *
     * @param orderListVo
     */
    @Override
    public Page<OrderListDto> getOrderList(OrderListVo orderListVo) {
        log.info("小程序查询订单列表参数:{}", JSON.toJSONString(orderListVo));
        JwtUserDetails userDetails = currentUserDetails();
        Long userId = userDetails.getUserId();
        OrderQueryCategory orderQueryCategory = OrderQueryCategory.valueOf(orderListVo.getCategory());
        Page<OrderListDto> page = new Page<>(orderListVo.getCurrentPage(), orderListVo.getPageSize());
        List<OrderListDto> orderList = new ArrayList<>();
        final List<OrderInfo> orderInfoList = Lists.newArrayList();
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<OrderInfo> wrapper = new EntityWrapper<>();
            wrapper.eq("DEL_FLAG", DelFlagStatus.DEL_STATUS.getStatus());
            if (orderQueryCategory.getStatus() != 0) {
                if ("WAIT_GROUP".equals(orderListVo.getCategory())) {
                    wrapper.eq("ORDER_STATUS", orderQueryCategory.getStatus());
                } else {
                    wrapper.eq("ORDER_STATUS", orderQueryCategory.getStatus());
                }

                wrapper.eq("HAS_AFTER_SALE", false);
            }
            wrapper.in("CREATER_ID", v);
            wrapper.orderDesc(Arrays.asList("CREATER_TIME", "ORDER_STATUS"));
            orderInfoList.addAll(orderInfoMapper.selectPage(page, wrapper));
        });

        List<OrderInfo> list = orderInfoList.stream().sorted(new Comparator<OrderInfo>() {
            @Override
            public int compare(OrderInfo o1, OrderInfo o2) {
                Integer o1Status = o1.getOrderStatus();
                Integer o2Status = o2.getOrderStatus();
                return o1Status < o2Status ? -1 : o2.getCreaterTime().compareTo(o1.getCreaterTime());
            }
        }).limit(page.getSize()).collect(Collectors.toList());

        HashMap<String, Object> map = new HashMap<>();
        Map<Long, List<String>> hasRecommendMap = Maps.newHashMapWithExpectedSize(list.size());
        list.stream().forEach(r -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, r.getCreaterId());
            OrderListDto orderListDto = new OrderListDto();
            orderListDto.setOrderNo(r.getOrderNo());
            orderListDto.setOrderStatus(
                    (r.getOrderStatus().intValue() == OrderStatus.PENDING_PAYMENT.getStatus()
                            && System.currentTimeMillis() - r.getCreaterTime().getTime() > 86400000)
                            ? OrderStatus.CANCELLED.getStatus() : r.getOrderStatus());
            orderListDto.setOrderStatusDesc(OrderStatus.toStatusMessage(r.getOrderStatus()));
            orderListDto.setOrderAmount(PriceConversion.intToString(r.getOrderAmount()));
            orderListDto.setOrderType(r.getOrderType());
            orderListDto.setOrderTypeDesc(OrderTypeStatus.OrderTypeStatus(r.getOrderType()));
            orderListDto.setCreateDate(DateUtils.getDate(r.getCreaterTime(), "yyyy-MM-dd HH:mm:ss"));
            if (Objects.nonNull(r.getExpirtTime())) {
                orderListDto.setExpireDate(DateUtils.getDate(r.getExpirtTime(), "yyyy-MM-dd HH:mm:ss"));
            }

            //订单状态转换
            if (null != r.getOrderStatus() && r.getOrderStatus() == OrderStatus.PAYMENTED.getStatus()
                    && isGroupOrder(r.getOrderType())) {
                //查询订单团的状态
                OrderStatus orderStatus = groupStatus2OrderStatus(r.getOrderStatus(), r.getPassingData());
                if (null != orderStatus) {
                    orderListDto.setOrderStatus(orderStatus.getStatus());
                    orderListDto.setOrderStatusDesc(orderStatus.getMessage());
                }
            } else {
                orderListDto.setOrderStatus(r.getOrderStatus());
                orderListDto.setOrderStatusDesc(OrderStatus.toStatusMessage(r.getOrderStatus()));
            }

            if (null == orderListDto.getOrderStatus() || null == orderListDto.getOrderStatusDesc()) {
                orderListDto.setOrderStatus(r.getOrderStatus());
                orderListDto.setOrderStatusDesc(OrderStatus.toStatusMessage(r.getOrderStatus()));
            }

            //   订单商品封装
            map.put("ORDER_NO", r.getOrderNo());
            List<OrderGoodsDto> goods = new ArrayList<>();
            List<OrderGood> orderGoodList = orderGoodService.selectByMap(map);
            for (OrderGood anOrderGoodList : orderGoodList) {
                OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
                orderGoodsDto.setGoodId(anOrderGoodList.getGoodId());
                orderGoodsDto.setGoodSku(anOrderGoodList.getGoodSku());
                orderGoodsDto.setSaleId(anOrderGoodList.getSaleId());
                orderGoodsDto.setGoodTitle(anOrderGoodList.getGoodName());
                orderGoodsDto.setGoodImage(anOrderGoodList.getGoodImage());
                orderGoodsDto.setGoodNum(anOrderGoodList.getGoodNum());
                orderGoodsDto.setModelName(anOrderGoodList.getModelName());
                orderGoodsDto.setCreateTime(anOrderGoodList.getCreaterTime());
                orderGoodsDto.setVirtualGoodFlag(anOrderGoodList.getVirtualFlag());
//                orderGoodsDto.setGoodAmount(anOrderGoodList.getGoodAmount().toString());
//                orderGoodsDto.setCouponAmount(anOrderGoodList.getCouponAmount().toString());
//                orderGoodsDto.setDiscountAmount(anOrderGoodList.getDiscountAmount().toString());
//                orderGoodsDto.setUnitPrice(PriceConversion.intToString(anOrderGoodList.getGoodPrice()));
//                orderGoodsDto.setOriginalPrice(PriceConversion.intToString(anOrderGoodList.getGoodAmount()));
                goods.add(orderGoodsDto);
            }
            orderListDto.setGood(goods);

            // 快递封装
            List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(r.getOrderNo(), r.getCreaterId());
            List<OrderListLogisticsDto> logistics = new ArrayList<>();
            for (OrderLogistics anOrderLogisticsList : orderLogisticsList) {
                OrderListLogisticsDto orderListLogisticsDto = new OrderListLogisticsDto();
                orderListLogisticsDto.setPackageNo(anOrderLogisticsList.getPackageNo());
                orderListLogisticsDto.setLogisticsNo(anOrderLogisticsList.getLogisticsNo());
                orderListLogisticsDto.setLogisticsName(anOrderLogisticsList.getCompanyName());
                orderListLogisticsDto.setOrderStatus(r.getOrderStatus());
                logistics.add(orderListLogisticsDto);
            }
            orderListDto.setLogistics(logistics);
            orderList.add(orderListDto);
            //  商品推荐
            if (hasRecommend(r)) {
                List<String> orderNoList = hasRecommendMap.get(r.getCreaterId());
                if (Objects.isNull(orderNoList)) {
                    orderNoList = Lists.newArrayListWithCapacity(list.size());
                }
                orderNoList.add(r.getOrderNo());
                hasRecommendMap.put(r.getCreaterId(), orderNoList);
            }
        });
        //  商品推荐
        hasRecommendMap.forEach((k, v) -> {
            if (v.size() > 0) {
                try {
                    HashMap<String, Object> maps = new HashMap<>();
                    maps.put("createrId", k);
                    maps.put("orderNoList", v);
                    log.info("订单列表开始调用商品推荐接口,用户id为:{},订单号为:{}", k, v);
                    ReturnData<List<UserRecommendOrder>> listReturnData = userFeignClient.selectByOrderNo(maps);
                    log.info("订单列表开始调用商品推荐接口结果为:{}", listReturnData);
                    if (listReturnData != null && listReturnData.getData() != null) {
                        List<UserRecommendOrder> userRecommendOrderList = listReturnData.getData();
                        orderList.forEach(orderListDto -> {
                            Optional<UserRecommendOrder> recommendOrder = userRecommendOrderList.stream().filter(ro -> orderListDto.getOrderNo().equals(ro.getOrderNo())).findFirst();
                            if (recommendOrder.isPresent()) {
                                orderListDto.setHasRecommend(recommendOrder.get().getStatus());
                                orderListDto.setRecommendId(recommendOrder.get().getRecommendId());
                            }
                        });
                    }
                } catch (Exception e) {
                    log.info("调用订单是展示 去写推荐 or 分享得返现 方法异常:" + e);
                }
            }
        });
        page.setRecords(orderList);
        return page;
    }

    /**
     * 判断是否可进行商品推荐
     *
     * @param r
     * @return
     */
    private boolean hasRecommend(OrderInfo r) {
        long sysTime = System.currentTimeMillis();
        return r.getOrderType() != OrderType.LOTTERY && r.getOrderType() != OrderType.RELAY_LOTTERY
                && r.getOrderType() != OrderType.MM_KING
                && r.getOrderType() != OrderType.RECHARGE &&
                r.getOrderStatus() == OrderStatus.COMPLETED.getStatus() &&
                sysTime - r.getCreaterTime().getTime() < 7776000000l;
    }


    private OrderStatus groupStatus2OrderStatus(Integer orderStatus, String passData) {
        if (null == orderStatus || StringUtils.isBlank(passData))
            return null;
        log.info("修改团购订单状态,orderStatus:{},passData:{}", orderStatus, passData);
        String groupNo = null;
        PassingDataDto dto = PassingDataUtil.disPassingData(passData);
        if (null == dto)
            return null;
        if (StringUtils.isNotEmpty(dto.getGroupNo())) {
            groupNo = dto.getGroupNo();
        } else if (StringUtils.isNotEmpty(dto.getBindGroupNo())) {
            groupNo = dto.getBindGroupNo();
        }
        if (null == groupNo) {
            log.info("拼团订单却不存在团号:{}", passData);
            return null;
        }

        OrderGroup group = orderGroupService.getByGroupNo(groupNo);
        if (null == group) {
            log.info("拼团订单团不存在:{}", groupNo);
            return null;
        }
        if (orderStatus == OrderStatus.PAYMENTED.getStatus()) {
            //二人团、抽奖已支付状态,--> 待成团
            return OrderStatus.TO_BE_A_GROUP;
        } else if (orderStatus == OrderStatus.TO_BE_AWARDED.getStatus() && group.getGroupStatus() == OrderGroupStatus.COMPLETED.getStatus()) {
            return OrderStatus.TO_BE_AWARDED;
        }
        return null;
    }

    /**
     * 小程序订单详情
     *
     * @param orderDetailVo
     * @return
     */
    @Override
    public OrderDetaislDto getDetails(OrderDetailVo orderDetailVo) {
        log.info("进入当前订单号{}的详情..", orderDetailVo.getOrderNo());
        shardingKey(orderDetailVo.getOrderNo());
        OrderDetaislDto orderDetaislDto = new OrderDetaislDto();
        OrderInfo orderInfo = getByOrderNo(orderDetailVo.getOrderNo());

        log.info("订单号{}的详情为:", orderDetailVo.getOrderNo(), orderInfo);

        orderInfoDetail(orderDetaislDto, orderInfo);
        orderPackageDetail(orderInfo, orderDetaislDto, orderDetailVo.getOrderNo(), orderDetailVo.getUserId());
        orderAfterSale(orderInfo, orderDetaislDto, orderDetailVo.getOrderNo(), orderDetailVo.getUserId());
        return orderDetaislDto;
    }


    /**
     * 封装售后信息与收件人信息
     *
     * @param orderDetaislDto
     * @param orderNo
     * @param userId
     */
    @Transactional(rollbackFor = Exception.class)
    public void orderAfterSale(OrderInfo orderInfo, OrderDetaislDto orderDetaislDto, String orderNo, String userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        log.info("当前订单号{}进入售后信息封装!", orderNo);

//        AddAfterSaleVo addAfterSaleVo = new AddAfterSaleVo();
//        addAfterSaleVo.setUserId(userId);
//        addAfterSaleVo.setOrderNo(orderNo);
//        try {
//            ReturnData<AfterSaleDto> returnData = afterSaleFeignClient.getAfterSaleInfo(addAfterSaleVo);
//            if (returnData != null && returnData.getData() != null) {
//                AfterSaleDto afterSaleDtos = returnData.getData();
//                /*  orderDetaislDto.setAfterSaleDto(afterSaleDtos);*/
//            }
//        } catch (Exception e) {
//            log.info("订单详情调用售后信息异常");
//        }


        List<OrderLogistics> orderLogisticsList = orderLogisticsService.getOrderLogistics(orderNo, Long.valueOf(userId));
        if (orderLogisticsList != null && orderLogisticsList.size() > 0) {
            OrderLogistics orderLogistics = orderLogisticsList.get(0);
            OrderLogisticsDto orderLogisticsDto = new OrderLogisticsDto();
            orderLogisticsDto.setProvince(orderLogistics.getProvince());
            orderLogisticsDto.setCity(orderLogistics.getCity());
            orderLogisticsDto.setArea(orderLogistics.getArea());
            orderLogisticsDto.setConsumerAddr(orderLogistics.getConsumerAddr());
            orderLogisticsDto.setConsumerMobile(orderLogistics.getConsumerMobile());
            orderLogisticsDto.setConsumerName(orderLogistics.getConsumerName());
            if (orderLogistics.getSendTime() != null) {
                orderLogisticsDto.setSendTime(DateUtils.getDate(orderLogistics.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            if (orderLogistics.getCheckTime() != null) {
                orderLogisticsDto.setCheckTime(DateUtils.getDate(orderLogistics.getCheckTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            orderDetaislDto.setOrderLogistics(orderLogisticsDto);
        } else {
            OrderLogisticsDto orderLogisticsDto = new OrderLogisticsDto();
            if (orderInfo.getOrderType() == OrderType.RECHARGE && StringUtils.isNotEmpty(orderInfo.getPassingData())) {
                JSONObject jsonObject = JSON.parseObject(orderInfo.getPassingData());
                String phoneNumber = jsonObject.getString("phoneNumber");
                orderLogisticsDto.setConsumerMobile(StringUtils.isNotBlank(phoneNumber) ? phoneNumber : "");
            }
            orderDetaislDto.setOrderLogistics(orderLogisticsDto);
        }


    }


    // 封装订单信息表
    public void orderInfoDetail(OrderDetaislDto orderDetaislDto, OrderInfo orderInfo) {
        log.info("当前订单号{}进入主表信息封装", orderInfo.getOrderNo());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderInfo.getCreaterId());
        orderDetaislDto.setOrderNo(orderInfo.getOrderNo());
        orderDetaislDto.setOrderStatus(orderInfo.getOrderStatus());
        orderDetaislDto.setOrderType(orderInfo.getOrderType());
        orderDetaislDto.setOrderStatusDesc(OrderStatus.toStatusMessage(orderInfo.getOrderStatus()));
        orderDetaislDto.setOrderAmount(PriceConversion.intToString(orderInfo.getOrderAmount()));
        orderDetaislDto.setCouponAmount(PriceConversion.intToString(orderInfo.getCouponAmount()));
        orderDetaislDto.setGoodAmount(PriceConversion.intToString(orderInfo.getGoodAmount()));
        orderDetaislDto.setDiscountAmount(PriceConversion.intToString(orderInfo.getDiscountAmount()));
        orderDetaislDto.setFreight(PriceConversion.intToString(orderInfo.getExpressAmount()));
        orderDetaislDto.setCreateDate(DateUtils.getDate(orderInfo.getCreaterTime(), "yyyy-MM-dd HH:mm:ss"));
        if (Objects.nonNull(orderInfo.getExpirtTime())) {
            orderDetaislDto.setExpireDate(DateUtils.getDate(orderInfo.getExpirtTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        orderDetaislDto.setMemberOrder(orderInfo.getMemberOrder());
        //订单状态转换
        if (null != orderInfo.getOrderStatus() && orderInfo.getOrderStatus() == OrderStatus.PAYMENTED.getStatus()
                && isGroupOrder(orderInfo.getOrderType())) {
            //查询订单团的状态
            OrderStatus orderStatus = groupStatus2OrderStatus(orderInfo.getOrderStatus(), orderInfo.getPassingData());
            if (null != orderStatus) {
                orderDetaislDto.setOrderStatus(orderStatus.getStatus());
                orderDetaislDto.setOrderStatusDesc(orderStatus.getMessage());
            }
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderInfo.getOrderNo());
        List<OrderPackage> orderPackageList = orderPackageService.selectByMap(map);
        if (orderPackageList != null && orderPackageList.size() > 0) {
            OrderPackage orderPackage = orderPackageList.get(0);
            if (StringUtils.isNotBlank(orderPackage.getPackageDesc())) {
                orderDetaislDto.setFreightRemarks(orderPackage.getPackageDesc());
            } else {
                orderDetaislDto.setFreightRemarks("");
            }
        } else {
            orderDetaislDto.setFreightRemarks("");
        }

        if (isGroupOrder(orderInfo.getOrderType())) {
            orderDetaislDto.setGroupNo(getOrderGroupNo(orderInfo.getPassingData()));
        }
        if (orderInfo.getOrderType().equals(OrderType.FREE_ORDER)) {
            //免费送查询团号
            OrderGroup og = orderGroupService.getByOrderNo(orderInfo.getOrderNo());
            if (null != og) {
                orderDetaislDto.setGroupNo(og.getGroupNo());
            }
        }
        List<OrderPayment> orderPaymentList = orderPaymentService.getOrderPayment(orderInfo.getOrderNo(), orderInfo.getCreaterId());
        if (orderPaymentList != null && orderPaymentList.size() > 0) {
            OrderPayinfoDto orderPayinfoDto = new OrderPayinfoDto();
            orderPayinfoDto.setPayTime(DateUtils.getDate(orderPaymentList.get(0).getPayTime(), "yyyy-MM-dd HH:mm:ss"));
            orderPayinfoDto.setPayType(orderPaymentList.get(0).getPayType());
            orderPayinfoDto.setPayAmount(PriceConversion.intToString(orderPaymentList.get(0).getPayAmount()));
            orderDetaislDto.setOrderPayinfoDto(orderPayinfoDto);
        }
        log.info("当前订单号{}完成主表信息封装", orderInfo.getOrderNo());

    }

    private static String getOrderGroupNo(String passingData) {
        PassingDataDto dto = PassingDataUtil.disPassingData(passingData);
        if (null == dto)
            return null;
        if (StringUtils.isNotEmpty(dto.getGroupNo()))
            return dto.getGroupNo();
        if (StringUtils.isNotEmpty(dto.getBindGroupNo()))
            return dto.getBindGroupNo();
        return null;
    }

    private String getKingRemarks(String orderNo, boolean memberOrder, Integer orderAmount,
                                  Integer orderStatus, boolean hasAfterSale, Long userId) {
        if (orderStatus == OrderStatus.PENDING_PAYMENT.getStatus() || orderStatus == OrderStatus.CANCELLED.getStatus()) {
            return null;
        }
        Integer afterSaleStatus = null;
        if (hasAfterSale) {
            OrderAfterVo afterVo = new OrderAfterVo();
            afterVo.setOrderNo(orderNo);
            afterVo.setUserId(userId.toString());
            ReturnData<List<OrderAfterSaleDto>> data = afterSaleFeignClient.getAfterSale(afterVo);
            if (data != null && null != data.getData() && null != data.getData().get(0)
                    && null != data.getData().get(0)) {
                afterSaleStatus = data.getData().get(0).getAfterStatus();
            }
        }

        String kingRemarks = null;
        try {
            boolean isBuyGive = userFeignClient.isGiveBuy(orderNo, userId);
            if (isBuyGive) { //参与买送
                int giveNum = orderAmount * 10;
                if (memberOrder) {
                    giveNum += orderAmount / 10;
                }
                String msg = null != afterSaleStatus && afterSaleStatus.equals(
                        AfterSalesStatus.RETURN_MONEY_FINISH.getStatus()) ? "由于退款已收回" :
                        (orderStatus == OrderStatus.COMPLETED.getStatus() ? "已到账" : "确认收货后到账");
                kingRemarks = String.format("本订单买送活动获得%d买买金(%s)", giveNum, msg);
            } else if (!memberOrder || orderAmount < 100) {
                return null;
            } else {
                Map<String, Object> result = okService.getKingByOrder(orderNo, orderStatus, afterSaleStatus);
                if (Objects.nonNull(result)) {
                    int status = (Integer) result.get("status");
                    int kingNum = (Integer) result.get("kingNum");
                    String msg = (String) result.get("msg");
                    if (status >= 0) {
                        kingRemarks = String.format("本订单获得%d买买金(%s)", kingNum, msg);
                    } else {
                        kingRemarks = String.format("本订单获得%d买买金(确认收货后到账)", orderAmount / 10);
                    }
                } else {
                    kingRemarks = String.format("本订单获得%d买买金(确认收货后到账)", orderAmount / 10);
                }
            }
        } catch (Exception e) {
            log.info("调用买送接口异常:" + e);
        }
        return kingRemarks;
    }

    //  订单包裹表
    public void orderPackageDetail(OrderInfo orderInfo, OrderDetaislDto orderDetaislDto, String orderNo, String userId) {
        log.info("当前订单号{}进入包裹信息封装", orderNo);
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        List<OrderPackage> orderPakcages = orderPackageService.selectByMap(map);
        log.info("当前订单号{}，的包裹数量为:{}", orderNo, orderPakcages.size());
        List<OrderPackageDto> packages = new ArrayList<>();
        if (orderPakcages != null && orderPakcages.size() > 0) {
            for (int i = 0; i < orderPakcages.size(); i++) {
                OrderPackageDto orderPackageDto = new OrderPackageDto();
                String packageNo = orderPakcages.get(i).getPackageNo();
                orderPackageDto.setPackageNo(packageNo);
                // 封装快递信息
                OrderPackageLogDto orderPackageLogDto = new OrderPackageLogDto();
                log.info("当前订单号{}，的包裹号为{}", orderNo, packageNo);
                OrderPackageLog orderPackageLog = orderPackageLogService.selectByPackageNo(packageNo);
                if (orderPackageLog != null) {
                    orderPackageLogDto.setLogisticsNo(orderPackageLog.getLogisticsNo());
                    orderPackageLogDto.setLogisticsName(orderPackageLog.getLogisticsName());
                }
                OrderLogistics logistics = orderLogisticsService.selectOneByPackageNo(orderNo, packageNo);
                log.info(" 包裹号为{}的快递信息{}", packageNo, logistics);
                if (logistics != null) {
                    orderPackageLogDto.setLogisticsCode(logistics.getCompanyCode());
                    orderPackageLogDto.setPackageNo(packageNo);
                    orderPackageLogDto.setOrderNo(orderNo);
                    if (logistics.getSendTime() != null) {
                        orderPackageLogDto.setSendTime(DateUtils.getDate(logistics.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                    }
                    if (logistics.getCheckTime() != null) {
                        orderPackageLogDto.setCheckTime(DateUtils.getDate(logistics.getCheckTime(), "yyyy-MM-dd HH:mm:ss"));
                    }

                }
                // 封装到包裹里
                orderPackageDto.setLogistics(orderPackageLogDto);
                // 封装商品信息
                List<OrderGoodsDto> goods = new ArrayList<>();
                log.info(" 包裹号为{}的商品信息{}", packageNo, orderPakcages);

                List<OrderGood> orderGoodList = orderGoodService.selectByPackageNo(orderPakcages.get(i).getOrderNo(), orderPakcages.get(i).getPackageNo());
                if (orderGoodList != null && orderGoodList.size() > 0) {
                    for (OrderGood anOrderGoodList : orderGoodList) {
                        OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
                        orderGoodsDto.setGoodTitle(anOrderGoodList.getGoodName());
                        orderGoodsDto.setGoodNum(anOrderGoodList.getGoodNum());
                        orderGoodsDto.setGoodId(anOrderGoodList.getGoodId());
                        orderGoodsDto.setGoodSku(anOrderGoodList.getGoodSku());
                        orderGoodsDto.setSaleId(anOrderGoodList.getSaleId());
                        orderGoodsDto.setModelName(anOrderGoodList.getModelName());
                        orderGoodsDto.setGoodImage(anOrderGoodList.getGoodImage());
                        orderGoodsDto.setVirtualGoodFlag(anOrderGoodList.getVirtualFlag());
                        orderGoodsDto.setGoodAmount(PriceConversion.intToString(anOrderGoodList.getGoodAmount()));
                        orderGoodsDto.setDiscountAmount(PriceConversion.intToString(anOrderGoodList.getDiscountAmount()));
                        orderGoodsDto.setCouponAmount(PriceConversion.intToString(anOrderGoodList.getCouponAmount()));
                        orderGoodsDto.setOriginalPrice(PriceConversion.intToString(anOrderGoodList.getGoodAmount()));
                        orderGoodsDto.setPriceType(anOrderGoodList.getPriceType());
                        orderGoodsDto.setUnitPrice(PriceConversion.intToString(anOrderGoodList.getGoodPrice()));
                        orderGoodsDto.setMemberPrice(PriceConversion.intToString(anOrderGoodList.getMemberPrice()));
                        if (anOrderGoodList.getCouponAmount() != null) {
                            orderGoodsDto.setCouponPrice(PriceConversion.intToString(anOrderGoodList.getCouponAmount() / anOrderGoodList.getGoodNum()));
                        }
                        if (anOrderGoodList.getGoldPrice() != null) {
                            orderGoodsDto.setGoodTotalPrice(PriceConversion.intToString(anOrderGoodList.getGoodPrice() * anOrderGoodList.getGoodNum()));
                        } else {
                            orderGoodsDto.setGoodTotalPrice(PriceConversion.intToString(anOrderGoodList.getGoodAmount() * anOrderGoodList.getGoodNum()));
                        }

                        if (anOrderGoodList.getCreaterTime() != null) {
                            orderGoodsDto.setCreateTime(anOrderGoodList.getCreaterTime());
                        }
                        if (hasRecommend(orderInfo)) {
                            // 商品推荐
                            recommend(anOrderGoodList, orderGoodsDto);
                        }
                        // 虚拟商品
                        virtualGoodsFlag(anOrderGoodList, orderGoodsDto);
                        goods.add(orderGoodsDto);
                    }
                }
                // 商品封装到包裹里
                orderPackageDto.setGood(goods);
                packages.add(orderPackageDto);
            }
            //  包裹封装到订单详情
            orderDetaislDto.setPackages(packages);
            log.info("当前订单号{}包裹封装完毕！", orderNo);
        }
    }


    /**
     * 商品推荐
     *
     * @param orderGood
     * @param orderGoodsDto
     * @return
     */
    public OrderGoodsDto recommend(OrderGood orderGood, OrderGoodsDto orderGoodsDto) {
        HashMap<String, Object> recommendmap = new HashMap<>();
        List<String> recommendList = new ArrayList<>();
        recommendList.add(orderGood.getSaleId().toString());
        recommendmap.put("orderNo", orderGood.getOrderNo());
        recommendmap.put("createrId", orderGood.getCreaterId());
        recommendmap.put("goodSku", recommendList);
        try {
            ReturnData<List<UserRecommendOrder>> returnData = userFeignClient.selectByGoodSku(recommendmap);
            if (returnData != null && returnData.getData().size() > 0) {
                List<UserRecommendOrder> list = returnData.getData();
                UserRecommendOrder userRecommendOrder = list.get(0);
                orderGoodsDto.setHasRecommend(userRecommendOrder.getStatus());
                orderGoodsDto.setRecommendId(userRecommendOrder.getRecommendId());
            }
        } catch (Exception e) {
            log.info("订单详情--调用商品推荐接口异常:" + e);
        }
        return orderGoodsDto;
    }


    /**
     * 虚拟商品
     */
    public OrderGoodsDto virtualGoodsFlag(OrderGood orderGood, OrderGoodsDto orderGoodsDto) {

        if (orderGood.getVirtualType() != null && orderGood.getVirtualType() == 1) {
            String snapshotData = orderGood.getSnapshotData();
            Map<String, Object> stringObjectMap = JSON.parseObject(snapshotData, Map.class);
            Object goodId = stringObjectMap.get("goodsbaseid");
            Object limitNum = stringObjectMap.get("number");
            Object type = stringObjectMap.get("type");
            if (goodId != null) {
                Map<String, Object> snapshotDataMap = new HashMap<>();
                snapshotDataMap.put("goodId", goodId);
                snapshotDataMap.put("virtualGoodsFlag", 1);
                snapshotDataMap.put("limitNum", limitNum);
                snapshotDataMap.put("type", type);
                if (type != null && type.equals(1)) {
                    Object couponTemplateids = stringObjectMap.get("couponTemplateids");
                    snapshotDataMap.put("couponTemplateids", couponTemplateids);
                }
                String str = JSONObject.toJSONString(snapshotDataMap);
                orderGoodsDto.setPassingData(str);
            } else {
                orderGoodsDto.setPassingData(orderGood.getSnapshotData());
            }
        }
        return orderGoodsDto;

    }

    @Override
    public ReturnData<Page<OrderSearchResultDto>> getOrderListFromES(BossListVo bossListVo) {

        OrderSearchConditionDto condition = new OrderSearchConditionDto();
        condition.setCreateTimeStart(StringUtils.isNotBlank(bossListVo.getBeginOrderDate()) ? DateUtils.parse(bossListVo.getBeginOrderDate() + " 00:00:00").getTime() : null);
        condition.setCreateTimeEnd(StringUtils.isNotBlank(bossListVo.getEndOrderDate()) ? DateUtils.parse(bossListVo.getEndOrderDate() + " 23:59:59").getTime() : null);
        condition.setCurrentPage(bossListVo.getCurrentPage());
        condition.setPageSize(bossListVo.getPageSize());
        condition.setName(bossListVo.getConsigneeName());
        condition.setTelNumber(bossListVo.getConsigneeTel());
        condition.setOrderNo(bossListVo.getOrderNo());
        condition.setOrderStatus(bossListVo.getOrderStatus());
        condition.setOrderType(bossListVo.getOrderType());
        condition.setVirtualGood(bossListVo.getVirtualGood());
        condition.setChannel(bossListVo.getChannel());
        condition.setSource(bossListVo.getSource());
        log.info("=> 订单列表检索条件：{}", bossListVo);
        ReturnData<Page<OrderSearchResultDto>> result = eSearchFeignClient.getOrderList(condition);
        if (null == result)
            return null;
        if (null == result.getData())
            return null;

        for (OrderSearchResultDto dto : result.getData().getRecords()) {
            dto.setOrderStatusDesc(OrderStatus.toStatusMessage(dto.getOrderStatus()));
        }
        return result;
    }


    /**
     * 订单详情
     *
     * @param bossDetailVo
     * @return
     */
    @Override
    public BossDetailDto getBossDetail(BossDetailVo bossDetailVo) {
        OrderInfo orderInfo = getByOrderNo(bossDetailVo.getOrderNo());
        if (orderInfo == null) {
            return null;
        }
        shardingKey(bossDetailVo.getOrderNo());
        BossDetailDto bossDetailDto = new BossDetailDto();
        bossOrderInfo(bossDetailDto, orderInfo);
        bossLogisticsDetail(bossDetailDto, bossDetailVo);
        bossPackageDetail(bossDetailDto, bossDetailVo);
        return bossDetailDto;
    }


    /**
     * 封装boss订单详情-- 订单基本信息
     *
     * @param bossDetailDto
     * @param orderInfo
     */
    private void bossOrderInfo(BossDetailDto bossDetailDto, OrderInfo orderInfo) {
        log.info("boss--后台--详情--基本信息--当前订单号{}进入基本信息封装...", orderInfo.getOrderNo());
        bossDetailDto.setOrderNo(orderInfo.getOrderNo());
        bossDetailDto.setOrderStauts(orderInfo.getOrderStatus());
        bossDetailDto.setOrderStatusDesc(OrderStatus.toStatusMessage(orderInfo.getOrderStatus()));
        bossDetailDto.setOrderType(orderInfo.getOrderType());
        bossDetailDto.setOrderTypeDesc(OrderTypeStatus.OrderTypeStatus(orderInfo.getOrderType()));
        bossDetailDto.setMemberOrder(orderInfo.getMemberOrder());
        try {
            OrderAfterVo orderAfterVo = new OrderAfterVo();
            orderAfterVo.setOrderNo(orderInfo.getOrderNo());
            orderAfterVo.setUserId(String.valueOf(orderInfo.getCreaterId()));

            ReturnData<List<OrderAfterSaleDto>> returnData = afterSaleFeignClient.getAfterSale(orderAfterVo);
            if (returnData != null) {
                List<OrderAfterSaleDto> afterSalesList = returnData.getData();
                if (afterSalesList != null && afterSalesList.size() > 0) {
                    if (afterSalesList != null && afterSalesList.size() > 0) {
                        bossDetailDto.setAfterSaleStatus(afterSalesList.get(0).getAfterStatus());
                        bossDetailDto.setAfterSaleStatusDesc(AfterSalesStatus.toStatusMessage(afterSalesList.get(0).getAfterStatus()));
                        if (afterSalesList.get(0).getJstCancel() != null && afterSalesList.get(0).getJstCancel() == 1) {
                            bossDetailDto.setJstStatus(true);
                        } else {
                            bossDetailDto.setJstStatus(false);
                        }
                        ShippingDto shipping = new ShippingDto();
                        shipping.setLogisticsCode(afterSalesList.get(0).getLogisticsCode());
                        shipping.setLogisticsNo(afterSalesList.get(0).getLogisticsNo());
                        shipping.setLogisticsName(afterSalesList.get(0).getLogisticsName());
                        bossDetailDto.setShipping(shipping);

                    }
                }
            }
        } catch (Exception e) {
            log.info("获取订单售后信息失败:" + e);
        }

        bossDetailDto.setOrderAmount(PriceConversion.intToString(orderInfo.getOrderAmount()));
        bossDetailDto.setGoodAmount(PriceConversion.intToString(orderInfo.getGoodAmount()));
        bossDetailDto.setCouponAmount(PriceConversion.intToString(orderInfo.getCouponAmount()));
        bossDetailDto.setDiscountAmount(PriceConversion.intToString(orderInfo.getDiscountAmount()));
        bossDetailDto.setFreight(PriceConversion.intToString(orderInfo.getExpressAmount()));
        //  todo  返现待实现
        //免费送返现金额
        if ("11".equals(orderInfo.getOrderType())) {
            log.info("售后详情计算免费送退款,订单号:{},订单金额:{}", orderInfo.getOrderNo(),
                    PriceConversion.intToString(orderInfo.getOrderAmount()));
            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, "MFS");
            OrderGroup group = orderGroupService.getByOrderNo(orderInfo.getOrderNo());
            if (group != null && group.getGroupStatus() == 1) {
                bossDetailDto.setResultAmount(PriceConversion.intToString(orderInfo.getOrderAmount()));
            }
        }

        if (null == bossDetailDto.getResultAmount())
            bossDetailDto.setResultAmount(PriceConversion.intToString(0));
        bossDetailDto.setCreateDate(DateUtils.getDate(orderInfo.getCreaterTime(), "yyyy-MM-dd HH:mm:ss"));
        if (Objects.nonNull(orderInfo.getExpirtTime())) {
            bossDetailDto.setExpireDate(DateUtils.getDate(orderInfo.getExpirtTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        bossDetailDto.setCreaterId(String.valueOf(orderInfo.getCreaterId()));


        // 成团信息
        String str = orderInfo.getPassingData();
        if (StringUtil.isNotBlank(str)) {
            Map<String, Object> map = JSON.parseObject(str, Map.class);
            Object object = map.get("groupNo");
            String groupNo;
            if (Objects.isNull(object)) {
                groupNo = (String) map.get("bindGroupNo");
            } else {
                groupNo = (String) object;
            }
            try {
                GroupInfoDto groupInfoDto = orderGroupService.getGroupInfo(orderInfo.getCreaterId(), groupNo
                        , orderInfo.getOrderNo());
                if (groupInfoDto.getGroup().getGroupStatus() == OrderGroupStatus.COMPLETED.getStatus()) {
                    GroupDto group = new GroupDto();
                    group.setGroupPeople(groupInfoDto.getGroup().getGroupPeople());
                    group.setGroupDate(DateUtils.getDate(groupInfoDto.getGroup().getUpdateDate(), "yyyy-MM-dd HH:mm:ss"));
                    bossDetailDto.setGroupInfo(group);
                }

            } catch (Exception e) {
                log.info("获取拼团信息异常!" + e);
            }
        }
        // 支付信息封装
        List<OrderPayment> list = orderPaymentService.getOrderPayment(orderInfo.getOrderNo(), orderInfo.getCreaterId());
        if (list != null && list.size() > 0) {
            OrderPayinfoDto orderPayinfo = new OrderPayinfoDto();
            orderPayinfo.setPayAmount(PriceConversion.intToString(list.get(0).getPayAmount()));
            orderPayinfo.setPayTime(DateUtils.getDate(list.get(0).getPayTime(), "yyyy-MM-dd HH:mm:ss"));
            orderPayinfo.setPayType(list.get(0).getPayType());
            bossDetailDto.setOrderPayinfo(orderPayinfo);
        }

        log.info("boss--后台--详情--基本信息--当前订单号{}进基本信息封装完毕!", orderInfo.getOrderNo());


    }


    /**
     * boss订单详情--收件人信息封装
     *
     * @param bossDetailDto
     * @param bossDetailVo
     */
    private void bossLogisticsDetail(BossDetailDto bossDetailDto, BossDetailVo bossDetailVo) {
        log.info("boss--后台--详情--收件信息--当前订单号{},userId:{},进入收件人信息封装", bossDetailVo.getOrderNo(), bossDetailVo.getUserId());
        List<OrderLogistics> list = orderLogisticsService.getOrderLogistics(bossDetailVo.getOrderNo(), bossDetailVo.getUserId());
        if (null == list || list.size() == 0)
            return;
        OrderLogistics logistics = list.get(0);
        log.info("boss--后台--详情--收件信息--当前订单号{},快递信息{}", bossDetailVo.getOrderNo(), logistics);
        if (logistics == null)
            return;
        OrderLogisticsDto orderLogisticsDto = new OrderLogisticsDto();
        orderLogisticsDto.setConsumerName(logistics.getConsumerName());
        orderLogisticsDto.setConsumerMobile(logistics.getConsumerMobile());
        orderLogisticsDto.setConsumerAddr(logistics.getConsumerAddr());
        orderLogisticsDto.setProvince(logistics.getProvince());
        orderLogisticsDto.setCity(logistics.getCity());
        orderLogisticsDto.setArea(logistics.getArea());
        if (logistics.getSendTime() != null) {
            orderLogisticsDto.setSendTime(DateUtils.getDate(logistics.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        if (logistics.getCheckTime() != null) {
            orderLogisticsDto.setCheckTime(DateUtils.getDate(logistics.getCheckTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        bossDetailDto.setOrderLogistics(orderLogisticsDto);
        log.info("boss--后台--详情--收件信息--当前订单号{}快递信息封装完毕!", bossDetailVo.getOrderNo());
    }

    /***
     *   boss 订单详情--包裹信息封装
     * @param bossDetailDto
     * @param bossDetailVo
     */
    private void bossPackageDetail(BossDetailDto bossDetailDto, BossDetailVo bossDetailVo) {
        log.info("boss-后台-- 详情--包裹,当前订单号{}进入包裹信息封装...", bossDetailVo.getOrderNo());

        Map<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", bossDetailVo.getOrderNo());
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, bossDetailVo.getUserId());
        List<OrderPackage> orderPackages = orderPackageService.selectByMap(map);

        log.info("boss-后台-- 详情--包裹,当前订单号{}进入包裹数量为:{}", bossDetailVo.getOrderNo(), orderPackages.size());
        if (orderPackages.size() <= 0) {
            return;
        }

        List<OrderPackageDto> list = new ArrayList<>();
        for (OrderPackage orderPackage : orderPackages) {
            OrderPackageDto orderPackageDto = new OrderPackageDto();
            String packageNo = orderPackage.getPackageNo();
            orderPackageDto.setPackageNo(orderPackage.getPackageNo());

            OrderPackageLogDto orderPackageLogDto = new OrderPackageLogDto();
            OrderPackageLog orderPackageLog = orderPackageLogService.selectByPackageNo(orderPackage.getPackageNo());
            if (orderPackageLog != null) {
                orderPackageLogDto.setLogisticsName(orderPackageLog.getLogisticsName());
                orderPackageLogDto.setLogisticsNo(orderPackageLog.getLogisticsNo());
            }
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, bossDetailVo.getUserId());
            List<OrderLogistics> orderLogisticsList = orderLogisticsService.selectByMap(map);
            if (orderLogisticsList != null && orderLogisticsList.size() > 0) {
                OrderLogistics logistics = orderLogisticsList.get(0);
                log.info("boss-后台-- 详情--包裹,当前包裹号为{},快递信息为{}", packageNo, logistics);
                orderPackageLogDto.setOrderNo(logistics.getOrderNo());
                orderPackageLogDto.setPackageNo(orderPackage.getPackageNo());
                if (logistics.getSendTime() != null) {
                    orderPackageLogDto.setSendTime(DateUtils.getDate(logistics.getSendTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                if (logistics.getCheckTime() != null) {
                    orderPackageLogDto.setCheckTime(DateUtils.getDate(logistics.getCheckTime(), "yyyy-MM-dd HH:mm:ss"));
                }
                // 快递信息封装到包裹表里
                orderPackageDto.setLogistics(orderPackageLogDto);
            }

            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, bossDetailVo.getUserId());
            map.clear();
            map.put("PACKAGE_NO", packageNo);
            List<OrderGood> orderGoodList = orderGoodService.selectByMap(map);
            log.info("boss-后台-- 详情--包裹,当前包裹号为{},包裹商品信息为:{}", packageNo, orderGoodList);
            List<OrderGoodsDto> goods = new ArrayList<>();
            if (orderGoodList != null && orderGoodList.size() > 0) {
                for (OrderGood anOrderGoodList : orderGoodList) {
                    OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
                    orderGoodsDto.setGoodSku(anOrderGoodList.getGoodSku());
                    orderGoodsDto.setSaleId(anOrderGoodList.getSaleId());
                    orderGoodsDto.setGoodId(anOrderGoodList.getGoodId());
                    orderGoodsDto.setCreateTime(anOrderGoodList.getCreaterTime());
                    orderGoodsDto.setGoodTitle(anOrderGoodList.getGoodName());
                    orderGoodsDto.setGoodNum(anOrderGoodList.getGoodNum());
                    orderGoodsDto.setModelName(anOrderGoodList.getModelName());
                    orderGoodsDto.setPriceType(anOrderGoodList.getPriceType());
                    orderGoodsDto.setUnitPrice(PriceConversion.intToString(anOrderGoodList.getGoodPrice()));
                    orderGoodsDto.setOriginalPrice(PriceConversion.intToString(anOrderGoodList.getGoodAmount()));
                    orderGoodsDto.setGoodAmount(PriceConversion.intToString(anOrderGoodList.getGoodAmount()));
                    orderGoodsDto.setMemberPrice(PriceConversion.intToString(anOrderGoodList.getMemberPrice()));
                    orderGoodsDto.setCouponAmount(PriceConversion.intToString(anOrderGoodList.getCouponAmount()));
                    if (anOrderGoodList.getCouponAmount() != null && anOrderGoodList.getGoodNum() != null) {
                        orderGoodsDto.setCouponPrice(PriceConversion.intToString(anOrderGoodList.getCouponAmount() / anOrderGoodList.getGoodNum()));
                        orderGoodsDto.setGoodTotalPrice(PriceConversion.intToString(anOrderGoodList.getGoodPrice() * anOrderGoodList.getGoodNum()));
                    } else {
                        orderGoodsDto.setCouponPrice("0");
                        orderGoodsDto.setGoodTotalPrice("0");
                    }

                    orderGoodsDto.setDiscountAmount(PriceConversion.intToString(anOrderGoodList.getDiscountAmount()));
                    orderGoodsDto.setGoodImage(anOrderGoodList.getGoodImage());
                    orderGoodsDto.setVirtualGoodFlag(anOrderGoodList.getVirtualFlag());
                    goods.add(orderGoodsDto);
                }
            }
            // 商品封装到包裹
            orderPackageDto.setGood(goods);
            // 包裹封装到订单详情
            list.add(orderPackageDto);
            log.info("boss-后台-- 详情--包裹,当前订单号{}包裹信息封装完毕", bossDetailVo.getOrderNo());
        }
        bossDetailDto.setPackages(list);
    }

    /**
     * 通过订单号查询订单信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public OrderInfo selectByOrderNo(String orderNo, Long userId) {
        log.info("通过订单号查询订单信息--- 当前用户为:{},订单号为:{}", userId, orderNo);
        shardingKey(orderNo);
//        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        OrderInfo info = new OrderInfo();
        info.setOrderNo(orderNo);
//        info.setCreaterId(userId);
//        info.setDelFlag(DelFlagStatus.DEL_STATUS.getStatus());
        OrderInfo orderInfo = orderInfoMapper.selectOne(info);
        log.info("通过订单号查询订单信息--- 当前用户为:{},订单号为:{},查询结果为{}", userId, orderNo, orderInfo);
        return orderInfo;
    }


    /**
     * 取消订单
     *
     * @param cancelVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String cancel(CancelVo cancelVo) {
        RLock fairLock = redissonClient.getFairLock("ORDER:CANCEL:" + cancelVo.getOrderNo());
        fairLock.lock(3, TimeUnit.SECONDS);
        try {
            shardingKey(cancelVo.getOrderNo());
            log.info("当前用户{}已经取消订单方法内，订单号为:{}", cancelVo.getUserId(), cancelVo.getOrderNo());
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("ORDER_NO", cancelVo.getOrderNo());
            List<OrderInfo> list = orderInfoMapper.selectByMap(hashMap);
            Assert.isTrue(!(list.size() <= 0 || list == null), "当前用户的订单号不存在!");
            OrderInfo orderInfo = list.get(0);
            if (orderInfo.getOrderStatus() == OrderStatus.PAYMENTED.getStatus()) {
                throw new IllegalArgumentException("取消失败:请稍后再试");
            }
            if (orderInfo.getHasAfterSale()) {
                throw new IllegalArgumentException("取消失败:售后中");
            }
            if (orderInfo.getOrderStatus() == OrderStatus.PENDING_PAYMENT.getStatus()) {
                // 修改订单状态
                updateOrderStatus(OrderStatus.CANCELLED.getStatus(), cancelVo.getOrderNo());
                HashMap<String, Object> map = new HashMap<>();
                map.put("ORDER_NO", cancelVo.getOrderNo());
                List<OrderGood> goods = orderGoodService.selectByMap(map);

                if (Objects.nonNull(orderInfo.getCouponAmount()) && orderInfo.getCouponAmount() > 0) {
                    //解绑优惠券
                    useCoupon(orderInfo.getCreaterId(), orderInfo.getOrderNo(), null, false);
                }
            } else {
                // 修改订单售后标识
                UserOrderVo userOrderVo = new UserOrderVo();
                userOrderVo.setUserId(cancelVo.getUserId());
                userOrderVo.setOrderNo(cancelVo.getOrderNo());
                updateAfterSaleFlag(userOrderVo);
                // 售后
                AddAfterSaleVo addAfterSaleVo = new AddAfterSaleVo();
                addAfterSaleVo.setOrderNo(cancelVo.getOrderNo());
                addAfterSaleVo.setUserId(String.valueOf(cancelVo.getUserId()));
                addAfterSaleVo.setOrderType(orderInfo.getOrderType().toString());
                List<OrderLogistics> orderLogistics = orderLogisticsService.getOrderLogistics(userOrderVo.getOrderNo(), null);
                addAfterSaleVo.setConsumerName(orderLogistics != null ? orderLogistics.get(0).getConsumerName() : "");
                addAfterSaleVo.setConsumerMobile(orderLogistics != null ? orderLogistics.get(0).getConsumerMobile() : "");
                addAfterSaleVo.setCreaterTime(orderInfo.getCreaterTime());
                afterSaleFeignClient.addAfterSale(addAfterSaleVo);

//                Map<String, Object> addAfterMap = new HashMap<>();
//                addAfterMap.put("orderNo", cancelVo.getOrderNo());
//                addAfterMap.put("userId", cancelVo.getUserId());
//                addAfterMap.put("orderType", orderInfo.getOrderType());
//                addAfterMap.put("createrTime", orderInfo.getCreaterTime());
//                List<OrderLogistics> orderLogistics = orderLogisticsService.getOrderLogistics(userOrderVo.getOrderNo(), null);
//                addAfterMap.put("consumerName", orderLogistics != null ? orderLogistics.get(0).getConsumerName() : "");
//                addAfterMap.put("consumerMobile", orderLogistics != null ? orderLogistics.get(0).getConsumerMobile() : "");
//                mQProducer.addAfterSale(addAfterMap);
            }

            // 取消订单--推荐返现
            Map<String, Object> cancelMap = new HashMap<>();
            cancelMap.put("orderNo", cancelVo.getOrderNo());
            cancelMap.put("userId", cancelVo.getUserId());
            mQProducer.sendCancel(cancelMap);
            mQProducer.returnMMKing(Long.valueOf(cancelVo.getUserId()), cancelVo.getOrderNo(), orderInfo.getGoldNum());
            if (orderInfo.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
                mQProducer.sendCancelBysysj(cancelMap);
            }
            return "success";
        } finally {
            fairLock.unlock();
        }
    }

    /**
     * 删除订单(逻辑删除)
     *
     * @param removeOrderVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String removeOrder(RemoveOrderVo removeOrderVo) {
        shardingKey(removeOrderVo.getOrderNo());
        log.info("当前用户{}开始删除订单,订单号为:{}", removeOrderVo.getUserId(), removeOrderVo.getOrderNo());
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setDelFlag(DelFlagStatus.NOT_DEL_STATUS.getStatus());
        EntityWrapper<OrderInfo> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ORDER_NO", removeOrderVo.getOrderNo());
        entityWrapper.eq("CREATER_ID", removeOrderVo.getUserId());
        boolean result = update(orderInfo, entityWrapper);
        log.info("当前用户:{},删除订单,订单号为:{},执行状态:{}", removeOrderVo.getUserId(), removeOrderVo.getOrderNo(), result);
        Assert.isTrue(result, "删除订单失败");
        ReturnData returnData = afterSaleFeignClient.delAfterSaleNo(removeOrderVo.getOrderNo());
        Assert.isTrue(SecurityConstants.SUCCESS_CODE == returnData.getCode(), returnData.getDesc());
        return "success";
    }


    /**
     * 确认收货
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String receiveOrder(ReceiveOrderVo receiveOrderVo) {
        shardingKey(receiveOrderVo.getOrderNo());
        OrderInfo orderInfo = this.getByOrderNo(receiveOrderVo.getOrderNo());
        Assert.notNull(orderInfo, "订单不存在");
        Assert.isTrue(orderInfo.getOrderStatus() == OrderStatus.PENDING_RECEIPT.getStatus(), "订单状态错误，不能执行该操作");
        try {
            freeOrderHandler(orderInfo, receiveOrderVo.getRemark());
            orderKingHandler(orderInfo);
            List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo());
            if (null != orderGoodList && orderGoodList.size() > 0) {
                SMSInfoDto infoDto = new SMSInfoDto(orderInfo.getCreaterId(),
                        orderInfo.getOrderNo(), orderGoodList.get(0).getGoodName());
                smsProcessor.sendReceivedMS(infoDto);
            }
        } catch (Exception e) {
            log.info("免费送、短信或买买金接口异常:" + e);
        }
        Map<String, Object> map = new HashMap<>();
        if (StringUtil.isNotBlank(receiveOrderVo.getOrderNo())) {
            receiveByOrderInfo(Long.valueOf(receiveOrderVo.getUserId()), receiveOrderVo.getOrderNo());
            receiveByOrderLogistics(Long.valueOf(receiveOrderVo.getUserId()), receiveOrderVo.getOrderNo());
            // 确定收货--推荐返现用
            map.put("orderNo", receiveOrderVo.getOrderNo());
            map.put("userId", receiveOrderVo.getUserId());
            map.put("appId", SecurityUserUtil.getUserDetails().getAppId());
            mQProducer.sendReceiveOrder(map);
            // 十元三件专用
            if (orderInfo.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
                mQProducer.sendReceiveBysysj(map);
            }

            // todo  生产环境放开 更新购物清单
//            groceryLlistUtils.updateOrder(receiveOrderVo.getOrderNo(), 100);
            return "success";
        } else if (StringUtil.isNotBlank(receiveOrderVo.getPackageNo())) {
            int index = StringUtils.indexOf(receiveOrderVo.getPackageNo(), "-");
            String parentOrderNo = StringUtils.substring(receiveOrderVo.getPackageNo(), 0, index);
            receiveByOrderInfo(Long.valueOf(receiveOrderVo.getUserId()), parentOrderNo);
            receiveByOrderLogistics(Long.valueOf(receiveOrderVo.getUserId()), parentOrderNo);
            // 确定收货--推荐返现用
            map.put("orderNo", receiveOrderVo.getOrderNo());
            map.put("userId", receiveOrderVo.getUserId());
            map.put("appId", SecurityUserUtil.getUserDetails().getAppId());
            mQProducer.sendReceiveOrder(map);
            // 十元三件专用
            if (orderInfo.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
                mQProducer.sendReceiveBysysj(map);
            }
            // todo  生产环境放开 更新购物清单
//            groceryLlistUtils.updateOrder(receiveOrderVo.getOrderNo(), 100);
            return "success";
        } else {
            return "fail";
        }
    }

    @Override
    public boolean autoReceipt(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderInfo orderInfo = getByOrderNo(orderNo);
        if (Objects.nonNull(orderInfo) && orderInfo.getOrderStatus() == OrderStatus.PENDING_RECEIPT.getStatus()) {
            return updateOrderStatus(OrderStatus.COMPLETED.getStatus(), orderNo);
        }
        return true;
    }

    private void orderKingHandler(OrderInfo orderInfo) {

        if (orderInfo.getOrderAmount() / 100 < 1) {
            //1元起送
            log.info("订单不满1元，不送买买金");
            return;
        }

        if (orderInfo.getDelFlag() == 0)
            return;

        int kingNum = orderInfo.getOrderAmount() / 10;

        if (orderInfo.getMemberOrder()) {
            OrderKing ok = new OrderKing();
            ok.setNum(kingNum);
            ok.setCreateTime(new Date());
            ok.setOrderNo(orderInfo.getOrderNo());
            ok.setStatus(1);
            ok.setType(0);
            ok.setUserId(orderInfo.getCreaterId());
            boolean cnt = okService.insert(ok);
            log.info("订单赠送普通买买金结果:{}", cnt);

            Map<String, Object> orderParams = Maps.newHashMapWithExpectedSize(4);
            orderParams.put("orderNo", orderInfo.getOrderNo());
            orderParams.put("isGiveBy", "0");//0:表示非买送
            orderParams.put("kingNum", kingNum);
            orderParams.put("userId", orderInfo.getCreaterId());
            int sumKing = userFeignClient.orderKingProc(orderParams);
            log.info("userId:{},订单获得买买金后，总买买金数量:{}", orderInfo.getCreaterId(), sumKing);
        }

        //判断是否享受买送活动
        boolean isGive = userFeignClient.isGiveBuy(orderInfo.getOrderNo(), orderInfo.getCreaterId());
        log.info("判断订单【{}】是否享受买送活动:{} ", orderInfo.getOrderNo(), isGive);
        if (isGive && (OrderType.TEN_YUAN_SHOP == orderInfo.getOrderType().intValue() || OrderType.RECHARGE == orderInfo.getOrderType().intValue())) {
            int giveByNum = orderInfo.getOrderAmount() * 10;  //比例1:1000

            OrderKing ok = new OrderKing();
            ok.setOrderNo(orderInfo.getOrderNo());
            ok.setUserId(orderInfo.getCreaterId());
            ok.setType(1);
            ok.setStatus(1);
            ok.setCreateTime(new Date());
            ok.setNum(giveByNum);
            okService.insert(ok);

            Map<String, Object> orderParams = Maps.newHashMapWithExpectedSize(4);
            orderParams.put("orderNo", orderInfo.getOrderNo());
            orderParams.put("isGiveBy", "1");//0:表示非买送
            orderParams.put("kingNum", giveByNum);
            orderParams.put("userId", orderInfo.getCreaterId());
            userFeignClient.orderKingProc(orderParams);
        }
    }

    private void freeOrderHandler(OrderInfo orderInfo, String remark) {

        //查找用户绑定关系
        UserShardVo shardVo = null;
        try {
            ReturnData<UserShardVo> data = userFeignClient.getFreeOrderRelation(orderInfo.getCreaterId());
            log.info("查询免费送关系,userId:{},结果:{}", orderInfo.getCreaterId(), data);
            if (data.getCode() == 1) {
                shardVo = data.getData();
            } else {
                log.info("确认收货时 查询免费送关系报错,{}", data.getDesc());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (null == shardVo || null == shardVo.getOrderNo()
                || null == shardVo.getShardFrom())
            return;

        //删除免费送绑定关系
        boolean bool = userFeignClient.delFreeOrderRelation(orderInfo.getCreaterId());
        log.info("确定收货时删除免费送关系结果:{},orderNo:{}", bool, orderInfo.getOrderNo());

        if (1 == shardVo.getUserFlag()) {
            log.info("确定收货时免费送老用户不算组人数,订单号:{}", orderInfo.getOrderNo());
            return;
        }

        //判断发起人订单状态,如果取消则不给红包
        OrderInfo lanOrder = this.getByOrderNo(shardVo.getOrderNo());
        if (null == lanOrder)
            return;

        if (lanOrder.getOrderStatus() != OrderStatus.COMPLETED.getStatus() &&
                lanOrder.getOrderStatus() != OrderStatus.TO_BE_DELIVERED.getStatus() &&
                lanOrder.getOrderStatus() != OrderStatus.PENDING_RECEIPT.getStatus())
            return;

        OrderGroup orderGroup = this.getMFSOrderGroup(lanOrder.getOrderNo(), lanOrder.getCreaterId());
        log.info("免费送查询团结果:{}", orderGroup);
        if (null == orderGroup) {
            log.info("发起人订单号查询不到,{}", lanOrder.getOrderNo());
            //团主团信息不存在
            return;
        }

        BaseUser lanUser = userFeignClient.getByById(lanOrder.getCreaterId());
        if (null == lanUser)
            return;

        if (orderGroup.getCurrentPeople() >= orderGroup.getGroupPeople() ||
                OrderGroupStatus.COMPLETED.getStatus() == orderGroup.getGroupStatus()) {
            log.info("已成团");
            return;
        }

        OrderGroupJoin join = new OrderGroupJoin();
        join.setActiveType(OrderGroupType.FREE_ORDER.getType());
        join.setLaunchUserId(lanOrder.getCreaterId());
        join.setLaunchOrderNo(lanOrder.getOrderNo());
        join.setJoinOrderNo(orderInfo.getOrderNo());
        join.setGroupNo(orderGroup.getGroupNo());
        join.setBusinessId(1);//免费送活动默认1
        join.setGroupMain(0);
        join.setJoinUserId(orderInfo.getCreaterId());
        join.setRemark(remark);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderGroup.getGroupNo());
        orderGroupJoinService.insert(join);

        OrderGroup group = new OrderGroup();
        group.setGroupId(orderGroup.getGroupId());
        group.setCurrentPeople(orderGroup.getCurrentPeople() + 1);

        if (group.getCurrentPeople() < orderGroup.getGroupPeople()) {
            // TODO: 2019/6/29 未成团，发送模板消息,新用户加入提醒
            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, "MFS");
            orderGroupService.updateById(group);
            return;
        } else {
            group.setGroupStatus(OrderGroupStatus.COMPLETED.getStatus());

            BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, "MFS");
            orderGroupService.updateById(group);
        }


        //成团，发红包
        RedPackageUserVo packageUserVo = new RedPackageUserVo();
        packageUserVo.setPackageAmount(lanOrder.getOrderAmount());
        packageUserVo.setPackageCode(RedPackCodeUtils.genRedPackCode());
        packageUserVo.setActiveType(RedPackageType.FREE_ORDER.getType());
        packageUserVo.setOrderNo(lanOrder.getOrderNo());
        packageUserVo.setUserId(lanUser.getUserId());
        packageUserVo.setCreaterId(lanOrder.getCreaterId());
        packageUserVo.setPackageSource("FREE_ORDER");
        packageUserVo.setUnionId(lanUser.getUnionId());
        packageUserVo.setPackageStatus(0);
        boolean res = userFeignClient.addRedPackage(packageUserVo);
        log.info("成团发红包结果:{}", res);

        // TODO: 2019/6/29 发送免费送成团提醒
    }

    /**
     * 获取免费送团信息
     *
     * @param lanOrderNo
     * @param lanUserId
     * @return
     */
    private OrderGroup getMFSOrderGroup(String lanOrderNo, Long lanUserId) {
        OrderGroup orderGroup = new OrderGroup();
        orderGroup.setGroupType(OrderGroupType.FREE_ORDER.getType());
        orderGroup.setLaunchUserId(lanUserId);
        orderGroup.setLaunchOrderNo(lanOrderNo);
        EntityWrapper<OrderGroup> wrapper = new EntityWrapper<>(orderGroup);
        BaseContextHandler.set(SecurityConstants.SHARDING_GROUP_KEY, "MFS");
        return orderGroupService.selectOne(wrapper);
    }


    /**
     * 确认收货--修改OrderInfo 方法
     *
     * @param userId
     * @param orderNo
     */
    @Transactional(rollbackFor = Exception.class)
    public void receiveByOrderInfo(Long userId, String orderNo) {
        log.info("当前用户{}进入确认收货方法中--OrderInfo表中修改,订单号为:{}", userId, orderNo);
        shardingKey(orderNo);
        OrderInfo orderInfo = selectByOrderNo(orderNo, userId);
        if (orderInfo == null || orderInfo.getOrderStatus().equals(OrderStatus.COMPLETED.getStatus())) {
            log.info("订单异常,{}", orderInfo);
            return;
        }
        List<String> orderNoList = Lists.newArrayListWithCapacity(1);
        orderNoList.add(orderInfo.getOrderNo());
        this.batchUpdateOrderStatus(orderNoList, OrderStatus.COMPLETED.getStatus());
        orderPackageService.batchUpdateOrderPackageStatusByOrderNo(orderNoList, OrderStatus.COMPLETED.getStatus());
        log.info("当前用户{}已完成确认收货方法中--OrderInfo表中修改,订单号为:{}", userId, orderNo);
    }


    /**
     * 确认收货--修改orderLogistics 表
     */
    @Transactional(rollbackFor = Exception.class)
    public void receiveByOrderLogistics(Long userId, String orderNo) {
        shardingKey(orderNo);
        log.info("当前用户{}已经进入确认收货--修改orderLogistics方法中订单号为:{}", userId, orderNo);
        Date date = new Date();
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        List<OrderLogistics> orderLogisticsList = orderLogisticsService.selectByMap(map);
        if (orderLogisticsList == null || orderLogisticsList.size() <= 0) {
            return;
        }
        orderLogisticsList.forEach(r -> {
            OrderLogistics orderLogistics = new OrderLogistics();
            orderLogistics.setEndTime(date);
            orderLogistics.setCheckTime(date);
            EntityWrapper<OrderLogistics> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("ORDER_NO", orderNo);
            orderLogisticsService.update(orderLogistics, entityWrapper);
        });
        log.info("当前用户{}已经进入确认收货--修改orderLogistics方法中订单号为:{}", userId, orderNo);
    }


    /**
     * 获取支付信息
     *
     * @param orderNo
     * @param userId
     */
    @Override
    public OrderPayment getOrderPayment(String orderNo, Long userId) {
        shardingKey(orderNo);
        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setOrderNo(orderNo);
        OrderPayment orderPay = orderPaymentMapper.selectOne(orderPayment);
        return orderPay;
    }

    /**
     * 获取当前用户订单统计
     *
     * @param userId
     * @return
     */
    @Override
    public OrderStatsDto getUserOrderStats(Long userId) {
        OrderStatsDto orderStatsDto = new OrderStatsDto();
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(userId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<OrderInfo> wrapper = new EntityWrapper<>();
            wrapper.eq("DEL_FLAG", DelFlagStatus.DEL_STATUS.getStatus());
            wrapper.eq("ORDER_STATUS", OrderStatus.PENDING_PAYMENT.getStatus());
            wrapper.in("CREATER_ID", v);
            orderStatsDto.setWaitPayNum(orderStatsDto.getWaitPayNum() + orderInfoMapper.selectCount(wrapper));   //  待支付


            wrapper = new EntityWrapper<>();
            wrapper.eq("DEL_FLAG", DelFlagStatus.DEL_STATUS.getStatus());
            wrapper.in("ORDER_STATUS", Arrays.asList(OrderStatus.TO_BE_A_GROUP.getStatus()));
            wrapper.in("CREATER_ID", v);
            wrapper.eq("HAS_AFTER_SALE", false);
            orderStatsDto.setWaitGroupNum(orderStatsDto.getWaitGroupNum() + orderInfoMapper.selectCount(wrapper));   // 待成团


            wrapper = new EntityWrapper<>();
            wrapper.eq("DEL_FLAG", DelFlagStatus.DEL_STATUS.getStatus());
            wrapper.eq("ORDER_STATUS", OrderStatus.TO_BE_DELIVERED.getStatus());
            wrapper.in("CREATER_ID", v);
            wrapper.eq("HAS_AFTER_SALE", false);
            orderStatsDto.setWaitShipNum(orderStatsDto.getWaitShipNum() + orderInfoMapper.selectCount(wrapper));             //待发货

            wrapper = new EntityWrapper<>();
            wrapper.eq("DEL_FLAG", DelFlagStatus.DEL_STATUS.getStatus());
            wrapper.eq("ORDER_STATUS", OrderStatus.PENDING_RECEIPT.getStatus());
            wrapper.in("CREATER_ID", v);
            wrapper.eq("HAS_AFTER_SALE", false);
            orderStatsDto.setWaitReceipt(orderStatsDto.getWaitReceipt() + orderInfoMapper.selectCount(wrapper)); // 待收货--配送中
        });

        // 售后数量
//        Integer count = afterSaleFeignClient.getAfterSaleCount();

        try {
            ReturnData<Integer> returnData = afterSaleFeignClient.getAfterSaleCount();
            if (returnData != null) {
                Integer count = returnData.getData();
                orderStatsDto.setAfterSaleNum(count);

            }
        } catch (Exception e) {
            orderStatsDto.setAfterSaleNum(0);
            log.info("获取售后数量异常:" + e, e.getMessage());
        }
        return orderStatsDto;
    }

    /**
     * 虚拟商品发货
     *
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String sendVirtualGood(OrderGoodVo orderGoodVo) {
        shardingKey(orderGoodVo.getOrderNo());
        String orderNo = orderGoodVo.getOrderNo();
        OrderInfo orderInfo = getByOrderNo(orderNo);
        Assert.notNull(orderInfo, "未查询到该订单");
        Assert.notNull(orderInfo.getOrderStatus() == OrderStatus.COMPLETED.getStatus(), "订单已完成");
        List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo());
        List<OrderPackage> packages = orderPackageService.selectByOrderNo(orderInfo.getOrderNo());

        packages.forEach(orderPackage -> {
            if (Objects.nonNull(orderPackage.getVirtualGood()) && 1 == orderPackage.getVirtualGood()) {
                List<OrderGood> orderGoods = orderGoodList.stream().filter(orderGood -> orderGood.getPackageNo().equals(orderPackage.getPackageNo())).collect(Collectors.toList());
                orderPackageService.virtualGoodToBeDelivered(orderPackage, orderGoods, false);
            }
        });
        return "success";
    }

    @Override
    public void uploadErp(UploadErpVo uploadErpVo) {
        OrderPackage orderPackage = orderPackageService.selectByPackageNo(uploadErpVo.getPackageNo());
        Assert.notNull(orderPackage, "包裹号不存在");
        Assert.isTrue(orderPackage.getOrderStatus() == OrderStatus.TO_BE_DELIVERED.getStatus(), "当前包裹状态:" + OrderStatus.toStatusMessage(orderPackage.getOrderStatus()));
        List<OrderPackage> orderPackageList = Lists.newArrayListWithCapacity(1);
        orderPackageList.add(orderPackage);
        orderPackageService.toBeDelivered(orderPackageList);
    }

    /**
     * 扣减库存
     *
     * @param decrGoodNum
     * @return
     */
    @Override
    public Boolean decrGood(DecrGoodNum decrGoodNum) {
        log.info("进入扣库存方法中,sku{},数量{}", decrGoodNum.getSku(), decrGoodNum);
        return goodsStockService.decr(decrGoodNum.getOrderNo(), decrGoodNum.getSku(), decrGoodNum.getNum());
    }

    /**
     * 通过订单号查询订单商品
     *
     * @param orderGoodVo
     * @return
     */
    @Override
    public List<OrderGoodsDto> getOrderGoodList(OrderGoodVo orderGoodVo) {
        log.info("当前团号{}已经订单号查询商品信息方法中..", orderGoodVo.getGroupNo());
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isNotBlank(orderGoodVo.getGroupNo())) {
            OrderGroup group = orderGroupService.getByGroupNo(orderGoodVo.getGroupNo());
            if (null == group) {
                if (StringUtils.isBlank(orderGoodVo.getOrderNo())) {
                    return null;
                } else {
                    map.put("ORDER_NO", orderGoodVo.getOrderNo());
                }
            } else {
                map.put("ORDER_NO", group.getLaunchOrderNo());
            }
        } else {
            map.put("ORDER_NO", orderGoodVo.getOrderNo());
        }
        if (map.isEmpty())
            return null;
        String orderNo = (String) map.get("ORDER_NO");
        shardingKey(orderNo);
        List<OrderGood> orderGoodList = orderGoodService.selectByMap(map);
        log.info("当前用户{}已经订单号查询商品信息方法中..订单号为:{},查询的结果为：{}", orderGoodVo.getUserId(), orderGoodVo.getOrderNo(), orderGoodList);
        if (orderGoodList == null || orderGoodList.size() <= 0) {
            return null;
        }
        List<OrderGoodsDto> list = new ArrayList<>();
        orderGoodList.stream().forEach(r -> {
            OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
            orderGoodsDto.setGoodId(r.getGoodId());
            orderGoodsDto.setGoodTitle(r.getGoodName());
            orderGoodsDto.setGoodSku(r.getGoodSku());
            orderGoodsDto.setGoodNum(r.getGoodNum());
            orderGoodsDto.setSaleId(r.getSaleId());
            orderGoodsDto.setModelName(r.getModelName());
            orderGoodsDto.setOriginalPrice(PriceConversion.intToString(r.getGoodAmount()));
            orderGoodsDto.setPriceType(r.getPriceType());
            orderGoodsDto.setUnitPrice(PriceConversion.intToString(r.getGoodPrice()));
            orderGoodsDto.setMemberPrice(PriceConversion.intToString(r.getMemberPrice()));
            orderGoodsDto.setCreateTime(r.getCreaterTime());
            orderGoodsDto.setGoodImage(r.getGoodImage());
            orderGoodsDto.setCouponAmount(String.valueOf(r.getCouponAmount()));
            orderGoodsDto.setGoodAmount(String.valueOf(r.getGoodAmount()));
            orderGoodsDto.setDiscountAmount(String.valueOf(r.getDiscountAmount()));
            orderGoodsDto.setVirtualGoodFlag(r.getVirtualFlag());
            // 商品推荐
            recommend(r, orderGoodsDto);
            list.add(orderGoodsDto);
        });
        log.info("当前团号{}已经订单号查询商品信息方法中..订单号为:{},封装完毕.", orderGoodVo.getGroupNo(), orderGoodVo.getGroupNo());
        return list;
    }

    /**
     * 查询已完成的订单
     *
     * @param orderFinishGoodVo
     * @return
     */
    @Override
    public List<OrderGoodsDto> getOrderFinishList(OrderFinishGoodVo orderFinishGoodVo) {
        shardingKey(orderFinishGoodVo.getOrderNo());
        log.info("当前用户:{},开始查询已完成的订单", orderFinishGoodVo.getUserId());
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("ORDER_STATUS", OrderStatus.COMPLETED.getStatus());
//        entityWrapper.eq("CREATER_ID", orderFinishGoodVo.getUserId());
        entityWrapper.eq("ORDER_NO", orderFinishGoodVo.getOrderNo());
        List<OrderInfo> orderInfoList = orderInfoMapper.selectList(entityWrapper);
        log.info("当前用户:{},开始查询已完成的订单，结果为:{}", orderFinishGoodVo.getUserId(), orderInfoList);
        if (orderInfoList == null || orderInfoList.size() <= 0) {
            return null;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ORDER_NO", orderFinishGoodVo.getOrderNo());
        List<OrderGood> orderGoodList = orderGoodService.selectByMap(map);

        log.info("当前用户:{},开始查询已完成的订单，查询商品结果为:{}", orderFinishGoodVo.getUserId(), orderGoodList);
        List<OrderGoodsDto> list = new ArrayList<>();
        orderGoodList.stream().forEach(r -> {
            OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
            orderGoodsDto.setGoodId(r.getGoodId());
            orderGoodsDto.setGoodTitle(r.getGoodName());
            orderGoodsDto.setGoodSku(r.getGoodSku());
            orderGoodsDto.setSaleId(r.getSaleId());
            orderGoodsDto.setGoodNum(r.getGoodNum());
            orderGoodsDto.setModelName(r.getModelName());
            orderGoodsDto.setOriginalPrice(PriceConversion.intToString(r.getGoodAmount()));
            orderGoodsDto.setUnitPrice(PriceConversion.intToString(r.getGoodPrice()));
            orderGoodsDto.setCreateTime(r.getCreaterTime());
            orderGoodsDto.setGoodImage(r.getGoodImage());
            orderGoodsDto.setCouponAmount(String.valueOf(r.getCouponAmount()));
            orderGoodsDto.setGoodAmount(String.valueOf(r.getGoodAmount()));
            orderGoodsDto.setDiscountAmount(String.valueOf(r.getDiscountAmount()));
            orderGoodsDto.setVirtualGoodFlag(r.getVirtualFlag());
            list.add(orderGoodsDto);
        });
        return list;
    }

    /**
     * 通过订单号获取包裹信息
     *
     * @param orderNo
     * @param userId
     * @return
     */
    @Override
    public List<OrderPackageDto> getOrderPackages(String orderNo, String userId) {
        log.info("当前用户{},已经进入订单号查包裹信息. 订单号为:{}", userId, orderNo);
        shardingKey(orderNo);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ORDER_NO", orderNo);
        List<OrderPackage> orderPackageList = orderPackageService.selectByMap(hashMap);
        log.info("当前用户{},已经进入订单号查包裹信息. 订单号为:{},查询的包裹信息为:{}", userId, orderNo, orderPackageList);
        if (orderPackageList == null || orderPackageList.size() <= 0) {
            return null;
        }
        List<OrderPackageDto> list = new ArrayList<>();
        orderPackageList.stream().forEach(r -> {
            OrderPackageDto orderPackageDto = new OrderPackageDto();
            orderPackageDto.setPackageNo(r.getPackageNo());
            // 封装 订单商品
            List<OrderGoodsDto> goods = new ArrayList<>();
            HashMap<String, Object> goodsMap = new HashMap<>();
            goodsMap.put("PACKAGE_NO", r.getPackageNo());
            List<OrderGood> orderGoodList = orderGoodService.selectByMap(goodsMap);
            for (int j = 0; j < orderGoodList.size(); j++) {
                OrderGoodsDto orderGoodsDto = new OrderGoodsDto();
                orderGoodsDto.setGoodId(orderGoodList.get(j).getGoodId());
                orderGoodsDto.setGoodSku(orderGoodList.get(j).getGoodSku());
                orderGoodsDto.setGoodTitle(orderGoodList.get(j).getGoodName());
                orderGoodsDto.setGoodNum(orderGoodList.get(j).getGoodNum());
                orderGoodsDto.setModelName(orderGoodList.get(j).getModelName());
                orderGoodsDto.setCreateTime(r.getCreaterTime());


                orderGoodsDto.setOriginalPrice(PriceConversion.intToString(orderGoodList.get(j).getGoodAmount()));
                orderGoodsDto.setUnitPrice(PriceConversion.intToString(orderGoodList.get(j).getGoodPrice()));
                orderGoodsDto.setMemberPrice(PriceConversion.intToString(orderGoodList.get(j).getMemberPrice()));
                orderGoodsDto.setCouponPrice(PriceConversion.intToString(orderGoodList.get(j).getGoodAmount() / orderGoodList.get(j).getGoodNum()));
                orderGoodsDto.setGoodAmount(PriceConversion.intToString(orderGoodList.get(j).getGoodAmount()));
                orderGoodsDto.setCouponAmount(PriceConversion.intToString(orderGoodList.get(j).getCouponAmount()));
                orderGoodsDto.setDiscountAmount(PriceConversion.intToString(orderGoodList.get(j).getDiscountAmount()));
                if (orderGoodList.get(j).getGoldPrice() != null) {
                    orderGoodsDto.setGoodTotalPrice(PriceConversion.intToString(orderGoodList.get(j).getGoodPrice() * orderGoodList.get(j).getGoodNum()));
                } else {
                    orderGoodsDto.setGoodTotalPrice(PriceConversion.intToString(orderGoodList.get(j).getGoodAmount() * orderGoodList.get(j).getGoodNum()));
                }
                orderGoodsDto.setSaleId(orderGoodList.get(j).getSaleId());
                orderGoodsDto.setGoodImage(orderGoodList.get(j).getGoodImage());


                orderGoodsDto.setVirtualGoodFlag(orderGoodList.get(j).getVirtualFlag());
                orderPackageDto.setVirtualGoodFlag(orderGoodList.get(j).getVirtualFlag());
                // 封装包裹商品信息
                goods.add(orderGoodsDto);
            }
            OrderLogistics orderLogistics = orderLogisticsService.selectOneByPackageNo(orderNo, r.getPackageNo());
            log.info("通过订单号查询包裹信息方法中... 通过订单号:{}，包裹号:{},查询快递信息为:{}", orderNo, r.getPackageNo(), orderLogistics);
            if (orderLogistics != null) {
                OrderPackageLogDto orderPackageLogDto = new OrderPackageLogDto();
                orderPackageLogDto.setPackageNo(r.getPackageNo());
                orderPackageLogDto.setOrderNo(orderNo);
                orderPackageLogDto.setCheckTime(null != orderLogistics.getCheckTime() ? DateUtils.getDate(orderLogistics.getCheckTime(), "yyyy-MM-dd HH:mm:ss") : "");
                orderPackageLogDto.setSendTime(null != orderLogistics.getSendTime() ? DateUtils.getDate(orderLogistics.getSendTime(), "yyyy-MM-dd HH:mm:ss") : "");
                orderPackageLogDto.setLogisticsName(!"".equals(orderLogistics.getCompanyName()) ? orderLogistics.getCompanyName() : "");
                orderPackageLogDto.setLogisticsNo(!"".equals(orderLogistics.getLogisticsNo()) ? orderLogistics.getLogisticsNo() : "");
                // 封装收件人信息
                orderPackageDto.setLogistics(orderPackageLogDto);
            }
            orderPackageDto.setGood(goods);
            list.add(orderPackageDto);

        });
        log.info("通过订单号查询包裹信息方法中,订单号为{}，用户id:{} 封装完毕{}", orderNo, userId);
        return list;
    }

    /***
     *    获取用户的所有订单号
     *    （抽奖订单与接力购抽奖排除）
     * @return
     */
    @Override
    public List<OrderInfo> getUserAllOrderNos(String userId) {
        List<OrderInfo> orderInfoList = Lists.newArrayList();
        Long uId;
        if (StringUtil.isNotBlank(userId)) {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            uId = Long.valueOf(userId);
        } else {
            JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
            uId = jwtUser.getUserId();
        }
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(uId);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper entityWrapper = new EntityWrapper();
            Integer arr[] = {OrderType.ORDINARY, OrderType.GROUP_BUY, OrderType.TEN_YUAN_SHOP, OrderType.TEN_FOR_THREE_PIECE, OrderType.BARGAIN, OrderType.ZERO_SHOPPING, OrderType.NEW_CUSTOMER_FREE_POST, OrderType.OTHER_CHANNELS, OrderType.FREE_ORDER};
            entityWrapper.in("ORDER_TYPE", arr);
            entityWrapper.in("CREATER_ID", v);
            entityWrapper.eq("ORDER_STATUS", OrderStatus.COMPLETED.getStatus());
            List<OrderInfo> list = orderInfoMapper.selectList(entityWrapper);
            if (!list.isEmpty()) {
                orderInfoList.addAll(list);
            }
        });
        return orderInfoList;
    }

    /**
     * 通过商品id,skuid ,订单号获取订单信息
     *
     * @param orderInfoGoodVo
     * @return
     */
    @Override
    public List<OrderGood> getOrderInfoByGood(OrderInfoGoodVo orderInfoGoodVo) {
        shardingKey(orderInfoGoodVo.getOrderNo());
        HashMap<String, Object> map = new HashMap<>();
        EntityWrapper entityWrapper = new EntityWrapper();
        if (StringUtils.isNotBlank(orderInfoGoodVo.getOrderNo())) {
            entityWrapper.eq("ORDER_NO", orderInfoGoodVo.getOrderNo());
        }
        if (orderInfoGoodVo.getGoodId() != null) {
            entityWrapper.eq("GOOD_ID", orderInfoGoodVo.getGoodId());
        }
        if (orderInfoGoodVo.getSaleId() != null) {
            entityWrapper.eq("SALE_ID", orderInfoGoodVo.getSaleId());
        }
        entityWrapper.eq("CREATER_ID", orderInfoGoodVo.getUserId());
        entityWrapper.orderBy("CREATER_TIME desc");
        List<OrderGood> goods = orderGoodService.selectList(entityWrapper);
        return goods;

    }

    /**
     * 0元支付等 修改订单并上传聚水潭
     *
     * @param updateStatusVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateOrderInfo(UpdateStatusVo updateStatusVo) {
        Long userId = updateStatusVo.getUserId();
        String orderNo = updateStatusVo.getOrderNo();
        log.info("当前用户{}.. 已经进入修改订单上传聚水潭方法中，订单号为:{}，要修改的订单状态为:{}", userId, orderNo, updateStatusVo.getOrderStatus());
        shardingKey(orderNo);
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNo);
        orderInfo.setCreaterId(userId);
        OrderInfo order = orderInfoMapper.selectOne(orderInfo);
        log.info("当前用户{}.. 已经进入修改订单上传聚水潭方法中，订单号为:{}，要修改的订单状态为:{},查询结果为：{}", userId, orderNo, updateStatusVo.getOrderStatus(), order);
        if (order == null) {
            return "fail";
        }
        OrderInfo info = new OrderInfo();
        info.setOrderStatus(updateStatusVo.getOrderStatus());
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("ORDER_NO", orderNo);
        entityWrapper.eq("CREATER_ID", userId);
        log.info("当前用户{}.. 已经进入修改订单上传聚水潭方法中，订单号为{},开始修改订单状态为:{}", userId, orderNo, updateStatusVo.getOrderStatus());
        orderInfoMapper.update(info, entityWrapper);
        orderSearchSynchronizer.updateStatus(orderNo, updateStatusVo.getOrderStatus());
        log.info("当前用户{}.. 已经进入修改订单上传聚水潭方法中，订单号为{},成功修改单状态为:{}", userId, orderNo, updateStatusVo.getOrderStatus());
        //  上传聚水潭
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        List<OrderGood> goods = orderGoodService.selectByMap(map);

        List<OrderPayment> orderPaymentList = orderPaymentMapper.selectByMap(map);
        OrderPayment orderPayment = orderPaymentList.get(0);
        uploadJst(order, goods, orderPayment.getCreaterTime(), orderPayment.getPayAmount());
        log.info("当前用户{}.. 已经进入修改订单上传聚水潭方法中，订单号为{},单状态为:{}，上传聚水潭完毕", userId, orderNo, updateStatusVo.getOrderStatus());
        return "success";
    }

    /**
     * 其他类型订单上传聚水潭
     *
     * @param orderInfo
     * @param goods
     * @param date
     * @param payAmount
     */
    @Transactional(rollbackFor = Exception.class)
    public void uploadJst(OrderInfo orderInfo, List<OrderGood> goods, Date date, Integer payAmount) {
        shardingKey(orderInfo.getOrderNo());
        log.info("其他类型的订单开始上传聚水潭...订单号为{}", orderInfo.getOrderNo());
        OrdersUploadRequest request = new OrdersUploadRequest();
        request.setShopId(-1);
        request.setSoId(orderInfo.getOrderNo());
        request.setOrderDate(orderInfo.getCreaterTime());
        request.setShopStatus(String.valueOf(orderInfo.getOrderStatus()));
        request.setShopBuyerId(String.valueOf(orderInfo.getCreaterId()));

        OrderLogistics logistics = orderLogisticsService.selectOneByOrderNo(orderInfo.getOrderNo());
        if (logistics != null) {
            request.setReceiverPhone(logistics.getConsumerMobile());
            request.setReceiverAddress(logistics.getConsumerAddr());
            request.setReceiverCity(logistics.getCity());
            request.setReceiverState(logistics.getProvince());
            request.setReceiverDistrict(logistics.getArea());
            request.setReceiverName(logistics.getConsumerName());
        }
        request.setPayAmount(PriceConversion.intToString(payAmount));
        request.setOrderFrom(orderInfo.getOrderSource());
        request.setFreight(PriceConversion.intToString(orderInfo.getExpressAmount()));
        request.setRemark(orderInfo.getConsumerDesc());

        log.info("其他类型的订单开始上传聚水潭...订单号为{},订单基本信息封装完毕", orderInfo.getOrderNo());

        if (payAmount != null) {
            OrdersUploadRequest.Pay pay = new OrdersUploadRequest.Pay();
            pay.setOuterPayId(orderInfo.getOrderNo());
            pay.setPayDate(date);
            pay.setPayment("微信支付");
            pay.setSellerAccount("");
            pay.setBuyerAccount("");
            pay.setAmount(PriceConversion.intToString(payAmount));
            request.setPay(pay);
        }
        log.info("其他类型的订单开始上传聚水潭...订单号为{},支付基本信息封装完毕", orderInfo.getOrderNo());
        List<OrdersUploadRequest.Item> items = new ArrayList<>();
        goods.forEach(r -> {
            OrdersUploadRequest.Item item = new OrdersUploadRequest.Item();
            item.setName(r.getGoodName());
            item.setSkuId(String.valueOf(r.getSaleId()));
            item.setShopSkuId(r.getGoodSku());
            item.setPic(r.getGoodImage());
            item.setPropertiesValue(r.getModelName());
            item.setBasePrice(PriceConversion.intToString(r.getGoodAmount()));
            if (orderInfo.getMemberOrder()) {
                item.setAmount(PriceConversion.intToString(r.getMemberPrice() * r.getGoodNum()));
            } else {
                item.setAmount(PriceConversion.intToString(r.getGoodPrice() * r.getGoodNum()));
            }
            item.setQty(r.getGoodNum());
            item.setOuterOiId(" ");
            items.add(item);
        });
        request.setItems(items);
        log.info("其他类型的订单开始上传聚水潭...订单号为{},商品信息封装完毕", orderInfo.getOrderNo());
        // 放入消息队列中
        mQProducer.sendOrderUpload(request);

    }

    /**
     * 获取下单后的支付信息
     *
     * @param appId
     * @param openId
     * @param orderNo
     * @return
     */
    @Override
    public PayInfoDto getOrderPayInfo(String appId, String openId, String orderNo, Long userId) {
        log.info("已经进入订单支付流程了... 当前订单号为:{},用户的openi为:{},小程序的appId为:{},用户userId:{}", orderNo, openId, appId, userId);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        WxpayOrderEx wxpayOrderEx = new WxpayOrderEx();
        PayInfoDto payInfoDto = new PayInfoDto();
        // 1.校验订单是否存在
        OrderInfo orderInfo = this.getByOrderNo(orderNo);
        Assert.notNull(orderInfo, "当前订单号不存在");
        List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(orderInfo.getOrderNo());
        long currentTime = System.currentTimeMillis();
        long expireTime = orderInfo.getExpirtTime().getTime();
        if (expireTime - currentTime <= 0) { //订单过期
            throw new IllegalArgumentException("订单已过期");
        } else { //下单时锁定库存已经释放，需要重新锁定
            //虚拟商品和非虚拟商品不能同时下单
            int virtualNum = 0;
            int goodsNum = 0;
            for (OrderGood orderGood : orderGoodList) {
                if ("1".equals(orderGood.getVirtualFlag())) {
                    virtualNum++;
                } else {
                    goodsNum++;
                }
            }
            if (virtualNum == 0 && goodsNum > 0) {
                boolean flag = orderGoodService.checkOccupyTime(orderInfo.getOrderNo(), orderInfo.getOrderType());
//                Assert.isTrue(flag, "库存校验失败");
                if (!flag) {
                    //锁定库存15分钟
                    orderGoodService.occupyStock(orderNo, orderInfo.getOrderType(), new Date(), orderGoodList);
                }
            }
        }

        if (orderInfo.getOrderType() == OrderType.MM_KING) {
            Assert.notNull(orderGoodList, "兑换商品不能为空");
            Assert.isTrue(orderGoodList.size() > 0, "兑换商品不能为空!");
            Integer logId = this.mmKingProcess(orderInfo, orderGoodList);
            log.info("买买金商品兑换的logId:{}", logId);
            this.addOrderPayInfo(orderNo, orderInfo.getOrderAmount(), logId.toString(), new Date(), userId, appId, openId);
            payInfoDto.setPaid(true);
            return payInfoDto;
        }

        wxpayOrderEx.setAppId(appId);
        wxpayOrderEx.setOpenId(openId);
        wxpayOrderEx.setOutTradeNo(orderNo);
        wxpayOrderEx.setTotalFee(orderInfo.getOrderAmount());
        wxpayOrderEx.setGoodDesc(orderGoodList.get(0).getGoodName());
        // 3.发起微信支付
        try {
            ReturnData<Map<String, String>> returnData = payFeignClientl.getPayInfo(wxpayOrderEx);
            if (returnData != null && returnData.getData() != null) {
                Map<String, String> hashMap = returnData.getData();
                payInfoDto.setSign(hashMap.get("sign"));
                payInfoDto.setNonceStr(hashMap.get("nonceStr"));
                payInfoDto.setPrepayId(hashMap.get("prepayId"));
                payInfoDto.setTimestamp(hashMap.get("timestamp"));
                payInfoDto.setMwebUrl(hashMap.get("mwebUrl"));
            } else {
                throw new IllegalArgumentException("唤醒支付失败【1】");
            }
        } catch (Exception e) {
            log.info("唤醒支付失败异常:" + e);
            throw new IllegalArgumentException("唤醒支付失败【2】");
        }

        return payInfoDto;
    }

    private Integer mmKingProcess(OrderInfo info, List<OrderGood> goodList) {
        ReturnData<Boolean> data = userFeignClient.verify(info.getCreaterId(), info.getGoldNum());
        Assert.notNull(data, "使用买买金异常");
        Assert.isTrue(data.getCode() == 1, data.getDesc());
        Assert.isTrue(data.getData(), "买买金不足");
        OrdersMQDto dto = new OrdersMQDto();
        dto.setUseKingNum(info.getGoldNum());
        dto.setOrderNo(info.getOrderNo());
        dto.setUserId(info.getCreaterId());
        dto.setOrderType(info.getOrderType());
        OrdersMQDto.Goods goods = new OrdersMQDto.Goods();
        goods.setGoodId(goodList.get(0).getGoodId());
        dto.setGoods(Collections.singletonList(goods));
        return userFeignClient.exchageProc(dto);
    }

    @Override
    public double getConsumeMoney(Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(userId);
        AtomicDouble atomicDouble = new AtomicDouble(0);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            Double d = orderInfoMapper.getConsumeMoney(v);
            atomicDouble.addAndGet(d);
        });
        return atomicDouble.get();
    }

    /**
     * 查询历史消费金额
     *
     * @param orderDetailVo
     * @return
     */

    public double getConsumeMoneyTwo(OrderDetailVo orderDetailVo) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderDetailVo.getUserId());
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(Long.valueOf(orderDetailVo.getUserId()));
        AtomicDouble atomicDouble = new AtomicDouble(0);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            Double d = orderInfoMapper.getConsumeMoneyTwo(v, orderDetailVo.getOrderNo());
            atomicDouble.addAndGet(d);
        });
        return atomicDouble.get();
    }

    @Override
    public Map<String, Object> lotteryDetail(String orderNo) {

        OrderInfo info = getByOrderNo(orderNo);

        OrderDetaislDto detaislDto = new OrderDetaislDto();
        BeanUtils.copyProperties(info, detaislDto);
        detaislDto.setOrderStatusDesc(OrderStatus.toStatusMessage(info.getOrderStatus()));

        OrderGroup group = orderGroupService.getByOrderNo(orderNo);
        Assert.notNull(group, "抽奖团不存在");
        detaislDto.setGroupNo(group.getGroupNo());
        detaislDto.setGroupTime(group.getCreaterTime().getTime() + "");
        return null;
    }

    /**
     * 小程序获取订单订单详情会员，返现，商品推荐
     *
     * @param orderDetailVo
     * @return
     */
    @Override
    public OrderDetaisMemberDto getMemberDetails(OrderDetailVo orderDetailVo) {
        OrderDetaisMemberDto orderDetaisMemberDto = new OrderDetaisMemberDto();

        shardingKey(orderDetailVo.getOrderNo());
        List<String> list = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ORDER_NO", orderDetailVo.getOrderNo());
        List<OrderGood> orderGoodList = orderGoodService.selectByMap(hashMap);
        for (int i = 0; i < orderGoodList.size(); i++) {
            list.add(String.valueOf(orderGoodList.get(i).getSaleId()));
        }


        OrderInfo info = new OrderInfo();
        info.setOrderNo(orderDetailVo.getOrderNo());
        OrderInfo orderInfo = orderInfoMapper.selectOne(info);
        if (null == orderInfo) {
            return orderDetaisMemberDto;
        }
        if (null != orderInfo.getGoldPrice()) {
            orderDetaisMemberDto.setGoldAmount(PriceConversion.intToString(orderInfo.getGoldPrice()));
        } else {
            orderDetaisMemberDto.setGoldAmount("0.00");
        }
        orderDetaisMemberDto.setKingRemarks(getKingRemarks(orderInfo.getOrderNo(), orderInfo.getMemberOrder(),
                orderInfo.getOrderAmount(), orderInfo.getOrderStatus(), orderInfo.getHasAfterSale(), orderInfo.getCreaterId()));


        if (orderInfo.getOrderStatus() == OrderStatus.COMPLETED.getStatus()) {
            try {
                HashMap<String, Object> maps = new HashMap<>();
                maps.put("orderNo", orderDetailVo.getOrderNo());
                maps.put("createrId", orderDetailVo.getUserId());
                maps.put("goodSku", list);
                ReturnData<List<UserRecommendOrder>> returnData = userFeignClient.selectByGoodSku(maps);
                if (returnData != null && returnData.getData() != null) {
                    List<UserRecommendOrder> userRecommendOrderList = returnData.getData();
                    for (int i = 0; i < orderGoodList.size(); i++) {
                        for (int j = 0; j < userRecommendOrderList.size(); j++) {
                            if (orderGoodList.get(i).getSaleId().toString().equals(userRecommendOrderList.get(j).getGoodSku())) {
                                orderDetaisMemberDto.setHasRecommend(userRecommendOrderList.get(j).getStatus());
                                orderDetaisMemberDto.setRecommendId(userRecommendOrderList.get(j).getRecommendId());

                            }
                        }
                    }

                }
            } catch (Exception e) {
                log.info("调用商品推荐接口异常:" + e);
            }
        }

        return orderDetaisMemberDto;
    }


    private OrderGroup getGroupWhile(String groupNo, int count) throws InterruptedException {
        OrderGroup group = orderGroupService.getByGroupNo(groupNo);
        while (group == null) {
            Thread.sleep(1000L);
            count--;
            if (count <= 0)
                break;
            group = getGroupWhile(groupNo, count);
        }
        return group;
    }

    /**
     * 小程序获取拼团信息
     *
     * @param orderDetailVo
     * @return
     */
    @Override
    public OrderDetailGroupDto getGroupDetails(OrderDetailVo orderDetailVo) throws InterruptedException {
        Assert.notNull(orderDetailVo, "参数不能为空");
        OrderDetailGroupDto detailGroupDto = new OrderDetailGroupDto();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (StringUtils.isBlank(orderDetailVo.getGroupNo())) {
            log.info("查询团时，团号为空:{}", orderDetailVo);
            return null;
        }

        OrderGroup group = this.getGroupWhile(orderDetailVo.getGroupNo(), 10);
        if (null == group) {
            log.info("※※※ 团不存在,groupNo:{}", orderDetailVo.getGroupNo());
            return null;
        }
        detailGroupDto.setUserLevel(userDetails.getUserId().equals(group.getCreaterId()) ? 1 : 0);
        detailGroupDto.setGroupNo(group.getGroupNo());
        detailGroupDto.setCreateDate(group.getCreaterTime());

        detailGroupDto.setExpireDate(group.getExpireDate());
        if (null != group.getExpireDate())
            detailGroupDto.setLeftTime(group.getExpireDate().getTime() - new Date().getTime());

        if (userDetails.getUserId().equals(group.getLaunchUserId())) {
            detailGroupDto.setGroupRole(1);
        } else {
            detailGroupDto.setGroupRole(2);
        }
        detailGroupDto.setGroupStatus(group.getGroupStatus());
        detailGroupDto.setGroupStatusDesc(OrderGroupStatus.OrderTypeStatus(group.getGroupStatus()));
        detailGroupDto.setGroupType(group.getGroupType());
        detailGroupDto.setGroupTime(group.getModifyTime());
        detailGroupDto.setGroupPeople(group.getGroupPeople());
        detailGroupDto.setCurrentPeople(group.getCurrentPeople());

        try {
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(12); // 12 拼团
            ReturnData<Page<ActiveGood>> returnData = activeFeignClient.queryBaseList(activeGood);
            if (returnData != null && returnData.getData() != null) {
                Page<ActiveGood> page = returnData.getData();
                List<ActiveGood> list = page.getRecords();
                ActiveGood good = list.get(0);
                detailGroupDto.setGoodLimitCount(good.getLimitNum());
                detailGroupDto.setGoodLimitType(good.getLimitType());
            }
        } catch (Exception e) {
            log.info("调用查询活动商品列表接口异常:" + e);
        }


        //查询团员
        List<OrderGroupJoin> list = orderGroupJoinService.getListByGroupNo(group.getGroupNo());
        List<OrderDetailGroupDto.Member> members = Lists.newArrayListWithCapacity(list.size());
        list.stream().forEach(join -> {
            if (1 == join.getGroupMain()) {
                detailGroupDto.setLauncher(getMember(join.getJoinUserId()));
            } else {
                if (join.getJoinUserId().equals(userDetails.getUserId())) {
                    detailGroupDto.setUserLevel(2); //团员
                    detailGroupDto.setOrderNo(join.getJoinOrderNo());
                }
                members.add(getMember(join.getJoinUserId()));
            }
        });
        if (OrderGroupStatus.COMPLETED.getStatus() == group.getGroupStatus()
                && group.getCurrentPeople() < group.getGroupPeople()) {
            int randomNum = RandomUtil.randomInt(0, 9);
            String pic_url = RANDOM_USER_PIC.get(randomNum);
            members.add(new OrderDetailGroupDto.Member(pic_url, randomNum + "", randomNum + ""));
        }
        detailGroupDto.setMembers(members);
        return detailGroupDto;
    }

    /**
     * 用户信息转换
     */
    private OrderDetailGroupDto.Member getMember(Long userId) {
        if (userId == null)
            return null;
        log.info("查询用户:{}", userId);
        BaseUser user = userFeignClient.getByById(userId);
        log.info("查询用户结果:{}", user);
        if (null == user)
            return null;
        if (StringUtils.isBlank(user.getImagesUrl())) {
            int randomNum = RandomUtil.randomInt(0, 9);
            String pic_url = RANDOM_USER_PIC.get(randomNum);
            log.info("用户 {} 头像为空，随机生成头像:{}", user, pic_url);
            user.setImagesUrl(pic_url);
        }
        boolean isMember = false;
        try {
            ReturnData<Boolean> data = userFeignClient.isMember(user.getUserId());
            if (data != null)
                isMember = data.getData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return new OrderDetailGroupDto.Member(user.getImagesUrl(), user.getUserFullName(),
                user.getUnionId(), isMember, userId);
    }


    /**
     * 会员查询是否首单
     *
     * @param memberOrderVo
     * @return
     */

    public List<OrderInfo> getOrderList(MemberOrderVo memberOrderVo) {
        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(memberOrderVo.getUserId());
        List<OrderInfo> orderInfoList = Lists.newArrayList();
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper<OrderInfo> orderInfoEntityWrapper = new EntityWrapper<>();
            orderInfoEntityWrapper.in("CREATER_ID", v);
            orderInfoEntityWrapper.eq("DEL_FLAG", 1);
            orderInfoEntityWrapper.in("ORDER_TYPE", Stream.of(OrderType.TEN_YUAN_SHOP, OrderType.RECHARGE).collect(Collectors.toList()));
            orderInfoEntityWrapper.ge("CREATER_TIME", memberOrderVo.getMemberTime());
            List<Integer> statusList = new ArrayList<>();
            statusList.add(OrderStatus.TO_BE_DELIVERED.getStatus());
            statusList.add(OrderStatus.PENDING_RECEIPT.getStatus());
            statusList.add(OrderStatus.COMPLETED.getStatus());
            orderInfoEntityWrapper.in("ORDER_STATUS", statusList);
            orderInfoEntityWrapper.orderBy("MODIFY_TIME");
            List<OrderInfo> list = orderInfoMapper.selectList(orderInfoEntityWrapper);
            if (!list.isEmpty()) {
                orderInfoList.addAll(list);
            }
        });

        return orderInfoList;
    }

    /**
     * 查询快递
     *
     * @param logisticsQueryVo
     * @return
     */
    @Override
    public PollQueryResponse queryLogistics(LogisticsQueryVo logisticsQueryVo) {
        PollQueryResponse response;
        if ("虚拟发货".equals(logisticsQueryVo.getLogisticsNo())) {
            List<OrderGood> orderGoodList = orderGoodService.selectByOrderNo(logisticsQueryVo.getOrderNo());
            if (Objects.isNull(orderGoodList) || orderGoodList.size() == 0) {
                return new PollQueryResponse();
            } else {
                response = new PollQueryResponse();
                List<PollQueryResponse.Data> dataList = Lists.newArrayListWithCapacity(1);
                OrderGood orderGood = orderGoodList.get(0);
                String msg;
                if (1 == orderGood.getVirtualType()) { //优惠券
                    msg = "已发送到您的优惠券账户，可到会员-优惠券查看";
                } else if (2 == orderGood.getVirtualType()) {//买买金
                    msg = "已发送到您的买买金账户，可到会员-买买金查看";
                } else if (3 == orderGood.getVirtualType()) {//话费
                    msg = "话费已充值到您的收货手机中，预计24小时内到账，请您留意到账短信";
                } else if (4 == orderGood.getVirtualType()) { //直冲话费
                    msg = "话费已充值，预计24小时内到账，请您留意到账短信";
                } else {
                    msg = "暂无物流信息";
                }
                PollQueryResponse.Data data = new PollQueryResponse.Data();
                data.setContext(msg);
                data.setTime(DateUtils.getDate(DateUtils.SDF1, new Date()));
                data.setFtime(data.getTime());
                dataList.add(data);
                response.setResult(true);
                response.setData(dataList);
                return response;
            }
        } else {
            LogisticsVo logisticsVo = new LogisticsVo();
            logisticsVo.setOrderNo(logisticsQueryVo.getOrderNo());
            logisticsVo.setLcCode(logisticsQueryVo.getLogisticsCode());
            logisticsVo.setlId(logisticsQueryVo.getLogisticsNo());

            try {
                ReturnData<PollQueryResponse> returnData = junshuitanFeignClient.query(logisticsVo);
                if (returnData != null && returnData.getData() != null) {
                    PollQueryResponse pollQueryResponse = returnData.getData();
                    return pollQueryResponse;
                }
            } catch (Exception e) {
                log.info("调用快递查询失败:" + e);
            }
        }
        response = new PollQueryResponse();
        return response;
    }


    @Override
    public Integer getLotteryId(String groupNo) {
        OrderGroup group = orderGroupService.getByGroupNo(groupNo);
        if (null == group)
            return null;
        return group.getBusinessId();
    }

    /**
     * 判断当前是否新老用户
     *
     * @param userId
     * @return
     */
    @Override
    public boolean checkOldUser(Long userId) {
        Object obj = redisTemplate.opsForValue().get(UserConstant.IS_OLD_USER + userId);
        if (Objects.nonNull(obj)) {
            log.info("当前用户{}，已经存在缓存中", userId);
            return new Boolean(obj.toString());
        }

        Map<Long, List<Long>> mergeUserListMap = userMergeProcessor.getAllOrderToMoldMap(userId);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        mergeUserListMap.forEach((k, v) -> {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, v.get(0));
            EntityWrapper entityWrapper = new EntityWrapper();
            entityWrapper.in("CREATER_ID", v);
            Integer arr[] = {1, 2, 6, 8};
            entityWrapper.in("ORDER_STATUS", arr);
            entityWrapper.notIn("ORDER_TYPE", OrderType.LOTTERY);
            Integer count = orderInfoMapper.selectCount(entityWrapper);
            if (count > 0) {
                atomicInteger.addAndGet(count);
                return;
            }
        });
        Boolean isNewUser = atomicInteger.get() > 0;
        redisTemplate.opsForValue().set(UserConstant.IS_OLD_USER + userId, isNewUser);
        return isNewUser;
    }

    @Override
    public boolean batchUpdateStatus(String orderNo, Integer status) {
        shardingKey(orderNo);
        EntityWrapper<OrderInfo> wrapper = new EntityWrapper<>();
        OrderInfo info = new OrderInfo();
        info.setOrderStatus(status);
        wrapper.eq("ORDER_NO", orderNo);
        return update(info, wrapper);
    }


    /**
     * 过期订单
     *
     * @return
     */
    @Override
    public boolean timeoutCancel(String orderNo) {
        orderNoUtils.shardingKey(orderNo);
        OrderInfo orderInfo = getByOrderNo(orderNo);
        if (Objects.isNull(orderInfo)) {
            log.info("=> 自动过期，取消订单失败，订单未找到 orderNo:{},orderStatus:{}", orderNo, orderInfo.getOrderStatus());
            return false;
        }
        if (orderInfo.getOrderStatus() != OrderStatus.PENDING_PAYMENT.getStatus()) {
            log.info("=> 自动过期，取消订单失败，订单状态不对 orderNo:{},orderStatus:{}", orderNo, orderInfo.getOrderStatus());
            return true;
        }
        updateOrderStatus(OrderStatus.CANCELLED.getStatus(), orderInfo.getOrderNo());
        HashMap<String, Object> map = new HashMap<>();
        map.put("ORDER_NO", orderNo);
        try {
            //解绑优惠券
            useCoupon(orderInfo.getCreaterId(), orderInfo.getOrderNo(), null, false);
        }catch (Exception e){
            log.error("自动过期解绑优惠券处理错误", e);
        }
        // 取消订单--推荐返现
        Map<String, Object> cancelMap = new HashMap<>();
        cancelMap.put("orderNo", orderInfo.getOrderNo());
        cancelMap.put("userId", orderInfo.getCreaterId());
        mQProducer.sendCancel(cancelMap);
        mQProducer.returnMMKing(Long.valueOf(orderInfo.getCreaterId()), orderInfo.getOrderNo(), orderInfo.getGoldNum());
        if (orderInfo.getOrderType() == OrderType.TEN_FOR_THREE_PIECE) {
            mQProducer.sendCancelBysysj(cancelMap);
        }
        return true;
    }


    /**
     * 订单复购
     *
     * @param userId
     * @param orderNo
     */
    @Override
    public int buyAgain(Long userId, String orderNo) {
        shardingKey(orderNo);
        OrderInfo orderInfo = selectByOrderNo(orderNo, userId);
        if (orderInfo == null) {
            Assert.isTrue(false, "当前订单号不存在!");
        }

        Assert.isTrue(orderInfo.getOrderType() == OrderType.TEN_YUAN_SHOP
                &&
                (orderInfo.getOrderStatus() == OrderStatus.CANCELLED.getStatus()
                        || orderInfo.getOrderStatus() == OrderStatus.COMPLETED.getStatus()
                        || orderInfo.getOrderStatus() == OrderStatus.CLOSED.getStatus()), "不支持再次购买!");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ORDER_NO", orderNo);
        List<OrderGood> orderGoodList = orderGoodService.selectByMap(hashMap);
        if (orderGoodList.size() == 1) {
            OrderGood orderGood = orderGoodList.get(0);
            ShopCartsAddVo shopCartsAddVo = new ShopCartsAddVo();
            getShopCartsAddVo(shopCartsAddVo, orderGood, orderInfo);
            try {
                ReturnData returnData = userFeignClient.add(shopCartsAddVo);
                Boolean flag = returnData.getCode() == 1;
                if (flag) {
                    return 0;
                } else {
                    return 1;
                }
            } catch (Exception e) {
                log.info("当前订单号:{}，复购失败:{}", orderNo, e.toString());
            }
        } else {
            int failNum = 0;
            for (OrderGood orderGood : orderGoodList) {
                ShopCartsAddVo shopCartsAddVo = new ShopCartsAddVo();
                getShopCartsAddVo(shopCartsAddVo, orderGood, orderInfo);
                try {
                    ReturnData returnData = userFeignClient.add(shopCartsAddVo);
                    Boolean flag = returnData.getCode() == 1;
                    if (flag) {
                        failNum++;
                    } else {
                        failNum = failNum + 0;
                    }
                } catch (Exception e) {
                    log.info("当前订单号:{}，复购失败:{}", orderNo, orderNo, e.toString());
                }

            }
            return orderGoodList.size() == failNum ? 0 : (failNum > 0 ? 2 : 1);
        }
        return 1;
    }

    public ShopCartsAddVo getShopCartsAddVo(ShopCartsAddVo shopCartsAddVo, OrderGood orderGood, OrderInfo orderInfo) {
        shopCartsAddVo.setGoodId(orderGood.getGoodId());
        shopCartsAddVo.setGoodSku(orderGood.getGoodSku());
        shopCartsAddVo.setSaleId(orderGood.getSaleId());
        shopCartsAddVo.setGoodName(orderGood.getGoodName());
        shopCartsAddVo.setGoodImages(orderGood.getGoodImage());
        shopCartsAddVo.setModelName(orderGood.getModelName());
        shopCartsAddVo.setGoodType("16");
        shopCartsAddVo.setGoodNum(1);
        shopCartsAddVo.setGoodPrice(PriceConversion.intToString(orderGood.getGoodPrice()));
        shopCartsAddVo.setBasePrice(PriceConversion.intToString(orderGood.getGoodAmount()));
        shopCartsAddVo.setMemberPrice(PriceConversion.intToString(orderGood.getMemberPrice()));
        shopCartsAddVo.setMemberFlag(orderInfo.getMemberOrder());
        shopCartsAddVo.setCombinaFlag(orderGood.getCombinaFlag() == 1);
        shopCartsAddVo.setVirtualFlag("1".equals(orderGood.getVirtualFlag()));
        shopCartsAddVo.setSelectFlag(true);
        return shopCartsAddVo;
    }


    /**
     * 修改订单的售后
     *
     * @param userOrderVo
     * @return
     */
    @Override
    public String updateAfterSaleFlag(UserOrderVo userOrderVo) {
        log.info("当前用户:{}的订单号为:{} 开始修改售后标识", userOrderVo.getUserId(), userOrderVo.getOrderNo());
        shardingKey(userOrderVo.getOrderNo());
        OrderInfo order = new OrderInfo();
        order.setOrderNo(userOrderVo.getOrderNo());
        OrderInfo orderInfo = orderInfoMapper.selectOne(order);
        log.info("当前用户:{}的订单号为:{} 当前订单信息为{}", userOrderVo.getUserId(), userOrderVo.getOrderNo(), orderInfo);
        Assert.isTrue(orderInfo != null, "当前订单号不存在");
        OrderInfo updateOrderInfo = new OrderInfo();
        updateOrderInfo.setHasAfterSale(userOrderVo.isHasAfterSale());
        updateOrderInfo.setAfterSaleNum(Objects.isNull(orderInfo.getAfterSaleNum()) ? 1 : orderInfo.getAfterSaleNum() + 1);
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("ORDER_NO", userOrderVo.getOrderNo());
        orderInfoMapper.update(updateOrderInfo, entityWrapper);
        log.info("当前用户:{}的订单号为:{} 修改订单售后标识成功", userOrderVo.getUserId(), userOrderVo.getOrderNo());
        return "success";
    }

    /**
     * 更新收货地址
     *
     * @param orderAddressVo
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderAddress(OrderAddressVo orderAddressVo) {
        shardingKey(orderAddressVo.getOrderNo());
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("ORDER_NO", orderAddressVo.getOrderNo());
        Integer arr[] = {OrderStatus.TO_BE_DELIVERED.getStatus(), OrderStatus.PENDING_RECEIPT.getStatus()};
        entityWrapper.in("ORDER_STATUS", arr);
        long n = orderInfoMapper.selectCount(entityWrapper);
        Assert.isTrue(n > 0, "当前订单的状态不支持修改收件信息");
        boolean result = orderLogisticsService.updateOrderAddress(orderAddressVo);
        if (result) {
            //发送模板消息
            String address = orderAddressVo.getProvince() + orderAddressVo.getCity() + orderAddressVo.getArea() + orderAddressVo.getConsumerAddr();
            TemplateMessage templateMessage = new TemplateMessage();
            templateMessage.setTemplateId(TemplateIdConstants.ORDER_UPDATE_ADDRESS);
            templateMessage.setUserId(orderAddressVo.getUserId());
            templateMessage.setPage("pkgOrder/orderDetail/main?orderNo=" + orderAddressVo.getOrderNo());
            templateMessage.setKeyword1("你的地址已修改为:" + address);
            templateMessage.setKeyword2(DateUtils.getNowDate("yyyy-MM-dd HH:mm:ss"));
            messageUtils.send(templateMessage);
        }
        return result;
    }


    public List<OrderInfo> getLotteryWaitPay(Integer businessId) {
        return orderInfoMapper.getLotteryWaitPay(businessId);
    }

    @Override
    public List<UserOrderStatistics> getUsersOrdersDataForChannel(UserOrderStatisticsParam param) {
        // 取第一个userId进行分表
        Set<Long> userIdSet = param.getUserIdSet();
        for (Long userId : userIdSet) {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
            break;
        }
        return orderInfoMapper.getUsersOrdersDataForChannel(param.getUserIdSet(), param.getStartTime(), param.getEndTime());
    }

    @Override
    public void produceActiveOrder(OrderProduceDto orderProduceDto) {
        // 分表分库
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, orderProduceDto.getCreaterId());
        OrderInfo orderInfo = new OrderInfo();
        BeanUtils.copyProperties(orderProduceDto, orderInfo);
        orderInfo.setDelFlag(1);
        orderInfo.setGoldNum(0);
        orderInfo.setAfterSaleNum(0);
        orderInfo.setHasAfterSale(false);
        orderInfo.setGoldPrice(0);
        orderInfo.setExpressAmount(0);
        orderInfo.setCouponAmount(0);
        boolean result = insert(orderInfo);
        Assert.isTrue(result, "保存订单失败");
        log.info("=>生成活动订单1 orderInfo:{}", JSON.toJSONString(orderInfo));
        OrderPackage orderPackage = orderPackageService.saveOrderPackage(orderInfo, null, orderProduceDto.getVirtualGood(), null);
        log.info("=>生成活动订单2 orderPackage:{}", JSON.toJSONString(orderPackage));
        List<OrderGood> orderGoodList = Lists.newArrayListWithCapacity(1);
        OrderGood orderGood = new OrderGood();
        BeanUtils.copyProperties(orderProduceDto.getGoods(), orderGood);
        orderGood.setOrderId(orderInfo.getOrderId());
        orderGood.setOrderNo(orderInfo.getOrderNo());
        orderGood.setPackageNo(orderPackage.getPackageNo());
        orderGood.setLogisticsAmount(0);
        orderGood.setCouponAmount(0);
        orderGood.setDelFlag(1);
        orderGood.setCreaterId(orderInfo.getCreaterId());
        orderGoodList.add(orderGood);
        orderGoodService.saveOrderGoods(orderInfo, orderGoodList);
        log.info("=>生成活动订单3 orderGoodList:{}", JSON.toJSONString(orderGoodList));
        ConsignessVo consignessVo = new ConsignessVo();
        BeanUtils.copyProperties(orderProduceDto.getConsignee(), consignessVo);
        OrderLogistics orderLogistics = orderLogisticsService.saveOrderLogistics(orderInfo, orderPackage, consignessVo);
        log.info("=>生成活动订单4 orderGoodList:{}", JSON.toJSONString(orderLogistics));
        OrderPayment payment = new OrderPayment();
        BeanUtils.copyProperties(orderProduceDto.getPayment(), payment);
        payment.setOrderId(orderInfo.getOrderId());
        payment.setOrderNo(orderInfo.getOrderNo());
        payment.setCreaterTime(new Date());
        payment.setCreaterId(orderInfo.getCreaterId());
        payment.setPayTime(new Date());
        orderPaymentService.insert(payment);
        log.info("=>生成活动订单4 payment:{}", JSON.toJSONString(payment));
        SaveOrderDto saveOrderDto = new SaveOrderDto();
        saveOrderDto.setOrderNo(orderInfo.getOrderNo());
        saveOrderDto.setOrderInfo(orderInfo);
        saveOrderDto.setOrderLogistics(orderLogistics);
        saveOrderDto.setOrderGoods(orderGoodList);
        saveOrderDto.setOrderPayment(payment);
        orderSearchSynchronizer.send(saveOrderDto);
        try {
            //查询用户支付之前的身份
            boolean oldUser = this.checkOldUser(orderInfo.getCreaterId());
            if (!oldUser) { //新用户
                activeFeignClient.updateIndexCode(USER_IDENTITY);   //修改首页版本号
                log.info("--> 新用户成为老用户，清除首页缓存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("--> 新用户成为老用户，清除首页缓存成功");
        }
        // 支付回调，用户就为老用户
        Object obj = redisTemplate.opsForValue().get(UserConstant.IS_OLD_USER + orderInfo.getCreaterId());
        if ((Objects.isNull(obj) || !new Boolean(obj.toString()).booleanValue()) && OrderType.LOTTERY != orderInfo.getOrderType()) {
            redisTemplate.opsForValue().set(UserConstant.IS_OLD_USER + orderInfo.getCreaterId(), true);
        }
        // 删除历史消费金额缓存
        redisTemplate.delete("COUSUMEMONEY:" + orderInfo.getCreaterId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();
        orderDetailVo.setUserId(String.valueOf(orderInfo.getCreaterId()));
        orderDetailVo.setOrderNo(orderInfo.getOrderNo());
        orderDetailVo.setAppId(orderInfo.getAppId());
        orderDetailVo.setOpenId(orderInfo.getOpenId());
        //统计会员省钱、写入买送记录
        log.info("活动订单-开始放入会员省钱kafka：{}", orderDetailVo.toString());
        mQProducer.addMoneyItem(orderDetailVo);
        //放入会员正在处理标识 MemberOngoing
        redisTemplate.opsForValue().set(UserConstant.ISMEMBERONGOING + orderInfo.getCreaterId(), false);
        //删除订单支付成功后响应的标识
        redisTemplate.delete("wx_order:" + orderInfo.getOrderNo());
        orderPackageService.unpick(orderInfo);
        this.sendOrderSMS(orderInfo, orderGoodList);
        int retry = 10;
        boolean isMemberOnGoing;
        do {
            retry--;
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            isMemberOnGoing = redisTemplate.hasKey(UserConstant.ISMEMBERONGOING + orderInfo.getCreaterId());
            log.info("活动订单-检测会员省钱kafka是否已处理：{},isMemberOnGoing:{},retry:{}", orderDetailVo.toString(), isMemberOnGoing, retry);
        } while (isMemberOnGoing && retry > 0);

        try {
            orderKingHandler(orderInfo);
        } catch (Exception e) {
            log.error("=> 话费订单赠送买买金错误 orderNo:{},error:{}", orderInfo.getOrderNo(), e.getMessage());
        }
    }
}
