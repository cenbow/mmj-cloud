package com.mmj.pay.common.feign;


import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.pay.common.model.dto.GoodInfo;
import com.mmj.pay.common.model.dto.GoodSale;


@FeignClient(name = "mmj-cloud-good", fallbackFactory = GoodFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface GoodFeignClient {

    @RequestMapping(value = "/goodSale/queryList", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData<Object> queryList(@RequestBody GoodSale goodSale);

    @RequestMapping(value = "/goodInfo/getById/{id}", method = RequestMethod.POST)
    @ResponseBody
    public GoodInfo getById(@PathVariable("id") Integer id);
    
}
