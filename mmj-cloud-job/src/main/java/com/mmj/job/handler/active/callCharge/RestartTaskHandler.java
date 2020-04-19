package com.mmj.job.handler.active.callCharge;

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
 * @description: 话费商品-每天10点重置发放数量
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "RestartTaskHandler")
@Component
@Slf4j
public class RestartTaskHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 每天10点重置发放数量
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.restartTask();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("话费商品-每天10点重置发放数量 error:{}", returnData.getDesc());
                return FAIL;
            }
        } catch (Exception e) {
            log.error("话费商品-每天10点重置发放数量 {}", e.getMessage());
            XxlJobLogger.log("话费商品-每天10点重置发放数量 error:{}", e.getMessage());
            return FAIL;
        }
    }
}
