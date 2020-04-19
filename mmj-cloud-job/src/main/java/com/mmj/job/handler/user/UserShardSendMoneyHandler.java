package com.mmj.job.handler.user;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.UserFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 被分享人确定收货10天 - 发送零钱(定时任务)
 * @auther: KK
 * @date: 2019/8/3
 */
@JobHandler(value = "UserShardSendMoneyHandler")
@Component
@Slf4j
public class UserShardSendMoneyHandler extends IJobHandler {
    @Autowired
    private UserFeignClient userFeignClient;

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
            ReturnData returnData = userFeignClient.userShardSendMoney();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("发送零钱错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
