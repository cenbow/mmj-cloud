package com.mmj.oauth.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;

@FeignClient(name = "mmj-cloud-notice", fallbackFactory = NoticeFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface NoticeFeignClient {

    @RequestMapping(value = "/wxmsg/sendSms", method = RequestMethod.POST)
    ReturnData<Object> sendSms(@RequestBody String params);

}
