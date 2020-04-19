package com.mmj.job.feign;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @description: Notice模块熔断
 * @auther: KK
 * @date: 2019/8/3
 */
@Component
@Slf4j
public class NoticeFallbackFactory implements FallbackFactory<NoticeFeignClient> {
    private final ReturnData returnData = new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");

    @Override
    public NoticeFeignClient create(Throwable throwable) {
        return new NoticeFeignClient() {
            @Override
            public ReturnData wxMediaDel() {
                return returnData;
            }

            @Override
            public ReturnData wxFormDel() {
                return returnData;
            }

            @Override
            public ReturnData sendSMS() {
                return returnData;
            }

            @Override
            public ReturnData wxDelayTaskRepair() {
                return returnData;
            }
        };
    }
}
