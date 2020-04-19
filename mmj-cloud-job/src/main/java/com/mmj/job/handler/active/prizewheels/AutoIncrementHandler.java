package com.mmj.job.handler.active.prizewheels;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.ActiveFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 转盘活动 - 给最近活跃用户增加买买币
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "AutoIncrementHandler")
@Component
@Slf4j
public class AutoIncrementHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * (规则：1分钟执行一次)
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData<Object> returnData = activeFeignClient.autoIncrement();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error(" 转盘活动 给最近活跃用户增加买买币 {}", e.getMessage());
            return FAIL;
        }
    }
}
