package com.mmj.job.handler.good;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.GoodFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 库存-清理数据 每个月1号0点执行
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "CleanHandler")
@Component
@Slf4j
public class CleanHandler extends IJobHandler {
    @Autowired
    private GoodFeignClient goodFeignClient;

    /**
     * 库存-清理数据 每个月1号0点执行
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = goodFeignClient.clean();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("库存-清理数据失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("库存-清理数据错误 {}", e.toString());
            XxlJobLogger.log("库存-清理数据错误 {}", e.toString());
            return FAIL;
        }
    }
}
