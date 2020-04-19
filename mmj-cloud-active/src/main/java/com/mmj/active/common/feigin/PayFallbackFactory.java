package com.mmj.active.common.feigin;

import com.mmj.active.callCharge.model.vo.WxpayOrderEx;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PayFallbackFactory implements FallbackFactory<PayFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(PayFallbackFactory.class);

    @Override
    public PayFeignClient create(Throwable cause) {
        logger.info("PayFallbackFactory error message is {}", cause.getMessage());
        return new PayFeignClient() {
            @Override
            public ReturnData<Map<String, String>> getPayInfo(WxpayOrderEx wxpayOrderEx) {
                throw new BusinessException("调用支付信息接口报错," + cause.getMessage(), 500);
            }
        };
    }

}
