package com.mmj.active.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxpayTransfers;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "mmj-cloud-pay", fallbackFactory = WxpayTransfersFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface WxpayTransfersFeignClient {

    @RequestMapping(value = "/wxpayTransfers/transfers", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxpayTransfers> transfers(@RequestBody WxpayTransfers wxpayTransfers);
}
