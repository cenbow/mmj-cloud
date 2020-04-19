package com.mmj.good.util;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.constants.MQTopicConstant;
import com.mmj.common.model.order.OrderStatusMQDto;
import com.mmj.common.model.order.OrdersMQDto;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.service.GoodSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class MQConsumerGood {

    @Autowired
    private GoodSaleService goodSaleService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成订单成功
     * @param params
     */
    @KafkaListener(topics = {MQTopicConstant.SYNC_ORDER_TO_ES_TOPIC})
    public void listen(List<String> params) {
        log.info("=>kafka-listenOrders -> {}", params);
        if (params != null && !params.isEmpty()) {
            for (String data : params) {
                log.info("=>kafka-listenOrders1 -> {}", data);
                OrdersMQDto ordersMQDto = JSONObject.parseObject(data, OrdersMQDto.class);
                if (Objects.nonNull(ordersMQDto)) {
                    List<OrdersMQDto.Goods> goods = ordersMQDto.getGoods();
                    if (goods != null && goods.size() != 0) {
                        for (OrdersMQDto.Goods good : goods) {
                            goodSaleService.updateNum(good.getGoodNum(), good.getGoodSku());
                            redisTemplate.opsForValue().increment(GoodConstants.SKU_SALE + good.getGoodSku(), good.getGoodNum());
                        }
                    }
                }
            }
        }
    }
}
