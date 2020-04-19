package com.mmj.job.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @description: notice服务
 * @auther: KK
 * @date: 2019/8/3
 */
@FeignClient(name = "mmj-cloud-notice", fallbackFactory = NoticeFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface NoticeFeignClient {
    /**
     * 删除大于三天的临时素材 每12小时跑一次
     *
     * @return
     */
    @RequestMapping(value = "/wxMedia/del", method = RequestMethod.POST)
    @ResponseBody
    ReturnData wxMediaDel();

    /**
     * 删除七天以前的formid 每12小时跑一次
     *
     * @return
     */
    @RequestMapping(value = "/wxForm/del", method = RequestMethod.POST)
    @ResponseBody
    ReturnData wxFormDel();


    @RequestMapping(value = "/async/sendSMS", method = RequestMethod.POST)
    @ResponseBody
    ReturnData sendSMS();

    /**
     * 延迟队列数据修复 一分钟执行一次
     *
     * @return
     */
    @RequestMapping(value = "/async/wxDelayTask/repair", method = RequestMethod.POST)
    @ResponseBody
    ReturnData wxDelayTaskRepair();

}
