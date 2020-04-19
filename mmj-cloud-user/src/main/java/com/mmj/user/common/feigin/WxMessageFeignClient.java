package com.mmj.user.common.feigin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;

@FeignClient(name = "mmj-cloud-notice", fallbackFactory = WxMessageFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface WxMessageFeignClient {

    @RequestMapping(value="/wxmsg/sendTemplateM",method=RequestMethod.POST)
    @ResponseBody
    ReturnData<JSONObject> sendTemplateM(@RequestBody String msg);
}
