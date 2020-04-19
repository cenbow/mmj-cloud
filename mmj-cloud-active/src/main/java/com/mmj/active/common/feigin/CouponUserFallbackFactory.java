package com.mmj.active.common.feigin;

import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CouponUserFallbackFactory implements FallbackFactory<CouponUserFeignClient> {
    private final Logger logger = LoggerFactory.getLogger(CouponUserFallbackFactory.class);

    @Override
    public CouponUserFeignClient create(Throwable cause) {
        logger.info("CouponUserFallbackFactory error message is {}", cause.getMessage());
        return  new CouponUserFeignClient() {
            @Override
            public ReturnData receive(UserCouponVo userCouponVo) {
                throw new BusinessException("调用优惠券获取优惠券信息报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> hasReceive(UserCouponVo userCouponVo) {
                throw new BusinessException("调用优惠券判断用户是否领取优惠券报错," + cause.getMessage(), 500);
            }
        };
    }
}
