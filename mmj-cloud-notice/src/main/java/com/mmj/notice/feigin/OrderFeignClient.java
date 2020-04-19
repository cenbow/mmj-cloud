package com.mmj.notice.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.notice.common.dto.OrderInfo;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {


    @RequestMapping(value = "/async/order/{orderNo}", method = RequestMethod.POST)
    OrderInfo getOrderInfo(@PathVariable("orderNo") String orderNo);

}
