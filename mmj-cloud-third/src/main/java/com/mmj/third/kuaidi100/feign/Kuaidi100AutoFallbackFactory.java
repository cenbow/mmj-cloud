package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.AutoResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description: 快递100
 * @auther: KK
 * @date: 2019/7/11
 */
@Component
@Slf4j
public class Kuaidi100AutoFallbackFactory implements FallbackFactory<Kuaidi100AutoFeignClient> {

    @Override
    public Kuaidi100AutoFeignClient create(Throwable throwable) {
        log.info("Kuaidi100AutoFallbackFactory error message is {}", throwable.getMessage());
        return new Kuaidi100AutoFeignClient() {
            @Override
            public String auto(String num, String key) {
                return null;
            }
        };
    }
}
