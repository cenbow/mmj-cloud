package com.mmj.user.common.feigin;

import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.user.common.model.dto.CouponInfoDto;
import com.mmj.user.common.model.dto.CouponNumDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "mmj-cloud-active", fallbackFactory = ActiveFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface ActiveFeignClient {
    /**
     * 获取优惠券模板
     *
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/coupon/couponInfo/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<CouponInfoDto> getCouponInfo(@PathVariable("couponId") Integer couponId);

    /**
     * 批量获取优惠券列表
     *
     * @param couponIds
     * @return
     */
    @RequestMapping(value = "/async/batch", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CouponInfoDto>> batchCouponInfos(@RequestBody List<Integer> couponIds);

    /**
     * 已发放优惠券计数
     *
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/async/issue/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData issued(@PathVariable("couponId") Integer couponId);

    /**
     * 获取优惠券当天发放数量
     *
     * @param couponId
     * @return
     */
    @RequestMapping(value = "/coupon/couponInfo/todayNum/{couponId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<CouponNumDto> todayNum(@PathVariable("couponId") Integer couponId);

    /**
     * 根据优惠券活动标识获取优惠券模板
     *
     * @param activeType
     * @return
     */
    @RequestMapping(value = "/coupon/couponInfo/active/{activeType}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CouponInfoDto>> getActiveCouponInfoList(@PathVariable("activeType") String activeType);

    /**
     * 获取商品可用的优惠券
     *
     * @param goodClass
     * @param goodId
     * @return
     */
    @RequestMapping(value = "/coupon/couponGood/{goodClass}/{goodId}", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<CouponInfoDto>> getGoodCouponList(@PathVariable("goodClass") String goodClass, @PathVariable("goodId") Integer goodId);

    /**
     * 查询用户关注情况
     *
     * @param userId
     * @param from
     * @return
     */
    @RequestMapping(value = "/async/getRemind/{userId}/{from}", method = RequestMethod.POST)
    int getRemind(@PathVariable("userId") Long userId,
                  @PathVariable("from") Integer from);

    /**
     * 修改用户首页版本号
     *
     * @param userIdentity
     * @return
     */
    @PostMapping("/async/updateIndexCode/{userIdentity}")
    ReturnData<Object> updateIndexCode(@PathVariable("userIdentity") String userIdentity);
}
