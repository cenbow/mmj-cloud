package com.mmj.statistics.feigin;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ChannelUserParam;
import com.mmj.common.model.ChannelUserVO;
import com.mmj.common.model.ReturnData;

import feign.hystrix.FallbackFactory;

@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        logger.info("UserFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {

			@Override
			public ReturnData<List<ChannelUserVO>> getChannelUsers(
					ChannelUserParam param) {
				throw new BusinessException("获取用户当前端信息接口报错：" + cause.getMessage(), 500);
			}
        };
    }

}
