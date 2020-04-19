package com.mmj.job.handler.active.grouplottery;

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
 * @description: 抽奖开奖
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "AutoDrawLotteryJobHandler")
@Component
@Slf4j
public class AutoDrawLotteryHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * (一分钟调用一次/有效期30秒/当前任务超时后后续任务往后推)
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.autoDrawLottery();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("抽奖开奖错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
