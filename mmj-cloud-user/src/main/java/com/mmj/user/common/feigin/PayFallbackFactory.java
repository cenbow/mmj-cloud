package com.mmj.user.common.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.WxpayRedpack;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Component
public class PayFallbackFactory implements FallbackFactory<PayFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(PayFallbackFactory.class);

    @Override
    public PayFeignClient create(Throwable cause) {
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new PayFeignClient() {
            @Override
            public ReturnData<Map<String, String>> getPayInfo(@RequestBody JSONObject params) {
                throw new BusinessException("调用支付信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WxpayRedpack> sendRedpack(WxpayRedpack wxpayRedpack) {
                throw new BusinessException("调用发红包接口报错," + cause.getMessage(), 500);
            }
        };
    }

}
