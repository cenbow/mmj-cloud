package com.mmj.notice.feigin;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.notice.model.WxpayRedpack;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PayFallBackFactory  implements FallbackFactory<PayFeignClient> {



    @Override
    public PayFeignClient create(Throwable cause) {
        log.info("PayFallBackFactory error message is {}", cause.getMessage());
        return new PayFeignClient(){
            @Override
            public ReturnData<WxpayRedpack> sendRedpack(WxpayRedpack wxpayRedpack) {
                throw new BusinessException("发送红包失败," + cause.getMessage(), 500);
            }
        };
    }
}
