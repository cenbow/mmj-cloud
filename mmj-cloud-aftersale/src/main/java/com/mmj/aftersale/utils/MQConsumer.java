package com.mmj.aftersale.utils;

import com.alibaba.fastjson.JSONObject;
import com.mmj.aftersale.service.AfterSalesService;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.wx.RefundSuccess;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class MQConsumer {

    //  聚水潭-取消订单
    private static final String ORDER_CANCEL_TOPIC = "mmj-order-cancel-topic";

    // 取消订单插入售后订单
    private static final String RECOMMEND_AFTER_ADD_TOPIC = "mmj-order-recommend-after-add-topic";

    @Autowired
    private AfterSalesService afterSalesService;


    /**
     * 聚水潭取消订单取消订单
     *
     * @param records
     */
    @KafkaListener(topics = {ORDER_CANCEL_TOPIC})
    public void sendCancelOrder(List<ConsumerRecord<?, ?>> records) {
        log.info("进入聚水潭取消订单处理售后方法....");
        for (ConsumerRecord<?, ?> record : records) {
            Optional<?> kafkaMessage = Optional.ofNullable(record.value());
            log.info("接受消息为为:" + record);
            if (kafkaMessage.isPresent()) {
                Object message = record.value();
                String topic = record.topic();
                if (message == null) {
                    log.info("对接聚水潭取消订单,参数为空，{}", message);
                    continue;
                }
                Map<String, String> map = JSONObject.parseObject(message.toString(), Map.class);
                afterSalesService.jstCancelAfterSales(map);
            }
        }
    }

    /**
     * 订单取消订单添加售后订单
     *
     * @param params
     */
//    @KafkaListener(topics = {RECOMMEND_AFTER_ADD_TOPIC})
//    public void addAfter(List<String> params) {
//        log.info("进入取消订单添加售后订单回调....");
//        for(String s : params){
//            AddAfterSaleVo addAfterSaleVo = JSONObject.parseObject(s, AddAfterSaleVo.class);
//            afterSalesService.addAfterSale(addAfterSaleVo);
//        }
//    }
    @KafkaListener(topics = {MQTopicConstant.WX_REFUND_SUCCESS})
    public void addAfter(List<String> params) {
        log.info("售后模块，微信退款完成，修改订单状态:{}", params.toString());
        for (String s : params) {
            RefundSuccess rs = JSONObject.parseObject(s, RefundSuccess.class);
            if (null == rs)
                return;
            afterSalesService.updateStatus(rs.getOutTradeNo());
        }
    }

}
