package com.mmj.job.feign;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: 订单模块熔断
 * @auther: KK
 * @date: 2019/8/3
 */
@Component
@Slf4j
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {
    private final ReturnData returnData = new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");

    @Override
    public OrderFeignClient create(Throwable throwable) {
        return new OrderFeignClient() {
            @Override
            public ReturnData<Object> checkNewUser() {
                return returnData;
            }

            @Override
            public ReturnData<Object> datedOrder() {
                return returnData;
            }

            @Override
            public ReturnData<Object> queryLogistics() {
                return returnData;
            }
        };
    }
}
