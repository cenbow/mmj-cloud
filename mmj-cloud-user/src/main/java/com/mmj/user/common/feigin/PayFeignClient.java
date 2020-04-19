package com.mmj.user.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.WxpayRedpack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@FeignClient(name = "mmj-cloud-pay", fallbackFactory = PayFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface PayFeignClient {

    /**
     * 获取订单支付接口
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/wxpayOrder/getPayInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Map<String, String>> getPayInfo(@RequestBody JSONObject params);

    /**
     * 发红包
     *
     * @param wxpayRedpack
     * @return
     */

    @RequestMapping(value = "/wxpayRedpack/sendRedpack", method = RequestMethod.POST)
    ReturnData<WxpayRedpack> sendRedpack(@RequestBody WxpayRedpack wxpayRedpack);
}
