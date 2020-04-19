package com.mmj.job.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 订单模块服务
 * @auther: KK
 * @date: 2019/8/3
 */
@FeignClient(name = "mmj-cloud-order", fallbackFactory = OrderFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface OrderFeignClient {

    /**
     * 同步新老用户 每天一次12：00
     *
     * @return
     */
    @RequestMapping(value = "/orderInfo/sync/newUserFlag", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> checkNewUser();

    /**
     * 过期订单 5分钟
     *
     * @return
     */
    @RequestMapping(value = "/orderInfo/timeOut/order", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> datedOrder();

    /**
     * 同步聚水潭库存 每天一次12：00
     *
     * @return
     */
    @RequestMapping(value = "/orderInfo/jst/goodNum", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> queryLogistics();
}
