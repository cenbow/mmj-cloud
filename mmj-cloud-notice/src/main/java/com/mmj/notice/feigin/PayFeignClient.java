package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxpayRedpack;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "mmj-cloud-pay", fallbackFactory = PayFallBackFactory.class, configuration = FeignRequestInterceptor.class)
public interface PayFeignClient {

    /**
     * 发送红包
     * @param wxpayRedpack
     * @return
     */
    @RequestMapping(value = "wxpayRedpack/sendRedpack",method = RequestMethod.POST)
    ReturnData<WxpayRedpack> sendRedpack(WxpayRedpack wxpayRedpack);
}
