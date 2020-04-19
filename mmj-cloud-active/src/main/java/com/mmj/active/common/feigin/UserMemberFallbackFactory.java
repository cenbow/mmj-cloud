package com.mmj.active.common.feigin;

import com.mmj.active.common.model.UserMember;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserMemberFallbackFactory implements FallbackFactory<UserMemberFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(UserMemberFallbackFactory.class);

    @Override
    public UserMemberFeignClient create(Throwable cause) {
        return new UserMemberFeignClient() {
            @Override
            public ReturnData<UserMember> queryUserMemberInfoByUserId(Long userId) {
                throw new BusinessException("获取会员信息报错: " + cause.getMessage(), 500);
            }
        };
    }
}
