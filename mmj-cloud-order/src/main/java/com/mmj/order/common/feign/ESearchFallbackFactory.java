package com.mmj.order.common.feign;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrderSearchConditionDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ESearchFallbackFactory implements FallbackFactory<ESearchFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(ESearchFallbackFactory.class);

    @Override
    public ESearchFeignClient create(Throwable cause) {
        logger.info("FallbackFactory error message is {},case:{}", cause.getMessage(), cause);
        return new ESearchFeignClient() {
            @Override
            public ReturnData<Page<OrderSearchResultDto>> getOrderList(OrderSearchConditionDto orderSearchConditionDto) {
                throw new BusinessException("BOSS后台查询订单列表失败!," + cause.getMessage(), 500);
            }
        };
    }
}
