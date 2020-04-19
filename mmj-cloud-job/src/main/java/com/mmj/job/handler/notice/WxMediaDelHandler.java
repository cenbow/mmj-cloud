package com.mmj.job.handler.notice;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.job.feign.NoticeFeignClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @description: 微信素材表
 * @auther: KK
 * @date: 2019/8/3
 */
@JobHandler(value = "WxMediaDelHandler")
@Component
@Slf4j
public class WxMediaDelHandler extends IJobHandler {
    @Autowired
    private NoticeFeignClient noticeFeignClient;

    /**
     * 每12小时跑一次
     *
     * @param s
     * @return
     * @throws Exception
     */
    @Override
    public ReturnT<String> execute(String s) throws Exception {
        try {
            ReturnData returnData = noticeFeignClient.wxMediaDel();
            if (returnData.getCode() == SecurityConstants.SUCCESS_CODE) {
                return SUCCESS;
            } else {
                return FAIL;
            }
        } catch (Exception e) {
            log.error("微信素材表错误 {}", e.getMessage());
            return FAIL;
        }
    }
}
