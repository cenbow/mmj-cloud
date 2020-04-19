package com.mmj.user.manager.controller;

import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.StringUtils;
import com.mmj.user.manager.dto.*;
import com.mmj.user.manager.service.CouponUserService;
import com.mmj.user.manager.vo.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 用户关联优惠券表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-07-04
 */
@Slf4j
@RestController
@RequestMapping("/user/couponUser")
@Api(value = "用户优惠券", description = "用户优惠券")
public class CouponUserController extends BaseController {
    @Autowired
    private CouponUserService couponUserService;

    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ApiOperation(value = "添加优惠券")
    public ReturnData<UserReceiveCouponDto> receive(@Valid @RequestBody UserCouponVo userCouponVo) {
        log.info("-->添加优惠券，参数：{}", JSONObject.toJSONString(userCouponVo));
        try {
            UserReceiveCouponDto userReceiveCouponDto = couponUserService.receive(userCouponVo);
            log.info("-->添加优惠券，返回：{}", JSONObject.toJSONString(userReceiveCouponDto));
            if (userReceiveCouponDto.getResultStatus() == 1) {
                return initSuccessObjectResult(userReceiveCouponDto);
            }
            String message;
            switch (userReceiveCouponDto.getResultStatus()) {
                case 0:
                    message = "重复领取";
                    break;
                case 2:
                    message = "优惠券已发完";
                    break;
                default:
                    message = "领取优惠券异常";
                    break;
            }
            return initErrorObjectResult(message);
        } catch (Exception e) {
            log.error("领取优惠券异常 error:{}", e.toString());
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "领取优惠券失败");
        }
    }

    @RequestMapping(value = "/receive/batch", method = RequestMethod.POST)
    @ApiOperation(value = "批量添加优惠券")
    public ReturnData<UserReceiveCouponDto> batchReceive(@Valid @RequestBody UserCouponBatchVo userCouponBatchVo) {
        log.info("-->批量添加优惠券，参数：{}", JSONObject.toJSONString(userCouponBatchVo));
        try {
            couponUserService.batchReceive(userCouponBatchVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("批量添加优惠券异常 error:{}", e.toString());
            return initErrorObjectResult(StringUtils.isNotEmpty(e.getMessage()) ? e.getMessage() : "领取优惠券失败");
        }
    }

    @RequestMapping(value = "/use", method = RequestMethod.POST)
    @ApiOperation(value = "使用优惠券")
    public ReturnData<Object> use(@Valid @RequestBody UseUserCouponVo useUserCouponVo) {
        log.info("-->使用优惠券，参数：{}", JSONObject.toJSONString(useUserCouponVo));
        boolean status = couponUserService.use(useUserCouponVo);
        return status ? initSuccessResult() : initErrorObjectResult("使用优惠券失败");
    }

    @RequestMapping(value = "/hasReceive", method = RequestMethod.POST)
    @ApiOperation(value = "判断用户是否已经领取该优惠券")
    public ReturnData<Boolean> hasReceive(@Valid @RequestBody UserCouponVo userCouponVo) {
        log.info("-->判断用户是否已经领取该优惠券，参数：{}", JSONObject.toJSONString(userCouponVo));
        boolean status = couponUserService.hasReceive(userCouponVo);
        return initSuccessObjectResult(status);
    }

    @RequestMapping(value = "/hasReceive/batch", method = RequestMethod.POST)
    @ApiOperation(value = "批量判断用户是否已经领取优惠券")
    public ReturnData<List<UserCouponReceiveDto>> batchHasReceive(@Valid @RequestBody BatchUserCouponVo batchUserCouponVo) {
        log.info("-->批量判断用户是否已经领取优惠券，参数：{}", JSONObject.toJSONString(batchUserCouponVo));
        return initSuccessObjectResult(couponUserService.batchHasReceive(batchUserCouponVo));
    }

    @RequestMapping(value = "/my/order", method = RequestMethod.POST)
    @ApiOperation(value = "获取订单使用的优惠券")
    public ReturnData<List<UserCouponDto>> myOrderCouponList(@RequestBody OrderCouponVo orderCouponVo) {
        log.info("-->获取订单使用的优惠券，参数：{}", JSONObject.toJSONString(orderCouponVo));
        return initSuccessObjectResult(couponUserService.myOrderCouponList(orderCouponVo));
    }

    @RequestMapping(value = "/my", method = RequestMethod.POST)
    @ApiOperation(value = "获取我的优惠券")
    public ReturnData<List<UserCouponDto>> myCouponList() {
        log.info("-->获取我的优惠券");
        return initSuccessObjectResult(couponUserService.myCouponList());
    }

    @RequestMapping(value = "/my/{couponCode}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券编码获取优惠券信息")
    public ReturnData<UserCouponDto> myCouponInfo(@PathVariable("couponCode") Integer couponCode) {
        log.info("-->根据优惠券编码获取优惠券信息，参数：{}", couponCode);
        return initSuccessObjectResult(couponUserService.myCouponInfo(couponCode));
    }

    @RequestMapping(value = "/my/batch", method = RequestMethod.POST)
    @ApiOperation("根据多个优惠券编码获取优惠券信息")
    public ReturnData<List<UserCouponDto>> myCouponInfoList(@RequestBody List<Integer> couponCodes) {
        log.info("-->根据多个优惠券编码获取优惠券信息，参数：{}", couponCodes);
        return initSuccessObjectResult(couponUserService.myCouponInfoList(couponCodes));
    }

    @RequestMapping(value = "/my/couponId/{couponId}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券ID获取优惠券信息")
    public ReturnData<List<UserCouponDto>> myCouponInfoByCouponId(@PathVariable("couponId") Integer couponId) {
        log.info("-->根据优惠券ID获取优惠券信息，参数：{}", couponId);
        return initSuccessObjectResult(couponUserService.myCouponInfoByCouponId(couponId));
    }

    @RequestMapping(value = "/member", method = RequestMethod.POST)
    @ApiOperation(value = "获取会员日优惠券")
    public ReturnData<MemberCouponDto> memberCouponInfoList() {
        log.info("-->获取会员日优惠券");
        return initSuccessObjectResult(couponUserService.memberCouponInfoList());
    }

    @RequestMapping(value = "/goods", method = RequestMethod.POST)
    @ApiOperation(value = "获取商品可用的优惠券")
    public ReturnData<List<GoodsCouponDto>> goodCouponInfoList(@Valid @RequestBody GoodsCouponVo goodsCouponVo) {
        log.info("-->获取商品可用的优惠券，参数：{}", JSONObject.toJSONString(goodsCouponVo));
        return initSuccessObjectResult(couponUserService.goodsCouponInfoList(goodsCouponVo));
    }

    @RequestMapping(value = "/personal", method = RequestMethod.POST)
    @ApiOperation(value = "获取用户中心优惠券信息")
    public ReturnData<PersonalCouponInfoDto> personalCouponInfo() {
        log.info("-->获取用户中心优惠券信息");
        return initSuccessObjectResult(couponUserService.personalCouponInfo());
    }

    @RequestMapping(value = "/order/produce", method = RequestMethod.POST)
    @ApiOperation(value = "下单前获取优惠券列表")
    public ReturnData<ProduceOrderCouponDto> produceOrderCoupon(@Valid @RequestBody ProduceOrderCouponVo produceOrderCouponVo) {
        log.info("-->下单前获取优惠券列表，参数：{}", JSONObject.toJSONString(produceOrderCouponVo));
        return initSuccessObjectResult(couponUserService.produceOrderCoupon(produceOrderCouponVo));
    }
}

