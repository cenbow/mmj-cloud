package com.mmj.good.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.good.feigin.dto.DecrGoodNum;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {

    /**
     * 扣减库存
     *
     * @param decrGoodNum
     * @return
     */
    @RequestMapping(value = "/orderInfo/decr", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> decr(@RequestBody DecrGoodNum decrGoodNum);
}
