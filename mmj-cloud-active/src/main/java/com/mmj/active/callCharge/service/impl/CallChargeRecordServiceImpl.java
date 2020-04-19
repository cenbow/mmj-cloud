package com.mmj.active.callCharge.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.active.callCharge.mapper.CallChargeRecordMapper;
import com.mmj.active.callCharge.model.CallChargeGoods;
import com.mmj.active.callCharge.model.CallChargeRecord;
import com.mmj.active.callCharge.model.dto.PayInfoDto;
import com.mmj.active.callCharge.model.dto.RechargeRecordDto;
import com.mmj.active.callCharge.model.vo.BossQueryVo;
import com.mmj.active.callCharge.model.vo.RechargeOrderVo;
import com.mmj.active.callCharge.model.vo.WxpayOrderEx;
import com.mmj.active.callCharge.service.CallChargeGoodsService;
import com.mmj.active.callCharge.service.CallChargeRecordService;
import com.mmj.active.common.MQProducer;
import com.mmj.active.common.MessageUtils;
import com.mmj.active.common.feigin.PayFeignClient;
import com.mmj.active.common.feigin.ThirdFeignClient;
import com.mmj.active.common.feigin.UserFeignClient;
import com.mmj.active.common.model.UserMember;
import com.mmj.common.constants.*;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.TemplateMessage;
import com.mmj.common.model.active.RechargeVo;
import com.mmj.common.model.order.OrderProduceDto;
import com.mmj.common.model.third.recharge.RechargeDetailsVo;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@Slf4j
@Service
public class CallChargeRecordServiceImpl extends ServiceImpl<CallChargeRecordMapper, CallChargeRecord> implements CallChargeRecordService {
    @Autowired
    private CallChargeGoodsService callChargeGoodsService;
    @Autowired
    private PayFeignClient payFeignClient;
    @Autowired
    private ThirdFeignClient thirdFeignClient;
    @Autowired
    private MQProducer mqProducer;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private MessageUtils messageUtils;
    private final static String CALL_CHARGE_ORDER = "CALL_CHARGE:ORDER:";

