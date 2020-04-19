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
 * @description: 同步聚水潭-组合
 * @auther: KK
 * @date: 2019/10/15
 */
@JobHandler(value = "SynGoodsStockZhHandler")
@Component
@Slf4j
public class SynGoodsStockZhHandler extends IJobHandler {
    @Autowired
    private GoodFeignClient goodFeignClient;

    /**
     * 同步聚水潭-组合 5/11/17/23
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = goodFeignClient.synGoodsStockZh();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("同步聚水潭-组合失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("同步聚水潭-组合错误 {}", e.toString());
            XxlJobLogger.log("同步聚水潭-组合错误 {}", e.toString());
            return FAIL;
        }
    }
}
