package com.mmj.order.common.feign;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.order.OrderSearchConditionDto;
import com.mmj.common.model.order.OrderSearchResultDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mmj-cloud-elasticsearch", fallbackFactory = ESearchFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ESearchFeignClient {

    @RequestMapping(value = "/orders/search", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Page<OrderSearchResultDto>> getOrderList(@RequestBody OrderSearchConditionDto orderSearchConditionDto);
}
