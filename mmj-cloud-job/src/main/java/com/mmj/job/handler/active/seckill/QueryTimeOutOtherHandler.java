package com.mmj.job.handler.active.seckill;

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
 * @description: 清理过期活动
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "QueryTimeOutOtherHandler")
@Component
@Slf4j
public class QueryTimeOutOtherHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 清理过期活动，每小时执行一次
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.queryTimeOutOther();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("清理过期活动错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
