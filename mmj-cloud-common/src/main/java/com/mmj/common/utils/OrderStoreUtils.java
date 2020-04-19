package com.mmj.common.utils;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.order.OrderStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description: 下单订单缓存
 * @auther: KK
 * @date: 2019/9/3
 */
@Component
@Slf4j
public class OrderStoreUtils {
    private final static String ORDER_STORE_KEY = "ORDER:STORE:";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 写入订单缓存
     *
     * @param orderStore
     */

    public void write(OrderStore orderStore) {
        redisTemplate.opsForValue().set(ORDER_STORE_KEY + orderStore.getOrderNo(), JSON.toJSONString(orderStore), 30, TimeUnit.DAYS);
    }

    /**
     * 读出订单缓存
     *
     * @param orderNo
     * @return
     */
    public OrderStore reader(String orderNo) {
        String result = redisTemplate.opsForValue().get(ORDER_STORE_KEY + orderNo);
        if (StringUtils.isEmpty(result))
            return null;
        return JSON.parseObject(result, OrderStore.class);
    }
}
