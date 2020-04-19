package com.mmj.statistics.feigin;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;

import feign.hystrix.FallbackFactory;

@Component
public class OrderFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        return new OrderFeignClient() {

			@Override
			public ReturnData<List<UserOrderStatistics>> getUsersOrdersDataForChannel(
					UserOrderStatisticsParam param) {
				throw new BusinessException("调用查询渠道用户订单数据接口报错," + cause.getMessage(), 500);
			}
        };
    }

}
