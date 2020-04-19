package com.mmj.active.common.feigin;

import com.mmj.active.common.model.vo.UserCouponVo;
import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.UserReceiveCouponDto;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@FeignClient(name = "mmj-cloud-user", fallbackFactory = CouponUserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface CouponUserFeignClient {
    /**
     * 给用户发送优惠券
     *
     * @param userCouponVo
     * @return
     */
    @RequestMapping(value = "/async/receive", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<UserReceiveCouponDto> receive(@Valid @RequestBody UserCouponVo userCouponVo);

    /**
     * 判断用户是否已经领取过优惠券
     *
     * @param userCouponVo
     * @return
     */
    @RequestMapping(value = "/user/couponUser/hasReceive", method = RequestMethod.POST)
    @ResponseBody
    ReturnData<Boolean> hasReceive(@Valid @RequestBody UserCouponVo userCouponVo);
}
