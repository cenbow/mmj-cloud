package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.active.common.constants.WxMedia;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.BaseDict;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "mmj-cloud-notice", fallbackFactory = NoticeFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface NoticeFeignClient {

    @RequestMapping(value = "/wxImage/createImage", method = RequestMethod.POST)
    ReturnData<String> createImage(@RequestBody String params);

    @RequestMapping(value = "/baseDict/queryGlobalConfigByDictCode", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<BaseDict> queryGlobalConfigByDictCode(@RequestParam("dictCode") String dictCode);

    @RequestMapping(value = "/wxMedia/upload", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<WxMedia> wxMediaUpload(@RequestBody WxMedia wxMedia);

    @RequestMapping(value = "/async/freeGoodsCompose", method = RequestMethod.POST)
    ReturnData<String> freeGoodsCompose(@RequestBody JSONObject params);
}
