package com.mmj.third.kuaidi100.feign;

import com.mmj.third.kuaidi100.model.PollQueryResponse;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: 快递100调用客户端
 * @auther: KK
 * @date: 2019/7/11
 */
@FeignClient(name = "kuaidi100-poll", url = "https://poll.kuaidi100.com", fallbackFactory = Kuaidi100PollFallbackFactory.class)
public interface Kuaidi100PollFeignClient {
    /**
     * 查询快递
     *
     * @param param
     * @param customer
     * @param sign
     * @return
     */
    @RequestMapping(value = "/poll/query.do", method = RequestMethod.POST, headers = {"Content-Type=application/x-www-form-urlencoded"})
    @ResponseBody
    String query(@RequestParam("param") String param, @RequestParam("customer") String customer, @RequestParam("sign") String sign);

}
