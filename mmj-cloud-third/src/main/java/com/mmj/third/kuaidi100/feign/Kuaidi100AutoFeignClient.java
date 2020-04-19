package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.AutoResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @description: 快递100调用客户端
 * @auther: KK
 * @date: 2019/7/11
 */
@FeignClient(name = "kuaidi100-auto", url = "http://www.kuaidi100.com", fallbackFactory = Kuaidi100AutoFallbackFactory.class)
public interface Kuaidi100AutoFeignClient {

    /**
     * 查询所属快递
     *
     * @param num 物流单号
     * @param key
     * @return
     */
    @RequestMapping(value = "/autonumber/auto", method = RequestMethod.GET)
//    @ResponseBody
    String auto(@RequestParam("num") String num, @RequestParam("key") String key);
}
