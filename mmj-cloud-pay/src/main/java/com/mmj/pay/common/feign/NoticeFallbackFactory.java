package com.mmj.pay.common.feign;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.WxException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NoticeFallbackFactory implements FallbackFactory<NoticeFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(NoticeFallbackFactory.class);

    @Override
    public NoticeFeignClient create(Throwable cause) {
        logger.info("NoticeFallbackFactory error message is {}", cause.getMessage());
        return new NoticeFeignClient(){
            @Override
            public ReturnData<JSONObject> sendCustom(String msg) {
                throw new WxException("发送客服消息错误," + cause.getMessage(), 500);
            }
        };
    }
}
