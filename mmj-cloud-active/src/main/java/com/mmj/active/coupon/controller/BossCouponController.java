package com.mmj.active.coupon.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.BossCouponDto;
import com.mmj.active.coupon.model.vo.BossCouponAddVo;
import com.mmj.active.coupon.model.vo.BossCouponQueryVo;
import com.mmj.active.coupon.model.vo.DetailShowVo;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * boss优惠券
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@RestController
@RequestMapping("/boss/coupon")
@Api(value = "优惠券接口", description = "优惠券接口")
public class BossCouponController extends BaseController {
    @Autowired
    private CouponInfoService couponInfoService;

    @PostMapping("/add")
    @ApiOperation("新增优惠券")
    public ReturnData add(@Valid @RequestBody BossCouponAddVo couponAddVo) {
        couponInfoService.add(couponAddVo);
        return initSuccessResult();
    }

    @PostMapping("/query")
    @ApiOperation("优惠券列表")
    public ReturnData<Page<BossCouponDto>> list(@RequestBody BossCouponQueryVo queryVo) {
        return initSuccessObjectResult(couponInfoService.query(queryVo));
    }

    @PostMapping("/goods/show")
    @ApiOperation("商详隐藏优惠券")
    public ReturnData detailShow(@Valid @RequestBody DetailShowVo detailShowVo) {
        couponInfoService.detailShow(detailShowVo);
        return initSuccessResult();
    }

    @PostMapping("/batch")
    @ApiOperation("批量获取优惠券列表")
    public ReturnData<List<CouponInfo>> batchGetCouponInfoList(@RequestBody List<Integer> couponIds) {
        return initSuccessObjectResult(couponInfoService.batchCouponInfos(couponIds));
    }
}
