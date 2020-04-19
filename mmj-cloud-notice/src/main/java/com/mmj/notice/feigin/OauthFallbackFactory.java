package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.OfficialAccountUser;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OauthFallbackFactory implements FallbackFactory<OauthFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(OauthFallbackFactory.class);

    @Override
    public OauthFeignClient create(Throwable cause) {
        logger.info("OauthFallbackFactory error message is {}", cause.getMessage());
        return new OauthFeignClient(){
            @Override
            public ReturnData savePublic(OfficialAccountUser officialAccountUser) {
                throw new BusinessException("公众号保存用户信息失败" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData unsubUser(String openId) {
                throw new BusinessException("公众号取消关注用户信息失败" + cause.getMessage(), 500);
            }
        };
    }
}
