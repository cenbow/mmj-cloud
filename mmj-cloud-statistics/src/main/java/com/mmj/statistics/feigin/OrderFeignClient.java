package com.mmj.statistics.feigin;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserOrderStatistics;
import com.mmj.common.model.UserOrderStatisticsParam;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {
	
	@RequestMapping(value = "/async/usersOrdersDataForChannel", method = RequestMethod.POST)
    ReturnData<List<UserOrderStatistics>> getUsersOrdersDataForChannel(@RequestBody UserOrderStatisticsParam param);

}
