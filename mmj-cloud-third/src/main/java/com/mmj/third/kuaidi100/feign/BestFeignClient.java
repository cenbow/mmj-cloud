package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.BestRequest;
import com.mmj.third.kuaidi100.model.BestResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 快递100调用客户端
 * @auther: KK
 * @date: 2019/7/11
 */
@FeignClient(name = "best", url = "http://edi-q9.ns.800best.com", fallbackFactory = BestFallbackFactory.class)
public interface BestFeignClient {
    /**
     * 查询快递信息
     *
     * @param serviceType
     * @param partnerID
     * @param bizData
     * @param sign
     * @param bestRequest
     * @return
     */
    @RequestMapping(value = "/kd/api/process", method = RequestMethod.POST)
    String query(@RequestParam("serviceType") String serviceType,
                       @RequestParam("partnerID") String partnerID,
                       @RequestParam("bizData") String bizData,
                       @RequestParam("sign") String sign,
                       @RequestBody BestRequest bestRequest);

}
