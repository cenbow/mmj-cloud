package com.mmj.job.handler.active.coupon;

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
 * @description: 更新会员日优惠券发放数量
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "UpdateMemberDaySendNumHandler")
@Component
@Slf4j
public class UpdateMemberDaySendNumHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 每周三凌晨0点更新
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData<Boolean> returnData = activeFeignClient.updateMemberDaySendTotalCount();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE && returnData.getData()) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("更新会员日优惠券发放数量失败 error:{}", returnData.getDesc());
                return FAIL;
            }
        } catch (Exception e) {
            log.error("更新会员日优惠券发放数量错误 {}", e.getMessage());
            XxlJobLogger.log("更新会员日优惠券发放数量错误 error:{}", e.getMessage());
            return FAIL;
        }
    }
}
