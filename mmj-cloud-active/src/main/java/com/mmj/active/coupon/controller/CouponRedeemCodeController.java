package com.mmj.active.coupon.controller;

import com.mmj.active.coupon.model.vo.ExchangeCouponVo;
import com.mmj.active.coupon.model.vo.RedeemCodeVo;
import com.mmj.active.coupon.service.CouponRedeemCodeService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 优惠券兑换码
 */
@Slf4j
@RestController
@RequestMapping("/coupon/couponRedeemCode")
@Api(value = "优惠券兑换码", description = "优惠券兑换码")
public class CouponRedeemCodeController extends BaseController {

    @Autowired
    private CouponRedeemCodeService couponRedeemCodeService;

    /**
     * 生成优惠券兑换码
     * @param redeemCodeVo
     * @return
     */
    @RequestMapping(value = "/addRedeemCode", method = RequestMethod.POST)
    @ApiOperation(value = "生成优惠券兑换码")
    public ReturnData<Object> addRedeemCode(@RequestBody RedeemCodeVo redeemCodeVo) {
        return initSuccessObjectResult(couponRedeemCodeService.addRedeemCode(redeemCodeVo));
    }

    /**
     * 下载兑换码
     * @param request
     * @param response
     * @param batchCode
     * @return
     */
    @RequestMapping(value = "/downloadRedeemCode/{batchCode}", method = RequestMethod.GET)
    @ApiOperation(value = "下载兑换码")
    public ReturnData<Object> downloadRedeemCode(HttpServletRequest request,
                                     HttpServletResponse response, @PathVariable("batchCode")  String batchCode) {
        return initSuccessObjectResult(couponRedeemCodeService.downloadRedeemCode(request,response,batchCode));
    }

    /**
     * 兑换优惠券
     * @param exchangeCouponVo
     * @return
     */
    @RequestMapping(value = "/exchangeCoupon", method = RequestMethod.POST)
    @ApiOperation(value = "兑换优惠券")
    public ReturnData<Object> exchangeCoupon(@RequestBody ExchangeCouponVo exchangeCouponVo) {
        return initSuccessObjectResult(couponRedeemCodeService.exchangeCoupon(exchangeCouponVo));
    }

    /**
     * 根据兑换码获取优惠券
     * @param exchangeCouponVo
     * @return
     */
    @RequestMapping(value = "/getRedeemCoupon", method = RequestMethod.POST)
    @ApiOperation(value = "根据兑换码获取优惠券")
    public ReturnData<Object> getRedeemCoupon(@RequestBody ExchangeCouponVo exchangeCouponVo){
        return initSuccessObjectResult(couponRedeemCodeService.getRedeemCoupon(exchangeCouponVo));
    }

}
