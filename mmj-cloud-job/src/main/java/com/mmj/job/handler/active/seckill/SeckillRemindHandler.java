package com.mmj.job.handler.active.seckill;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.ActiveFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 秒杀-秒杀活动开启提醒：每小时 执行一次
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "SeckillRemindHandler")
@Component
@Slf4j
public class SeckillRemindHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 秒杀-秒杀活动开启提醒：每小时 执行一次
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.seckillRemind();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("秒杀活动开启提醒失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("秒杀活动开启提醒错误 {}", e.toString());
            XxlJobLogger.log("秒杀活动开启提醒错误 {}", e.toString());
            return FAIL;
        }
    }
}
