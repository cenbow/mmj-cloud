package com.mmj.job.handler.active.search;

import com.alibaba.fastjson.JSON;
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
 * @description: 搜索-热销前十商品更新：每周3中午12点
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "ResetHotSellGoodsHandler")
@Component
@Slf4j
public class ResetHotSellGoodsHandler extends IJobHandler {
    @Autowired
    private ActiveFeignClient activeFeignClient;

    /**
     * 搜索-热销前十商品更新：每周3中午12点
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = activeFeignClient.resetHotSellGoods();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                XxlJobLogger.log("热销前十商品更新失败 {}", JSON.toJSONString(returnData));
                return FAIL;
            }
        } catch (Exception e) {
            log.error("热销前十商品更新错误 {}", e.toString());
            XxlJobLogger.log("热销前十商品更新错误 {}", e.toString());
            return FAIL;
        }
    }
}
