package com.mmj.active.common.feigin;


import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.vo.UserOrderVo;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderGoodFallbackFactory implements FallbackFactory<OrderGoodFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(OrderGoodFallbackFactory.class);

    @Override
    public OrderGoodFeignClient create(Throwable cause) {
        logger.info("OrderGoodFallbackFactory error message is {}", cause.getMessage());
        return new OrderGoodFeignClient() {
            @Override
            public ReturnData<List<OrderInfo>> getUserAllOrderNos(UserOrderVo userOrderVo) {
                throw new BusinessException("调用订单获取订单信息报错," + cause.getMessage(), 500);
            }
        };
    }
}
