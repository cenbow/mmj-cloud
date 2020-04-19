package com.mmj.aftersale.common.feigin;

import com.mmj.aftersale.common.model.*;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    /**
     * 获取订单所使用的优惠券
     * @param orderCouponVo
     * @return
     */
    @RequestMapping(value = "/user/couponUser/my/order", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<List<UserCouponDto>> myOrderCouponList(@RequestBody OrderCouponVo orderCouponVo);


    @RequestMapping(value = "/member/kingUser/degradeProces/{orderNo}/{userId}",method = RequestMethod.POST)
    boolean degradeProces(@PathVariable("orderNo") String orderNo, @PathVariable("userId") Long userId);

    @RequestMapping(value="/member/send/getMemberThresholdAsConsumeBoss/{userId}",method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Map<String, Object>> getMemberThresholdAsConsumeBoss(@PathVariable("userId") Long userId);

    @RequestMapping(value="/member/degrade", method=RequestMethod.POST)
    ReturnData<Object> degrade(@RequestBody DegradeVo degradeVo);

    @RequestMapping(value="/member/send/editBuyGice", method=RequestMethod.POST)
    ReturnData<Boolean> editBuyGice(@RequestBody Long userId);

    @RequestMapping(value = "/member/kingUser/oweKingNum/{orderNo}/{userId}",method = RequestMethod.POST)
    int getOweKingNum(@PathVariable("orderNo") String orderNo, @PathVariable("userId") Long userId);

    @ApiOperation("推荐返现 - 查询退款金额(给订单调用)")
    @RequestMapping(value = "/recommend/userShard/queryRefundByOrderNo",method = RequestMethod.POST)
    ReturnData<Integer> queryRefundByOrderNo(@RequestBody Map<String,Object> map);

    /**
     * &
     * 给订单调用, 判断该订单是展示 去写推荐 or 分享得返现
     */
    @PostMapping("/recommend/userRecommend/selectByOrderNo")
    @ResponseBody
    ReturnData<List<UserRecommendOrder>> selectByOrderNo(@RequestBody Map<String, Object> map);

    /**
     * 查询判断是否会员
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/isMember/{userId}", method = RequestMethod.POST)
    ReturnData<Boolean> isMember(@PathVariable("userId") Long userId);

    /**
     * 查询会员信息
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "member/query/{userId}", method = RequestMethod.POST)
    ReturnData<UserMember> queryUserMemberInfoByUserId(@PathVariable("userId") Long userId);

}
