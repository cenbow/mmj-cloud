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
 * @description: 每天凌晨定时修改会员配置：活动开始日期(活动如果还剩0天，则将活动开始日期调整为当天)、会员人数(+100万)
 * @auther: shenfuding
 * @date: 2019/8/28
 */
@JobHandler(value = "MemberConfigHandler")
@Component
@Slf4j
public class MemberConfigHandler extends IJobHandler {
	
    @Autowired
    private UserFeignClient userFeignClient;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData<Object> returnData = userFeignClient.updateMemberActivityStartDate();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("修改会员配置发生错误：}", e);
            return FAIL;
        }
    }
}
