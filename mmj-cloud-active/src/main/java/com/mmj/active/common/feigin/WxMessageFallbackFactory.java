package com.mmj.active.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;

import feign.hystrix.FallbackFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class WxMessageFallbackFactory implements FallbackFactory<WxMessageFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(WxMessageFallbackFactory.class);
    @Override
    public WxMessageFeignClient create(Throwable throwable) {
        logger.info("WxMessageFallbackFactory error message is {}", throwable.getMessage());
        return new WxMessageFeignClient() {
            @Override
            public ReturnData<JSONObject> sendTemplateM(String msg) {
                throw new BusinessException("调用发送模板消息报错," + throwable.getMessage(), 500);
            }

			@Override
			public ReturnData<JSONObject> sendCustom(String msg) {
				throw new BusinessException("给用户发送公众号消息报错," + throwable.getMessage(), 500);
			}

            @Override
			public ReturnData<JSONObject> sendTemplate(@RequestBody String msg) {
                throw new BusinessException("给用户发送公众号模板消息出错," + throwable.getMessage(), 500);
            }
        };
    }
}
