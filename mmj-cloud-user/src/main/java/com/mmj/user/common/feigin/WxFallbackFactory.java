package com.mmj.user.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.dto.WxMediaDto;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;


@Component
public class WxFallbackFactory implements FallbackFactory<WxFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(WxFallbackFactory.class);

    @Override
    public WxFeignClient create(Throwable cause) {
        logger.info("User-WxFallbackFactory error message is {}", cause.getMessage());
        return new WxFeignClient() {
            @Override
            public ReturnData<WxMediaDto> query(@RequestBody WxMediaDto wxMedia) {
                throw new BusinessException("调用微信素材查询接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<JSONObject> sendCustom(@RequestBody String msg) {
                throw new BusinessException("调用发送客服消息(包含小程序和公众号)接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WxMediaDto> createQrcode(@RequestBody String msg) {
                throw new BusinessException("创建小程序里面的公众号二维码素材," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WxMediaDto> upload(@RequestBody WxMediaDto wxMedia) {
                throw new BusinessException("微信素材上传," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData doTag(@RequestBody String tagParams) {
                throw new BusinessException("给用户打标签," + cause.getMessage(), 500);
            }
        };
    }
}
