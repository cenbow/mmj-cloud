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
 * @description: 修改当前期次，每天23：59：59 执行一次
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "ChangePriodHandler")
@Component
@Slf4j
public class ChangePriodHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 每天23：59：59 执行一次
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.changePriod();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("修改当前期次错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
