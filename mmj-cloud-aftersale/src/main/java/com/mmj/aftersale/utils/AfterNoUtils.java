package com.mmj.aftersale.utils;

import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.OrderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AfterNoUtils {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 根据订单号获取取模值
     *
     * @param no
     * @return
     */
    public long getDelivery(String no) {
        return OrderUtils.isNewOrOld(no) ? OrderUtils.getNewDelivery(no) : getOldDelivery(no);
    }

    /**
     * 取模后结果放入变量中
     *
     * @param orderNo
     */
    public void shardingKey(String orderNo) {
        long mold = getDelivery(orderNo);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, mold);
        log.info("=> 通过售後单号取模 orderNo:{}, shardingKey:{}", orderNo, mold);
    }

    /**
     * 获取老订单取模值
     *
     * @param no
     * @return
     */
    public long getOldDelivery(String no) {
        if(no.indexOf("-")!=-1){
            no = no.split("-")[0];
        }
        String userId = (String) redisTemplate.opsForHash().get("ORDER_USERID_MAPPER", no);
        return Long.parseLong(userId.substring(userId.length() - 2, userId.length()));
    }
}
