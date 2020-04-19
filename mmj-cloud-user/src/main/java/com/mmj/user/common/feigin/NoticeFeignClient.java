package com.mmj.user.common.feigin;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;

@FeignClient(name = "mmj-cloud-notice", fallbackFactory = NoticeFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface NoticeFeignClient {
    
    @RequestMapping(value = "/baseDict/queryByDictType", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<BaseDict>> queryByDictType(@RequestParam("dictType")String dictType);

    @RequestMapping(value = "/baseDict/queryByDictTypeAndCode", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<BaseDict> queryByDictTypeAndCode(@RequestParam("dictType")String dictType, @RequestParam("dictCode")String dictCode);

    @RequestMapping(value = "/baseDict/queryGlobalConfigByDictCode", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<BaseDict> queryGlobalConfigByDictCode(@RequestParam("dictCode")String dictCode);
    
    @RequestMapping(value="/baseDict/saveBaseDict", method=RequestMethod.POST)
    @ResponseBody
	public ReturnData<Integer> saveBaseDict(@RequestBody BaseDict entity);
    
    @RequestMapping(value = "/wxmsg/sendSms", method = RequestMethod.POST)
    ReturnData<Object> sendSms(@RequestBody String params);

    @RequestMapping(value = "/wxmsg/sendCustom", method = RequestMethod.POST)
    ReturnData<JSONObject> sendCustom(@RequestBody String msg);
}
