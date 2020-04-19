package com.mmj.job.feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;

/**
 * @description: 用户模块服务
 * @auther: KK
 * @date: 2019/8/3
 */
@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    /**
     * 被分享人确定收货10天 - 发送零钱(定时任务)
     *
     * @return
     */
    @RequestMapping(value = "/recommend/userShard/userShardSendMoney", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> userShardSendMoney();

    @RequestMapping(value = "/async/member/config/updateMemberActivityStartDate", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> updateMemberActivityStartDate();

    /**
     * JOB-同时前一天的关注数据,每天3点同步，同时执行7个定时任务
     *
     * @param module
     * @param type
     * @return
     */
    @RequestMapping(value = "/async/syncFocusData/{module}/{type}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Object> syncFocusData(@PathVariable("module") Integer module, @PathVariable("type") Integer type);
}
