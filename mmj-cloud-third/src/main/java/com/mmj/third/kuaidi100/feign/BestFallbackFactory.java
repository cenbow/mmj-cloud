package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.BestRequest;
import com.mmj.third.kuaidi100.model.BestResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 快递100
 * @auther: KK
 * @date: 2019/7/11
 */
@Component
@Slf4j
public class BestFallbackFactory implements FallbackFactory<BestFeignClient> {

    @Override
    public BestFeignClient create(Throwable throwable) {
        log.info("BestFallbackFactory error message is {}", throwable.getMessage());
        return new BestFeignClient() {
            @Override
            public String query(String serviceType, String partnerID, String bizData, String sign, BestRequest bestRequest) {
                return null;
            }
        };
    }
}
