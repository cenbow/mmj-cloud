package com.mmj.pay.common.feign;

import com.mmj.common.exception.BusinessException;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.pay.common.model.vo.ActiveGoodStore;
import com.mmj.pay.common.model.vo.CouponClass;
import com.mmj.pay.common.model.vo.CouponGood;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActiveFallbackFactory implements FallbackFactory<ActiveFeignClient> {


    @Override
    public ActiveFeignClient create(Throwable cause) {
        return new ActiveFeignClient() {
            @Override
            public ReturnData<List<CouponClass>> getCouponGoodsClass(Integer couponId) {
                throw new BusinessException("通过优惠劵Id获取优惠券分类信息接口异常:," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<List<CouponGood>> getCouponGoods(Integer couponId) {
                throw new BusinessException("通过优惠券Id获取优惠券商品信息异常:," + cause.getMessage(), 500);
            }

            @Override
            public ReturnData<ActiveGoodStoreResult> orderCheck(ActiveGoodStore activeGoodStore) {
                throw new BusinessException("活动商品下单验证:," + cause.getMessage(), 500);
            }
        };
    }

}
