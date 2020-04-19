package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.WxpayRefund;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mmj-cloud-pay", fallbackFactory = PayFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface PayFeignClient {

    /**
     * 调用微信退款
     *
     * @param wxpayRefund
     * @return
     */
    @RequestMapping(value = "/wxpayRefund/refund", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxpayRefund> refund(@RequestBody WxpayRefund wxpayRefund);
}
