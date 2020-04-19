package com.mmj.job.handler.notice;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.NoticeFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 延迟队列数据修复 一分钟执行一次
 */
@JobHandler(value = "WxDelayRepairHandler")
@Component
@Slf4j
public class WxDelayRepairHandler extends IJobHandler {

    @Autowired
    private NoticeFeignClient noticeFeignClient;

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
            ReturnData returnData = noticeFeignClient.wxDelayTaskRepair();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("延迟队列数据修复失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("延迟队列数据修复错误 {}", e.toString());
            XxlJobLogger.log("延迟队列数据修复错误 {}", e.toString());
            return FAIL;
        }
    }
}
