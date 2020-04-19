package com.mmj.active.common.feigin;


import com.mmj.active.common.model.OrderInfo;
import com.mmj.active.common.model.vo.UserOrderVo;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderGoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderGoodFeignClient {

    /**
     * 根据userid获取这个用户所有的订单
     * @param userOrderVo
     * @return
     */
    @RequestMapping(value = "/orderInfo/get/allOrderNos", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<OrderInfo>> getUserAllOrderNos(@RequestBody UserOrderVo userOrderVo);

}
