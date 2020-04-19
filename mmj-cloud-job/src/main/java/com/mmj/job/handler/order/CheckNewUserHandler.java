package com.mmj.job.handler.order;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.OrderFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 同步新老用户 每天一次12：00
 * @auther: KK
 * @date: 2019/8/3
 */
@JobHandler(value = "CheckNewUserHandler")
@Component
@Slf4j
public class CheckNewUserHandler extends IJobHandler {
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 每天一次12：00
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = orderFeignClient.checkNewUser();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("同步新老用户错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
