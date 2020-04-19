package com.mmj.job.handler.active.threeSale;

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
 * @description: 十元三件红包裂变 - 定时任务,2小时内,助力好友未支付成功, 设置为已失效
 * @auther: KK
 * @date: 2019/8/3
 */
@JobHandler(value = "UpdateInvalidHandler")
@Component
@Slf4j
public class UpdateInvalidHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 一分钟执行一次
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.updateInvalid();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("十元三件红包裂变,设置为已失效错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
