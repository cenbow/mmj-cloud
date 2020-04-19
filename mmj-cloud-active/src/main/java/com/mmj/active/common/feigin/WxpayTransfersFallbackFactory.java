package com.mmj.active.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxpayTransfers;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WxpayTransfersFallbackFactory implements FallbackFactory<WxpayTransfersFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(WxpayTransfersFallbackFactory.class);

    @Override
    public WxpayTransfersFeignClient create(Throwable throwable) {
        logger.info("WxpayTransfersFallbackFactory error message is {}", throwable.getMessage());
        return new WxpayTransfersFeignClient() {
            @Override
            public ReturnData<WxpayTransfers> transfers(WxpayTransfers wxpayTransfers) {
                throw new BusinessException("调用发送零钱报错," + throwable.getMessage(), 500);
            }
        };
    }
}
