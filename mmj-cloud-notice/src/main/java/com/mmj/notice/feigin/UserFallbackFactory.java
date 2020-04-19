package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    @Override
    public UserFeignClient create(Throwable cause) {
        log.info("UserFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public String getUserOpenId(JSONObject object) {
                throw new BusinessException("调用用户接口报错," + cause.getMessage(), 500);
            }
        };
    }

}
