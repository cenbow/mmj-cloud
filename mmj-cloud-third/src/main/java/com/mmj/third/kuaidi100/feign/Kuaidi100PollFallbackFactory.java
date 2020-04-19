package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.AutoResponse;
import com.mmj.third.kuaidi100.model.PollQueryResponse;
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
public class Kuaidi100PollFallbackFactory implements FallbackFactory<Kuaidi100PollFeignClient> {

    @Override
    public Kuaidi100PollFeignClient create(Throwable throwable) {
        log.info("Kuaidi100PollFallbackFactory error message is {}", throwable.getMessage());
        return new Kuaidi100PollFeignClient() {
            @Override
            public String query(String param, String customer, String sign) {
                return null;
            }
        };
    }
}