    /**
     * 获取用户信息
     *
     * @return
     */
    private JwtUserDetails getUserDetails() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails, "缺少用户信息");
        return jwtUserDetails;
    }

    @Override
    public boolean userRight(Long userId) {
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setOrderStatus(2);
        queryCallChargeRecord.setRightOrder(true);
        queryCallChargeRecord.setCreateBy(userId);
        EntityWrapper<CallChargeRecord> callChargeGoodsEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        String startTime = String.format("%s-%s-01 00:00:00", year, month < 10 ? "0" + month : month);
        callChargeGoodsEntityWrapper.between("CREATE_AT", DateUtils.parse(startTime), new Date());
        return selectCount(callChargeGoodsEntityWrapper) == 0;
    }

    @Override
    public Page<CallChargeRecord> getCallChargeRecordList(BossQueryVo bossQueryVo) {
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setOrderNo(bossQueryVo.getOrderNo());
        EntityWrapper<CallChargeRecord> callChargeGoodsEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        callChargeGoodsEntityWrapper.orderBy("ID", false);
        Page<CallChargeRecord> page = new Page<>(bossQueryVo.getCurrentPage(), bossQueryVo.getPageSize());
        return selectPage(page, callChargeGoodsEntityWrapper);
    }

    @Override
    public RechargeRecordDto produceOrder(RechargeOrderVo rechargeOrderVo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        Integer goodsId = rechargeOrderVo.getGoodsId();
        String phoneNumber = rechargeOrderVo.getMobile();
        Assert.isTrue(CommonUtil.checkMobile(phoneNumber), "手机号格式不正确");
        CallChargeGoods callChargeGoods = callChargeGoodsService.selectById(goodsId);
        Assert.notNull(callChargeGoods, "商品已下架");
        try {
            int stockNum = callChargeGoodsService.getTodaySendNumber(rechargeOrderVo.getGoodsId());
            if (stockNum > 0) {
                stockNum = callChargeGoodsService.deductionStock(rechargeOrderVo.getGoodsId(), -1);
            }
            boolean hasRight = stockNum > 0 ? userRight(jwtUserDetails.getUserId()) : false;
            //优惠金额
            int discountedPrice = (hasRight ? callChargeGoods.getRightPrice() : callChargeGoods.getUnitPrice());
            //支付金额
            int payAmount = callChargeGoods.getOriginalPrice() - discountedPrice;
            int rechargeAmount = callChargeGoods.getOriginalPrice(); //话费充值金额
            CallChargeRecord callChargeRecord = new CallChargeRecord();
            callChargeRecord.setOrderNo(OrderUtils.gainOrderNo(jwtUserDetails.getUserId(), OrderType.RECHARGE, OrderSource.MIN.name(), OrderClassify.MAIN));
            callChargeRecord.setDisOrderNo(OrderUtils.gainOrderNo(jwtUserDetails.getUserId(), OrderType.RECHARGE, OrderSource.MIN.name(), OrderClassify.SON));
            callChargeRecord.setOrderStatus(1);
            callChargeRecord.setOrderAmount(payAmount);
            callChargeRecord.setGoodsAmount(rechargeAmount);
            callChargeRecord.setDiscountedPrice(discountedPrice);
            callChargeRecord.setGoodsId(callChargeGoods.getId());
            callChargeRecord.setRechargeStatus(-1);
            callChargeRecord.setRechargeMobile(rechargeOrderVo.getMobile());
            callChargeRecord.setRechargeAmount(rechargeAmount);
            callChargeRecord.setRightOrder(hasRight);
            callChargeRecord.setCreateBy(jwtUserDetails.getUserId());
            callChargeRecord.setUpdateBy(jwtUserDetails.getUserId());
            Assert.isTrue(insert(callChargeRecord), "下单失败，请稍后重试!");
            ReturnData<UserMember> returnData = userFeignClient.queryUserMemberInfoByUserId(jwtUserDetails.getUserId());
            UserMember userMember = returnData.getData();
            boolean memberOrder = Objects.nonNull(userMember) && userMember.getActive();
            //生成缓存订单
            OrderProduceDto orderProduceDto = new OrderProduceDto();
            orderProduceDto.setOrderNo(callChargeRecord.getOrderNo());
            orderProduceDto.setOrderType(OrderType.RECHARGE);
            orderProduceDto.setOrderStatus(OrderStatus.PENDING_PAYMENT.getStatus());
            orderProduceDto.setOrderAmount(callChargeRecord.getOrderAmount());
            orderProduceDto.setGoodAmount(callChargeRecord.getGoodsAmount());
            orderProduceDto.setDiscountAmount(callChargeRecord.getDiscountedPrice());
            orderProduceDto.setOrderSource(rechargeOrderVo.getSource());
            orderProduceDto.setOrderChannel(rechargeOrderVo.getChannel());
            orderProduceDto.setOpenId(jwtUserDetails.getOpenId());
            orderProduceDto.setAppId(jwtUserDetails.getAppId());
            orderProduceDto.setPassingData("{\"phoneNumber\":\"" + callChargeRecord.getRechargeMobile() + "\"}");
            orderProduceDto.setCreaterId(callChargeRecord.getCreateBy());
            orderProduceDto.setVirtualGood(1);
            orderProduceDto.setMemberOrder(memberOrder);
            OrderProduceDto.Goods goods = new OrderProduceDto.Goods();
            goods.setGoodId(callChargeGoods.getId());
            goods.setGoodSpu(callChargeGoods.getId().toString());
            goods.setSaleId(callChargeGoods.getId());
            goods.setGoodSku(callChargeGoods.getId().toString());
            goods.setClassCode("1010");
            goods.setWarehouseId(null);
            goods.setGoodName(callChargeGoods.getGoodsTitle());
            goods.setGoodImage(callChargeGoods.getGoodsImage());
            goods.setGoodNum(1);
            goods.setPriceType(hasRight ? 3 : 0);
            goods.setGoodPrice(callChargeGoods.getOriginalPrice());
            goods.setGoodAmount(callChargeGoods.getOriginalPrice());
            goods.setMemberPrice(callChargeGoods.getOriginalPrice());
            goods.setModelName(PriceConversion.intToString(callChargeGoods.getOriginalPrice()) + "元话费");
            goods.setVirtualFlag("1");
            goods.setVirtualType(4);
            goods.setDiscountAmount(callChargeRecord.getDiscountedPrice());
            goods.setCouponAmount(0);
            orderProduceDto.setGoods(goods);
            OrderProduceDto.Consignee consignee = new OrderProduceDto.Consignee();
            consignee.setConsumerName(jwtUserDetails.getUserFullName());
            consignee.setConsumerMobile(callChargeRecord.getRechargeMobile());
            orderProduceDto.setConsignee(consignee);
            redisTemplate.opsForValue().set(CALL_CHARGE_ORDER + orderProduceDto.getOrderNo(), JSONObject.toJSONString(orderProduceDto), 10, TimeUnit.MINUTES);
            return new RechargeRecordDto(callChargeRecord.getOrderNo());
        } catch (Exception e) {
            callChargeGoodsService.deductionStock(rechargeOrderVo.getGoodsId(), 1);
            log.error("=> 话费下单错误 vo:{},error:{}", JSONObject.toJSONString(rechargeOrderVo), e.toString());
            throw new IllegalArgumentException("充值失败，请稍后重试！");
        }
    }

    @Override
    public PayInfoDto getOrderPayInfo(String appId, String openId, String orderNo) {
        JwtUserDetails jwtUserDetails = getUserDetails();
        log.info("已经进入订单支付流程了... 当前订单号为:{},用户的openi为:{},小程序的appId为:{},用户userId:{}", orderNo, openId, appId, jwtUserDetails.getUserId());
        WxpayOrderEx wxpayOrderEx = new WxpayOrderEx();
        PayInfoDto payInfoDto = new PayInfoDto();
        // 1.校验订单是否存在
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setOrderNo(orderNo);
        queryCallChargeRecord.setOrderStatus(1);
        queryCallChargeRecord.setActive(true);
        EntityWrapper<CallChargeRecord> callChargeOrdersEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        CallChargeRecord callChargeRecord = selectOne(callChargeOrdersEntityWrapper);
        Assert.notNull(callChargeRecord, "订单已被处理，请重新下单");
        // 2. 校验订单实际的价格与库存等
    /*    boolean flag = checkGoodNumByPay(orderNo, userId, wxpayOrderEx);
        Assert.isTrue(flag, "价格计算错误或库存不足!");*/
        wxpayOrderEx.setAppId(appId);
        wxpayOrderEx.setOpenId(openId);
        wxpayOrderEx.setOutTradeNo(orderNo);
        wxpayOrderEx.setTotalFee(callChargeRecord.getOrderAmount());
        wxpayOrderEx.setGoodDesc(PriceConversion.intToString(callChargeRecord.getGoodsAmount()) + "元话费充值-" + callChargeRecord.getRechargeMobile());
        // 3.发起微信支付
        try {
            ReturnData<Map<String, String>> returnData = payFeignClient.getPayInfo(wxpayOrderEx);
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
            log.info("调取支付接口异常:" + e);
            throw new IllegalArgumentException("唤醒支付失败【2】");
        }
        return payInfoDto;
    }

    @Override
    public void payFail(RechargeVo rechargeVo) {
        // 1.校验订单是否存在
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setOrderNo(rechargeVo.getOrderNo());
        queryCallChargeRecord.setOrderStatus(1);
        queryCallChargeRecord.setActive(true);
        EntityWrapper<CallChargeRecord> callChargeOrdersEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        CallChargeRecord callChargeRecord = selectOne(callChargeOrdersEntityWrapper);
        if (Objects.nonNull(callChargeRecord)) {
            callChargeGoodsService.deductionStock(callChargeRecord.getGoodsId(), 1);
            CallChargeRecord updateCallChargeRecord = new CallChargeRecord();
            updateCallChargeRecord.setActive(false);
            queryCallChargeRecord = new CallChargeRecord();
            queryCallChargeRecord.setId(callChargeRecord.getId());
            EntityWrapper<CallChargeRecord> updateCallChargeOrdersEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
            update(updateCallChargeRecord, updateCallChargeOrdersEntityWrapper);
        }
    }

    @Override
    public void paySuccess(String orderNo, JSONObject jsonObject) {
        CallChargeRecord updateCallChargeRecord = new CallChargeRecord();
        updateCallChargeRecord.setOrderStatus(2);
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setOrderStatus(1);
        queryCallChargeRecord.setActive(true);
        queryCallChargeRecord.setOrderNo(orderNo);
        EntityWrapper<CallChargeRecord> callChargeGoodsEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        boolean result = update(updateCallChargeRecord, callChargeGoodsEntityWrapper);
        log.info("=> 话费订单:{},支付成功处理:{}", orderNo, result);
        if (result) {
            String content = redisTemplate.opsForValue().get(CALL_CHARGE_ORDER + orderNo);
            if (StringUtils.isNotEmpty(content)) {
                OrderProduceDto orderProduceDto = JSONObject.parseObject(content, OrderProduceDto.class);
                orderProduceDto.setOrderStatus(OrderStatus.PAYMENTED.getStatus());
                Integer totalFee = jsonObject.getInteger("totalFee");
                String transactionId = jsonObject.getString("transactionId");
//                Long createrId = jsonObject.getLong("createrId");
//                String appId = jsonObject.getString("appId");
//                String openId = jsonObject.getString("openId");
                OrderProduceDto.Payment payment = new OrderProduceDto.Payment();
                payment.setPayType("1");
                payment.setPayAmount(totalFee);
                payment.setPayNo(transactionId);
                payment.setPayStatus(1);
                payment.setPayDesc("话费充值");
                orderProduceDto.setPayment(payment);
                mqProducer.produceOrder(orderProduceDto);
                sendPayRecharge(orderProduceDto.getCreaterId(), orderProduceDto.getOrderNo()
                        , orderProduceDto.getGoods().getGoodName(), new Date(), PriceConversion.intToString(totalFee));
            }
        }
    }

    /**
     * 写入充值状态信息
     *
     * @param record
     * @param rechargeDto
     */
    private void writeRecharge(CallChargeRecord record, RechargeDto rechargeDto) {
        CallChargeRecord updateCallChargeOrdersByPrimaryKey = new CallChargeRecord();
        updateCallChargeOrdersByPrimaryKey.setId(record.getId());
        updateCallChargeOrdersByPrimaryKey.setRechargeStatus(rechargeDto.getResultCode());
        updateCallChargeOrdersByPrimaryKey.setRemarks(rechargeDto.getRemark());
        updateCallChargeOrdersByPrimaryKey.setRechargeErrorResponse(rechargeDto.getData());
        boolean result = updateById(updateCallChargeOrdersByPrimaryKey);
        record.setRechargeStatus(rechargeDto.getResultCode());
        record.setRemarks(rechargeDto.getRemark());
        record.setRechargeErrorResponse(rechargeDto.getData());
        log.info("=> 话费充值 disOrderNo:{},result:{},nn:{}", record.getDisOrderNo(), JSONObject.toJSONString(result), result);
    }

    @Override
    public void recharge(String orderNo) {
        log.info("=> 话费充值 orderNo:{}", orderNo);
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setActive(true);
        queryCallChargeRecord.setOrderNo(orderNo);
        EntityWrapper<CallChargeRecord> callChargeRecordEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        callChargeRecordEntityWrapper.orderBy("ID", false);
        CallChargeRecord callChargeRecord = selectOne(callChargeRecordEntityWrapper);
        Assert.notNull(callChargeRecord, "充值记录不存在");
        RechargeDto rechargeDto;
        com.mmj.common.model.third.recharge.RechargeVo rechargeVo;
        RechargeDetailsVo rechargeDetailsVo;
        //充值状态 -1待充值 0充值中 1充值成功 2充值超时（待重试） 3充值失败 4充值订单过期
        switch (callChargeRecord.getRechargeStatus().intValue()) {
            case -1:
                rechargeVo = new com.mmj.common.model.third.recharge.RechargeVo();
                rechargeVo.setAmount(PriceConversion.intToString(callChargeRecord.getRechargeAmount()));
                rechargeVo.setMobile(callChargeRecord.getRechargeMobile());
                rechargeVo.setOuterId(callChargeRecord.getDisOrderNo());
                rechargeDto = recharge(rechargeVo);
                writeRecharge(callChargeRecord, rechargeDto);
                syncSuccessToOrderStatus(callChargeRecord.getOrderNo(), rechargeDto);
                break;
            case 0:
                rechargeDetailsVo = new RechargeDetailsVo();
                rechargeDetailsVo.setOuterId(callChargeRecord.getDisOrderNo());
                rechargeDto = details(rechargeDetailsVo);
                if (rechargeDto.getResultCode() != 0) {
                    writeRecharge(callChargeRecord, rechargeDto);
                    syncSuccessToOrderStatus(callChargeRecord.getOrderNo(), rechargeDto);
                }
                break;
            case 2:
                rechargeDetailsVo = new RechargeDetailsVo();
                rechargeDetailsVo.setOuterId(callChargeRecord.getDisOrderNo());
                rechargeDto = details(rechargeDetailsVo);
                if (rechargeDto.getResultCode() != 2) {
                    writeRecharge(callChargeRecord, rechargeDto);
                    syncSuccessToOrderStatus(callChargeRecord.getOrderNo(), rechargeDto);
                }
                break;
            case 3:
                CallChargeRecord disCallChargeRecord = new CallChargeRecord();
                BeanUtils.copyProperties(callChargeRecord, disCallChargeRecord);
                disCallChargeRecord.setId(null);
                disCallChargeRecord.setDisOrderNo(OrderUtils.gainOrderNo(callChargeRecord.getCreateBy(), OrderType.RECHARGE, OrderSource.MIN.name(), OrderClassify.SON));
                disCallChargeRecord.setRechargeStatus(-1);
                disCallChargeRecord.setRemarks("");
                disCallChargeRecord.setRechargeErrorResponse("");
                rechargeVo = new com.mmj.common.model.third.recharge.RechargeVo();
                rechargeVo.setAmount(PriceConversion.intToString(disCallChargeRecord.getRechargeAmount()));
                rechargeVo.setMobile(disCallChargeRecord.getRechargeMobile());
                rechargeVo.setOuterId(disCallChargeRecord.getDisOrderNo());
                rechargeDto = recharge(rechargeVo);
                disCallChargeRecord.setRechargeStatus(rechargeDto.getResultCode());
                disCallChargeRecord.setRemarks(rechargeDto.getRemark());
                disCallChargeRecord.setRechargeErrorResponse(rechargeDto.getData());
                boolean result = insert(disCallChargeRecord);
                log.info("=> 话费充值重试 orderNo:{},disOrderNo:{},result:{},status:{}", disCallChargeRecord.getOrderNo(), disCallChargeRecord.getDisOrderNo(), JSONObject.toJSONString(result), result);
                if (result) {
                    CallChargeRecord updateCallChargeRecord = new CallChargeRecord();
                    updateCallChargeRecord.setActive(false);
                    queryCallChargeRecord = new CallChargeRecord();
                    queryCallChargeRecord.setDisOrderNo(callChargeRecord.getDisOrderNo());
                    EntityWrapper<CallChargeRecord> callChargeGoodsEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
                    result = update(updateCallChargeRecord, callChargeGoodsEntityWrapper);
                    log.info("=> 话费充值重试，原业务订单置为无效 orderNo:{},disOrderNo:{},result:{}", callChargeRecord.getOrderNo(), callChargeRecord.getDisOrderNo(), result);
                    syncSuccessToOrderStatus(disCallChargeRecord.getOrderNo(), rechargeDto);
                }
                break;
        }
        return;
    }

    @Override
    public void callback(RechargeDto rechargeDto) {
        CallChargeRecord updateCallChargeRecord = new CallChargeRecord();
        updateCallChargeRecord.setRechargeStatus(rechargeDto.getResultCode());
        CallChargeRecord queryCallChargeRecord = new CallChargeRecord();
        queryCallChargeRecord.setDisOrderNo(rechargeDto.getOuterId());
        EntityWrapper<CallChargeRecord> callChargeGoodsEntityWrapper = new EntityWrapper<>(queryCallChargeRecord);
        boolean result = update(updateCallChargeRecord, callChargeGoodsEntityWrapper);
        log.info("=> 话费充值，异步响应 content:{},result:{}", JSONObject.toJSONString(rechargeDto), result);
        if (rechargeDto.isResultStatus()) {
            //充值成功-更新订单状态
            CallChargeRecord callChargeRecord = selectOne(callChargeGoodsEntityWrapper);
            if (Objects.nonNull(callChargeRecord)) {
                syncSuccessToOrderStatus(callChargeRecord.getOrderNo(), rechargeDto);
                if (rechargeDto.isResultStatus()) {
                    sendRecharge(callChargeRecord.getCreateBy(), callChargeRecord.getOrderNo(), callChargeRecord.getRechargeMobile(), PriceConversion.intToString(callChargeRecord.getGoodsAmount()), PriceConversion.intToString(callChargeRecord.getDiscountedPrice()), new Date());
                }
            }
        }
    }

    /**
     * 同步话费充值成功状态至订单
     *
     * @param orderNo
     */
    private void syncSuccessToOrderStatus(String orderNo, RechargeDto rechargeDto) {
        if (rechargeDto.isResultStatus())
            mqProducer.syncOrderStatus(orderNo);
    }

    /**
     * 话费充值
     *
     * @param rechargeVo
     * @return
     */
    private RechargeDto recharge(com.mmj.common.model.third.recharge.RechargeVo rechargeVo) {
        log.info("=> 话费充值请求参数：{}", JSON.toJSONString(rechargeVo));
        ReturnData<RechargeDto> returnData = thirdFeignClient.recharge(rechargeVo);
        return returnData.getData();
    }

    /**
     * 查询话费充值详情
     *
     * @param rechargeDetailsVo
     * @return
     */
    private RechargeDto details(RechargeDetailsVo rechargeDetailsVo) {
        ReturnData<RechargeDto> returnData = thirdFeignClient.details(rechargeDetailsVo);
        return returnData.getData();
    }


    /**
     * 话费支付成功模板消息
     */
    private void sendPayRecharge(Long userId, String orderNo, String goodsTitle, Date payDate, String payAmount) {
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setUserId(userId);
        templateMessage.setTemplateId(TemplateIdConstants.RECHARGE_PAY_SUCCESS);
        templateMessage.setPage("pkgTimeSeckill/main?ed=1");
        templateMessage.setKeyword1(goodsTitle);
        templateMessage.setKeyword2(DateUtils.getDate(payDate, DateUtils.DATE_PATTERN_5));
        templateMessage.setKeyword3(payAmount + "元");
        templateMessage.setKeyword4("已完成");
        templateMessage.setKeyword5("预计2小时内到账，如有疑问请关注【买买家】公众号，在【联系我们】→【商城客服】中，咨询客服处理。送你1折起秒爆款的机会，点击领取>>");
        messageUtils.send(templateMessage);
    }

    /**
     * 话费充值成功模板消息
     */
    private void sendRecharge(Long userId, String orderNo, String rechargeMobile,
                              String rechargeAmount, String discountedPrice, Date orderDate) {
        TemplateMessage templateMessage = new TemplateMessage();
        templateMessage.setUserId(userId);
        templateMessage.setTemplateId(TemplateIdConstants.RECHARGE_SUCCESS);
        templateMessage.setPage("pkgProbationFree/main?ed=1");
        templateMessage.setKeyword1(rechargeMobile);
        templateMessage.setKeyword2("话费");
        templateMessage.setKeyword3(rechargeAmount + "元");
        templateMessage.setKeyword4(discountedPrice + "元");
        templateMessage.setKeyword5(DateUtils.getDate(orderDate, DateUtils.DATE_PATTERN_5));
        templateMessage.setKeyword6("短信可能被拦截，请以运营商话费余额查询为准。感谢对买买家的信任，点击领取十元三件包邮任选福利>>");
        messageUtils.send(templateMessage);
    }
}
