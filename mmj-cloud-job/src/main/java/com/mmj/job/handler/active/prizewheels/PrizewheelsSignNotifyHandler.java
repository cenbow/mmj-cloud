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
 * @auther: shenfuding
 * @date: 2019/9/3
 */
@JobHandler(value = "PrizewheelsSignNotifyHandler")
@Component
@Slf4j
public class PrizewheelsSignNotifyHandler extends IJobHandler {
	
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * (规则：每天上午10点钟执行)
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData<Object> returnData = activeFeignClient.sendSignNoticeForPrizewheelsUser();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("转盘活动-->给未签到的用户提醒签到发生错误：", e.getMessage());
            return FAIL;
        }
    }
}
