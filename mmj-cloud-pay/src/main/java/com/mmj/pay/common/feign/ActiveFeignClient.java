package com.mmj.pay.common.feign;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.active.ActiveGoodStoreResult;
import com.mmj.pay.common.model.vo.ActiveGoodStore;
import com.mmj.pay.common.model.vo.CouponClass;
import com.mmj.pay.common.model.vo.CouponGood;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-active", fallbackFactory = ActiveFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ActiveFeignClient {


    /**
     * 通过优惠券Id 获取优惠券分类信息信息异常
     *
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/coupon/couponClass/{couponId}", method = RequestMethod.POST)
    ReturnData<List<CouponClass>> getCouponGoodsClass(@PathVariable("couponId") Integer couponId);


    /**
     * 通过优惠券Id 获取优惠的商品信息
     *
     *
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/coupon/couponGood/{couponId}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券ID获取关联商品")
    public ReturnData<List<CouponGood>> getCouponGoods(@PathVariable("couponId") Integer couponId);


    /**
     *   @ApiOperation(value = "活动商品下单验证")
     * @param activeGoodStore
     * @return
     */
    @RequestMapping(value = "/activeGood/orderCheck", method = RequestMethod.POST)
    public ReturnData<ActiveGoodStoreResult> orderCheck(@RequestBody ActiveGoodStore activeGoodStore);


}
