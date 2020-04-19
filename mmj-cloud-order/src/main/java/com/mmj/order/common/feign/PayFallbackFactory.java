package com.mmj.order.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.order.common.model.WxpayRedpack;
import com.mmj.order.common.model.vo.CartOrderGoodsDetails;
import com.mmj.order.common.model.vo.WxpayOrderEx;
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
        logger.info("GoodFallbackFactory error message is {}", cause.getMessage());
        return new PayFeignClient() {
            @Override
            public ReturnData<Map<String, String>> getPayInfo(WxpayOrderEx wxpayOrderEx) {
                throw new BusinessException("调用支付信息接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<CartOrderGoodsDetails> calcFinalPrice(CartOrderGoodsDetails cogd) {
                throw new BusinessException("生单价格验证异常:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<WxpayRedpack> sendRedpack(WxpayRedpack wxpayRedpack) {
                throw new BusinessException("发送红包报错:" + cause.getMessage(), 500);
            }
        };
    }

}
