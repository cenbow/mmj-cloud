package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.notice.model.OfficialAccountUser;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class WxMessageFallbackFactory implements FallbackFactory<WxMessageFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(WxMessageFallbackFactory.class);

    @Override
    public WxMessageFeignClient create(Throwable cause) {
        logger.info("WxMessageFallbackFactory error message is {}", cause.getMessage());
        return new WxMessageFeignClient() {
            @Override
            public ReturnData<WxConfig> queryByWxNo(String wxNo) {
                throw new BusinessException("根据wxNo查询微信app配置," + cause.getMessage(), 500);
            }
            @Override
            public ReturnData<WxConfig> queryByAppId(String appId) {
                throw new BusinessException("根据appid查询微信app配置," + cause.getMessage(), 500);
            }
            @Override
            public ReturnData<Object> queryMemberConfig() {
                throw new BusinessException("查询会员配置异常" + cause.getMessage(), 500);
            }
            @Override
            public ReturnData<Object> selectByRecommendId(String recommendId) {
                throw new BusinessException("根据id查询推荐详情 - 小程序失败" + cause.getMessage(), 500);
            }
        };
    }
}
