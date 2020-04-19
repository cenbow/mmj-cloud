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
 * @description: 库存同步接口
 * @auther: KK
 * @date: 2019/10/15
 */
@JobHandler(value = "SynStock1Handler")
@Component
@Slf4j
public class SynStock1Handler extends IJobHandler {
    @Autowired
    private GoodFeignClient goodFeignClient;

    /**
     * 库存同步接口 0/12
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = goodFeignClient.synStock1();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("库存同步接口失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("库存同步接口错误 {}", e.toString());
            XxlJobLogger.log("库存同步接口错误 {}", e.toString());
            return FAIL;
        }
    }
}
