package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    @RequestMapping(value = "/async/getUserOpenId", method = RequestMethod.POST)
    String getUserOpenId(@RequestBody JSONObject object);
}
