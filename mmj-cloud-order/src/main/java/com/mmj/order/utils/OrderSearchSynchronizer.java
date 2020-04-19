package com.mmj.order.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.constants.OrderType;
import com.mmj.common.model.order.*;
import com.mmj.common.utils.SnowflakeIdWorker;
import com.mmj.order.model.OrderGood;
import com.mmj.order.model.OrderInfo;
import com.mmj.order.model.OrderLogistics;
import com.mmj.order.model.OrderPayment;
import com.mmj.order.model.dto.SMSInfoDto;
import com.mmj.order.model.dto.SaveOrderDto;
import com.mmj.order.model.vo.OrderSaveVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class OrderSearchSynchronizer {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private SnowflakeIdWorker snowflakeIdWorker;

    private SMSProcessor smsProcessor;

    public OrderSearchSynchronizer(KafkaTemplate<String, Object> kafkaTemplate, SnowflakeIdWorker snowflakeIdWorker, SMSProcessor smsProcessor) {
        this.kafkaTemplate = kafkaTemplate;
        this.snowflakeIdWorker = snowflakeIdWorker;
        this.smsProcessor = smsProcessor;
    }

    public void payOrder(String orderNo, Integer amount, Date payTime, Integer type) {
        OrderPayDto dto = new OrderPayDto();
        dto.setOrderNo(orderNo);
        dto.setPayAmount(amount);
        dto.setPayTime(payTime);
        dto.setPayType(type);
        long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQTopicConstant.MMJ_SYNC_ORDER_PAY_TO_ES_TOPIC, key + "", JSON.toJSONString(dto));
        log.info("支付后同步支付信息到ES:{} ", JSON.toJSONString(dto));
    }

    public void updateStatus(String orderNo, Integer status) {
        OrderStatusMQDto dto = new OrderStatusMQDto();
        dto.setOrderNo(orderNo);
        dto.setOrderStatus(status);
        long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQTopicConstant.SYNC_ORDER_STATUS_TO_ES_TOPIC, key + "", JSON.toJSONString(dto));
        log.info("同步订单状态到ES:{} ", JSON.toJSONString(dto));

    }


    public void send(SaveOrderDto orderDto) {
        log.info("同步订单到消息队列,orderNo:{}", orderDto.getOrderNo());
        OrderSaveVo orderSaveVo = orderDto.getOrderSaveVo();
        OrderInfo orderInfo = orderDto.getOrderInfo();
        OrdersMQDto dto = new OrdersMQDto();
        BeanUtils.copyProperties(orderInfo, dto);
        dto.setUserId(orderInfo.getCreaterId());
        dto.setMemberOrder(orderInfo.getMemberOrder());
        dto.setCouponCode(Objects.isNull(orderSaveVo) ? null : orderSaveVo.getCouponCode());
        dto.setUseKingNum(Objects.isNull(orderSaveVo) ? null : orderSaveVo.getUseKingNum());
        dto.setOrderDate(new Date());
        dto.setType(Objects.isNull(orderSaveVo) ? null : orderSaveVo.getType());
        dto.setAppId(orderInfo.getAppId());
        dto.setOpenId(orderInfo.getOpenId());
        dto.setSource(orderInfo.getOrderSource());
        dto.setChannel(orderInfo.getOrderChannel());
        OrderLogistics orderLogistics = orderDto.getOrderLogistics();
        OrderConsigneeMQDto consigneeMQDto = new OrderConsigneeMQDto();
        consigneeMQDto.setName(orderLogistics.getConsumerName());
        consigneeMQDto.setOrderNo(orderLogistics.getOrderNo());
        consigneeMQDto.setTelNumber(orderLogistics.getConsumerMobile());
        dto.setConsignee(consigneeMQDto);
        OrderPayDto orderPayDto = new OrderPayDto();
        OrderPayment orderPayment = orderDto.getOrderPayment();
        if (Objects.nonNull(orderPayment)) {
            orderPayDto.setOrderNo(orderPayment.getOrderNo());
            orderPayDto.setPayAmount(orderPayment.getPayAmount());
            orderPayDto.setPayTime(orderPayment.getPayTime());
            orderPayDto.setPayType(Integer.parseInt(orderPayment.getPayType()));
            dto.setPay(orderPayDto);
        }
        List<OrderGood> orderGoodList = orderDto.getOrderGoods();
        List<OrdersMQDto.Goods> goodsList = Lists.newArrayListWithCapacity(orderGoodList.size());
        orderGoodList.forEach(orderGood -> {
            OrdersMQDto.Goods good = new OrdersMQDto.Goods();
            good.setGoodName(orderGood.getGoodName());
            good.setGoodImage(orderGood.getGoodImage());
            good.setGoodSku(orderGood.getGoodSku());
            good.setGoodNum(orderGood.getGoodNum());
            good.setGoodId(orderGood.getGoodId());
            goodsList.add(good);
        });
        dto.setGoods(goodsList);
        long key = snowflakeIdWorker.nextId();
        kafkaTemplate.send(MQTopicConstant.SYNC_ORDER_TO_ES_TOPIC, key + "", JSON.toJSONString(dto));
        log.info("下单成功后同步订单到消息队列:{} ", JSON.toJSONString(dto));

        //发送短信
        if (goodsList.size() == 0) {
            log.info("下单后商品不存在:{}", goodsList);
            return;
        }
        SMSInfoDto infoDto = new SMSInfoDto(orderInfo.getCreaterId(),
                orderInfo.getOrderNo(), goodsList.get(0).getGoodName());

        if (OrderType.TEN_YUAN_SHOP == orderInfo.getOrderType())
            smsProcessor.sendTenShopUnPaySMS(infoDto);
        if (OrderType.TEN_FOR_THREE_PIECE == orderInfo.getOrderType())
            smsProcessor.sendTenPriceUnPaySMS(infoDto);
    }


    /**
     * 支付成功后-处理团订单
     *
     * @param orderInfo
     */
    public void handlerGroupOrder(OrderInfo orderInfo) {
        log.info("同步团订单到消息队列,orderNo:{}", orderInfo.getOrderNo());
        long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(orderInfo);
        kafkaTemplate.send(MQTopicConstant.ORDER_GROUP_TOPIC, key + "", content);
        log.info("支付成功后同步团订单到消息队列:{} ", content);

    }

    /**
     * 拆单后同步订单到ES
     *
     * @param ordersPackageMQDtoList
     */
    public void sendPackageEs(List<OrdersPackageMQDto> ordersPackageMQDtoList) {
        log.info("同步包裹订单到消息队列,orderNo:{},包裹数量:{}", ordersPackageMQDtoList.get(0).getOrderNo(), ordersPackageMQDtoList.size());
        long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(ordersPackageMQDtoList);
        kafkaTemplate.send(MQTopicConstant.SYNC_PACKAGE_TO_ES_TOPIC, key + "", content);
        log.info("同步包裹订单到消息队列:{} ", content);
    }

    /**
     * 同步包裹订单号状态到ES
     *
     * @param orderStatusMQDtoList
     */
    public void updateStatus(List<OrderStatusMQDto> orderStatusMQDtoList) {
        long key = snowflakeIdWorker.nextId();
        String content = JSONObject.toJSONString(orderStatusMQDtoList);
        kafkaTemplate.send(MQTopicConstant.SYNC_PACKAGE_STATUS_TO_ES_TOPIC, key + "", content);
        log.info("同步包裹订单状态到消息队列:{} ", content);
    }
}
