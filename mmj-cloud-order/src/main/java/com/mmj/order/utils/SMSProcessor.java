package com.mmj.order.utils;

import com.mmj.common.model.MessageConstants;
import com.mmj.common.model.SmsDto;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.dto.SMSInfoDto;
import com.mmj.order.service.OrderLogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SMSProcessor {

    @Autowired
    private MQProducer mQProducer;

    @Autowired
    private OrderLogisticsService logisticsService;

    private SmsDto genSmsDto(String orderNo, Long userId) {
        return new SmsDto(com.mmj.common.utils.StringUtils.getUUid(), userId, orderNo);
    }

    private void modifyRecvInfo(SmsDto sms, Map<String, Object> smsMap, String orderNo) {
        OrderLogistics logistics = logisticsService.selectOneByOrderNo(orderNo);
        if (logistics == null)
            return;
        smsMap.put("nickname", logistics.getConsumerName());
        sms.setPhone(logistics.getConsumerMobile());
        sms.setNickName(logistics.getConsumerName());
    }

    private OrderLogistics getLogistic(String orderNo) {
        OrderLogistics logistics = logisticsService.selectOneByOrderNo(orderNo);
        if (logistics == null)
            return null;
        return logistics;
    }


    //一分钱抽奖-支付成功后-支付成功后1小时没成团推送
    public void sendLotteryPaySMS(SMSInfoDto smsInfoDto) {
        log.info("一分钱抽奖-支付成功后-支付成功后1小时没成团推送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        sms.setMsgType(MessageConstants.msgType.NODE);

        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.LOTTERY_ONE);
        sms.setModel(MessageConstants.module.LOTTERY);
        sms.setType(MessageConstants.type.LOTTERY_ONE);
        smsMap.put("code", "");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //一分钱抽奖-开奖结果通知	boss后台完成开奖后立即发送

    /**
     * 骚扰用户不发了
     *
     * @param smsInfoDto
     */
    public void sendLotteryOppenSMS(SMSInfoDto smsInfoDto) {
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        sms.setMsgType(MessageConstants.msgType.NODE);

        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.LOTTERY_TWO);
        sms.setModel(MessageConstants.module.LOTTERY);
        sms.setType(MessageConstants.type.LOTTERY_TWO);
        smsMap.put("code", smsInfoDto.getCode());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //一分钱抽奖-拼团成功后-拼团成功后立即推送
    public void sendLotteryGroupedSMS(SMSInfoDto smsInfoDto) {
        log.info("一分钱抽奖-拼团成功后-拼团成功后立即推送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        sms.setMsgType(MessageConstants.msgType.NODE);

        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.LOTTERY_THREE);
        sms.setModel(MessageConstants.module.LOTTERY);
        sms.setType(MessageConstants.type.LOTTERY_THREE);
        smsMap.put("code", smsInfoDto.getCode());

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //二人团-下单结算未支付-触发结算但1小时未支付
    public void sendTwoGroupPaySMS(SMSInfoDto smsInfoDto) {
        log.info("二人团-下单结算未支付-触发结算但1小时未支付:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.GROUP_ONE);
        sms.setModel(MessageConstants.module.GROUP);
        sms.setType(MessageConstants.type.GROUP_ONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //店铺订单-下单结算未支付-触发结算但1小时未支付
    public void sendTenShopUnPaySMS(SMSInfoDto smsInfoDto) {
        log.info("店铺订单-下单结算未支付-触发结算但1小时未支付:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.TEN_YUAN_SHOP_ONE);
        sms.setModel(MessageConstants.module.TEN_YUAN_SHOP);
        sms.setType(MessageConstants.type.TEN_YUAN_SHOP_ONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //店铺订单-支付成功后-付款成功后立即发送
    public void sendTenShopPaidSMS(SMSInfoDto smsInfoDto) {
        log.info("店铺订单-支付成功后-付款成功后立即发送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.TEN_YUAN_SHOP_TWO);
        sms.setModel(MessageConstants.module.TEN_YUAN_SHOP);
        sms.setType(MessageConstants.type.TEN_YUAN_SHOP_TWO);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //砍价-支付成功后
    public void sendBargainPaidSMS(SMSInfoDto smsInfoDto) {
        log.info("砍价-支付成功后:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.BARGAIN_ONE);
        sms.setModel(MessageConstants.module.BARGAIN);
        sms.setType(MessageConstants.type.BARGAIN_ONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //10元三件-支付成功后-支付完后即时推送
    public void sendTenPricePaidSMS(SMSInfoDto smsInfoDto) {
        log.info("10元三件-支付成功后-支付完后即时推送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.TEN_THREE_ONE);
        sms.setModel(MessageConstants.module.TEN_THREE);
        sms.setType(MessageConstants.type.TEN_THREE_ONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //10元三件-下单结算未支付-触发结算但1小时未支付
    public void sendTenPriceUnPaySMS(SMSInfoDto smsInfoDto) {
        log.info("10元三件-下单结算未支付-触发结算但1小时未支付:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.TEN_THREE_TWO);
        sms.setModel(MessageConstants.module.TEN_THREE);
        sms.setType(MessageConstants.type.TEN_THREE_TWO);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }


    //限时秒杀-支付成功后-支付成功后1小时没成团推送
    public void sendFlashSalePaidSMS(SMSInfoDto smsInfoDto) {
        log.info("限时秒杀-支付成功后-支付成功后1小时没成团推送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        this.modifyRecvInfo(sms, smsMap, smsInfoDto.getOrderNo());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.FLASH_ONE);
        sms.setModel(MessageConstants.module.FLASH);
        sms.setType(MessageConstants.type.FLASH_ONE);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }

    //物流-确认收货-用户触发确认收货后立即发送
    public void sendReceivedMS(SMSInfoDto smsInfoDto) {
        log.info("物流-确认收货-用户触发确认收货后立即发送:{}", smsInfoDto.getOrderNo());
        SmsDto sms = this.genSmsDto(smsInfoDto.getOrderNo(), smsInfoDto.getUserId());
        Map<String, Object> smsMap = new HashMap<>();
        smsMap.put("orderno", smsInfoDto.getOrderNo());
        smsMap.put("url", "");
        sms.setMsgType(MessageConstants.msgType.NODE);

        OrderLogistics logistics = getLogistic(smsInfoDto.getOrderNo());
        if (null == logistics)
            return;
        smsMap.put("nickname", logistics.getConsumerName());
        smsMap.put("expressCode", logistics.getLogisticsNo());
        sms.setPhone(logistics.getConsumerMobile());
        sms.setNickName(logistics.getConsumerName());

        smsMap.put("title", smsInfoDto.getGoodName());

        sms.setNode(MessageConstants.type.LOGISTICS_TWO);
        sms.setModel(MessageConstants.module.LOGISTICS);
        sms.setType(MessageConstants.type.LOGISTICS_TWO);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("orderNo", smsInfoDto.getOrderNo());
        sms.setParams(paramMap);
        sms.setSmsParams(smsMap);
        mQProducer.sendSmsMsg(sms);
    }
}
