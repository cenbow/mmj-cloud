package com.mmj.pay.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.pay.common.model.dto.UserCouponDto;
import com.mmj.pay.common.model.vo.PayIsBuyGiveVo;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        logger.info("UserFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {
            @Override
            public ReturnData<Boolean> isMember(Long userId) {
                throw new BusinessException("调用判断是否是会员接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<UserCouponDto> myCouponInfo(Integer couponCode) {
                throw new BusinessException("通过优惠券编码获取优惠券信息接口报错:," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> getPayIsBuyGive(PayIsBuyGiveVo payIsBuyGiveVo) {
                throw new BusinessException("调用用户下单是否具有买送资格:" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<WxConfig>> queryByAppType(String type) {
                throw new BusinessException("根据类型获取公众号配置错误:" + cause.getMessage(), 500);
            }
        };
    }
}
