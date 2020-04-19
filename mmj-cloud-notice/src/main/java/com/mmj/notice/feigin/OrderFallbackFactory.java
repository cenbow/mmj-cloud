package com.mmj.notice.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.notice.common.dto.OrderInfo;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {
            @Override
            public OrderInfo getOrderInfo(String orderNo) {
                throw new BusinessException("查询订单接口异常," + cause.getMessage(), 500);
            }
        };
    }

}
