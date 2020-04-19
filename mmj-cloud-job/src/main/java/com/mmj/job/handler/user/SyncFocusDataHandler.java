package com.mmj.job.handler.user;

import com.alibaba.fastjson.JSON;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.UserFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 流量池-同步前一天的关注数据
 * 每天3点同步，同时执行7个定时任务
 * module
 * 1 2 3 4 5 6 7
 * type
 * 1
 * @auther: KK
 * @date: 2019/6/19
 */
@JobHandler(value = "SyncFocusDataHandler")
@Component
@Slf4j
public class SyncFocusDataHandler extends IJobHandler {
    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * 每天3点同步，同时执行7个定时任务
     * module
     * 1 2 3 4 5 6 7
     * type
     * 1
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        final int[] module = {1, 2, 3, 4, 5, 6, 7};
        final int type = 1;
        for (int i = 0; i < module.length; i++) {
            try {
                ReturnData returnData = userFeignClient.syncFocusData(module[i], type);
                if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                } else {
                    XxlJobLogger.log("流量池-同步前一天的关注数据失败 module:{},type:{},return:{}", module[i], type, JSON.toJSONString(returnData));
                }
            } catch (Exception e) {
                log.error("流量池-同步前一天的关注数据错误 module:{},type:{},error:{}", module[i], type, e.toString());
                XxlJobLogger.log("流量池-同步前一天的关注数据错误 module:{},type:{},error:{}", module[i], type, e.toString());
            }
        }
        return SUCCESS;
    }
}
