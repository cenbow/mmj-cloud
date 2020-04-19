package com.mmj.job.feign;

import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;

import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * @description: 用户模块熔断
 * @auther: KK
 * @date: 2019/8/3
 */
@Component
@Slf4j
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {
    private final ReturnData returnData = new ReturnData(SecurityConstants.EXCEPTION_CODE, "fail");

    @Override
    public UserFeignClient create(Throwable throwable) {
        return new UserFeignClient() {
            @Override
            public ReturnData<Object> userShardSendMoney() {
                return returnData;
            }

			@Override
			public ReturnData<Object> updateMemberActivityStartDate() {
				return returnData;
			}

            @Override
            public ReturnData<Object> syncFocusData(Integer module, Integer type) {
                return returnData;
            }
        };
    }
}
