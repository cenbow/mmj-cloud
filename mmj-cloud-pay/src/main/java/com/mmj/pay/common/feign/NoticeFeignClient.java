package com.mmj.pay.common.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
@FeignClient(name = "mmj-cloud-notice", fallbackFactory = NoticeFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface NoticeFeignClient {

    /**
     * 发送客服消息(包含小程序和公众号)
     * @param msg
     * @return
     */
    @RequestMapping(value="/wxmsg/sendCustom",method=RequestMethod.POST)
    @ResponseBody
    ReturnData<JSONObject> sendCustom(@RequestBody String msg);
}
