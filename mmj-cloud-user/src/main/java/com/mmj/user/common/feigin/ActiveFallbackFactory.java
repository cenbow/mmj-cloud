package com.mmj.user.common.feigin;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.user.common.model.dto.CouponInfoDto;
import com.mmj.user.common.model.dto.CouponNumDto;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActiveFallbackFactory implements FallbackFactory<ActiveFeignClient> {

    private final Logger logger = LoggerFactory.getLogger(ActiveFallbackFactory.class);

    @Override
    public ActiveFeignClient create(Throwable cause) {
        logger.info("ActiveFallbackFactory error message is {}", cause.getMessage());
        return new ActiveFeignClient() {
            @Override
            public ReturnData<CouponInfoDto> getCouponInfo(Integer couponId) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "获取优惠券模板失败");
            }

            @Override
            public ReturnData<List<CouponInfoDto>> batchCouponInfos(List<Integer> couponIds) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "获取优惠券模板失败");
            }

            @Override
            public ReturnData issued(Integer couponId) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "已发放优惠券计数失败");
            }

            @Override
            public ReturnData<CouponNumDto> todayNum(Integer couponId) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "获取当天优惠券发放数量失败");
            }

            @Override
            public ReturnData<List<CouponInfoDto>> getActiveCouponInfoList(String activeType) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "获取活动优惠券失败");
            }

            @Override
            public ReturnData<List<CouponInfoDto>> getGoodCouponList(String goodClass, Integer goodId) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "获取商品优惠券失败");
            }

            @Override
            public int getRemind(Long userId, Integer from) {
                throw new BusinessException("流量池查询抽奖关注失败");
            }

            @Override
            public ReturnData<Object> updateIndexCode(String userIdentity) {
                return new ReturnData<>(SecurityConstants.EXCEPTION_CODE, "修改首页版本号失败");
            }
        };
    }

}
