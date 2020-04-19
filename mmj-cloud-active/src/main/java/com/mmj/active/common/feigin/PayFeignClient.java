package com.mmj.active.common.feigin;

import com.mmj.active.callCharge.model.vo.WxpayOrderEx;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
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
     * @param wxpayOrderEx
     * @return
     */
    @RequestMapping(value = "/wxpayOrder/getPayInfo", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Map<String, String>> getPayInfo(@RequestBody WxpayOrderEx wxpayOrderEx);

}
