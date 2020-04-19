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
 * @description: 过期订单 5分钟
 * @auther: KK
 * @date: 2019/8/3
 */
@JobHandler(value = "DatedOrderHandler")
@Component
@Slf4j
public class DatedOrderHandler extends IJobHandler {
    @Autowired
    private OrderFeignClient orderFeignClient;

    /**
     * 5分钟
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = orderFeignClient.datedOrder();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("过期订单错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
