package com.mmj.active.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = WxConfigFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface WxConfigFeignClient {
	
	@ResponseBody
	@RequestMapping(value="/wx/config/queryByAppId/{appId}", method=RequestMethod.POST)
	ReturnData<WxConfig> queryByAppId(@PathVariable(value="appId") String appId);
	
	@ResponseBody
	@RequestMapping(value = "/wx/config/queryByWxNo/{wxNo}", method = RequestMethod.POST)
	ReturnData<WxConfig> queryByWxNo(@PathVariable(value = "wxNo")String wxNo);
	
	@ResponseBody
	@RequestMapping(value="/wx/config/queryByType/{type}", method=RequestMethod.POST)
    public ReturnData<List<WxConfig>> queryByAppType(@PathVariable(value = "type") String type);

}
