package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.WxpayRefund;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PayFallbackFactory implements FallbackFactory<PayFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(PayFallbackFactory.class);


    @Override
    public PayFeignClient create(Throwable cause) {
        return new PayFeignClient() {
            @Override
            public ReturnData<WxpayRefund> refund(WxpayRefund wxpayRefund) {
                throw new BusinessException("调用支付接口失败，"+cause.getMessage(),500);
            }
        };
    }
}
