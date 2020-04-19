package com.mmj.active.coupon.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.coupon.model.CouponClass;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.service.CouponClassService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 优惠券关联商品分类表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@RestController
@RequestMapping("/coupon/couponClass")
public class CouponClassController extends BaseController {
    @Autowired
    private CouponClassService couponClassService;

    @RequestMapping(value = "/{couponId}", method = RequestMethod.POST)
    @ApiOperation("根据优惠券ID获取关联商品分类")
    public ReturnData<List<CouponClass>> getCouponGoodsClass(@PathVariable("couponId") Integer couponId) {
        CouponClass queryCouponClass = new CouponClass();
        queryCouponClass.setCouponId(couponId);
        EntityWrapper entityWrapper = new EntityWrapper(queryCouponClass);
        return initSuccessObjectResult(couponClassService.selectList(entityWrapper));
    }
}

