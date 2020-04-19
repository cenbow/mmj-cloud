package com.mmj.active.coupon.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.coupon.model.CouponClass;
import com.mmj.active.coupon.model.CouponGood;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponGoodDto;
import com.mmj.active.coupon.service.CouponGoodService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 优惠券关联商品表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@RestController
@RequestMapping("/coupon/couponGood")
public class CouponGoodController extends BaseController {
    @Autowired
    private CouponGoodService couponGoodService;

    @RequestMapping(value = "/show/{couponId}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券ID获取关联商品-可页面展示价格的商品")
    public ReturnData<List<CouponGoodDto>> getShowCouponGoods(@PathVariable("couponId") Integer couponId) {
        return initSuccessObjectResult(couponGoodService.getCouponGoods(couponId));
    }

    @RequestMapping(value = "/{couponId}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券ID获取关联商品")
    public ReturnData<List<CouponGood>> getCouponGoods(@PathVariable("couponId") Integer couponId) {
        CouponGood queryCouponGood = new CouponGood();
        queryCouponGood.setCouponId(couponId);
        EntityWrapper entityWrapper = new EntityWrapper(queryCouponGood);
        return initSuccessObjectResult(couponGoodService.selectList(entityWrapper));
    }

    @RequestMapping(value = "/{goodClass}/{goodId}", method = RequestMethod.POST)
    @ApiOperation("根据商品分类编码和商品ID获取可用优惠券列表")
    public ReturnData<List<CouponInfo>> getCouponInfoList(@PathVariable("goodClass") String goodClass, @PathVariable("goodId") Integer goodId) {
        return initSuccessObjectResult(couponGoodService.getCouponInfoList(goodClass, goodId));
    }
}

