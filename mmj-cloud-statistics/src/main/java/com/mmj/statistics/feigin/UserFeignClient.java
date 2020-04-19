package com.mmj.statistics.feigin;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.common.model.ReturnData;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    @RequestMapping(value = "/async/channel/getChannelUsers", method = RequestMethod.POST)
    ReturnData<List<ChannelUserVO>> getChannelUsers(@RequestBody ChannelUserParam param);
}
