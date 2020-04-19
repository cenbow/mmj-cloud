package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.*;
import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Component
public class UserFallbackFactory implements FallbackFactory<UserFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(UserFallbackFactory.class);

    @Override
    public UserFeignClient create(Throwable cause) {
        logger.info("OrderFallbackFactory error message is {}", cause.getMessage());
        return new UserFeignClient() {
            public ReturnData<List<UserCouponDto>> myOrderCouponList(@RequestBody OrderCouponVo orderCouponVo) {
                throw new BusinessException("查询订单所使用优惠券报错，" + cause.getMessage(), 500);
            }

            @Override
            public boolean degradeProces(String orderNo, Long userId) {
                throw new BusinessException("会员降级处理买买金报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Map<String, Object>> getMemberThresholdAsConsumeBoss(Long userId) {
                throw new BusinessException("查询会员门槛及历史消费金额报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Object> degrade(@RequestBody DegradeVo degradeVo) {
                throw new BusinessException("调用会员降级报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> editBuyGice(@RequestBody Long userId) {
                throw new BusinessException("调用取消会员买送资格报错，" + cause.getMessage(), 500);
            }

            @Override
            public int getOweKingNum(String orderNo, Long userId) {
                throw new BusinessException("查询会员已经使用的买买金数量报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Integer> queryRefundByOrderNo(@RequestBody Map<String, Object> map) {
                throw new BusinessException("调用更新分享金额接口报错," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<UserRecommendOrder>> selectByOrderNo(@RequestBody Map<String, Object> map) {
                throw new BusinessException("调用用户推荐订单报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<Boolean> isMember(Long userId) {
                throw new BusinessException("查询判断用户是否是会员报错，" + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<UserMember> queryUserMemberInfoByUserId(Long userId) {
                throw new BusinessException("查询会员信息接口报错，" + cause.getMessage(), 500);
            }
        };
    }

}
