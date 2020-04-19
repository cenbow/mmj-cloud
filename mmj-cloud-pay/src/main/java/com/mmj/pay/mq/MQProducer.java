package com.mmj.pay.mq;

import com.alibaba.fastjson.JSON;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.pay.model.WxpayOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MQProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 支付成功通知
     *
     * @param wxpayOrder
     */
    public void sendOrderPay(WxpayOrder wxpayOrder) {
        Long increment = redisTemplate.opsForValue().increment(MQTopicConstant.WX_ORDER_TOPIC + wxpayOrder.getOutTradeNo(), 1);
        redisTemplate.expire(MQTopicConstant.WX_ORDER_TOPIC + wxpayOrder.getOutTradeNo(), 60, TimeUnit.SECONDS);
        if(increment == 1){
            log.info("微信支付成功MQProducer send msg {} start", JSON.toJSONString(wxpayOrder));
            kafkaTemplate.send(MQTopicConstant.WX_ORDER_TOPIC, JSON.toJSONString(wxpayOrder));
            log.info("微信支付成功MQProducer send msg {} success", JSON.toJSONString(wxpayOrder));
        }
    }

}

