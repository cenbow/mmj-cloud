package com.mmj.pay.common.feign;


import com.mmj.common.interceptor.FeignRequestInterceptor;
import com.mmj.common.model.ReturnData;
import com.mmj.common.model.WxConfig;
import com.mmj.pay.common.model.dto.UserCouponDto;
import com.mmj.pay.common.model.vo.PayIsBuyGiveVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "mmj-cloud-user", fallbackFactory = UserFallbackFactory.class, configuration = FeignRequestInterceptor.class)
public interface UserFeignClient {

    /**
     *   判断当前用户是否会员
     * @param userId
     * @return
     */
    @RequestMapping(value = "/user/isMember/{userId}", method = RequestMethod.POST)
    @ResponseBody
    public ReturnData<Boolean> isMember(@PathVariable("userId") Long userId);


    /**
     * 根据优惠券编码获取优惠券信息
     * @param couponCode
     * @return
     */
    @RequestMapping(value="/user/couponUser/my/{couponCode}", method=RequestMethod.POST)
    @ResponseBody
    public ReturnData<UserCouponDto> myCouponInfo(@PathVariable("couponCode") Integer couponCode);

    /**
     * 下单查询是否享受买送资格
     * @param payIsBuyGiveVo
     * @return
     */
    @RequestMapping(value="/member/send/getPayIsBuyGive", method=RequestMethod.POST)
    @ResponseBody
    public ReturnData<Boolean> getPayIsBuyGive(@RequestBody PayIsBuyGiveVo payIsBuyGiveVo);

    /**
     * 根据类型查询公众号配置
     * @param type
     * @return
     */
    @RequestMapping(value="/wx/config/queryByType/{type}", method=RequestMethod.POST)
    @ResponseBody
    ReturnData<List<WxConfig>> queryByAppType(@PathVariable("type") String type);
}
