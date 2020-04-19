package com.mmj.good.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.DecrGoodNum;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(OrderFallbackFactory.class);

    @Override
    public OrderFeignClient create(Throwable cause) {
        logger.info("Good-OrderFallbackFactory error message is {}", cause.getMessage());
        return new OrderFeignClient() {
            @Override
            public ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum) {
                throw new BusinessException("调用查询订单接口报错," + cause.getMessage(), 500);
            }

        };
    }
}
