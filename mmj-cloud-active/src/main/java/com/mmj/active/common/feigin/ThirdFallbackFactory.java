package com.mmj.active.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.third.recharge.RechargeDetailsVo;
import com.mmj.common.model.third.recharge.RechargeDto;
import com.mmj.common.model.third.recharge.RechargeVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ThirdFallbackFactory implements FallbackFactory<ThirdFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(ThirdFallbackFactory.class);

    @Override
    public ThirdFeignClient create(Throwable cause) {
        logger.info("ThirdFallbackFactory error message is {}", cause.getMessage());
        return new ThirdFeignClient() {
            @Override
            public ReturnData<RechargeDto> details(RechargeDetailsVo rechargeDetailsVo) {
                throw new BusinessException("查询话费详情错误，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<RechargeDto> recharge(RechargeVo rechargeVo) {
                throw new BusinessException("话费充值错误，" + cause.getMessage(), 500);
            }
        };
    }


}
