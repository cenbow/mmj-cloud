package com.mmj.active.coupon.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.coupon.model.CouponInfo;
import com.mmj.active.coupon.model.dto.CouponInfoDto;
import com.mmj.active.coupon.model.dto.CouponNumDto;
import com.mmj.active.coupon.service.CouponInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;

/**
 * <p>
 * 优惠券信息表 前端控制器
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Slf4j
@RestController
@RequestMapping("/coupon/couponInfo")
@Api(value = "优惠券接口", description = "优惠券接口")
public class CouponInfoController extends BaseController {
    @Autowired
    private CouponInfoService couponInfoService;

    /**
     * 根据优惠券ID获取优惠券模板
     *
     * @param couponId
     * @return
     */
    @PostMapping("/{couponId}")
    @ApiOperation("根据优惠券ID获取优惠券模板")
    public ReturnData<CouponInfoDto> getCouponInfo(@PathVariable("couponId") Integer couponId) {
    	log.info("-->通过ID查询优惠券配置信息:{}", couponId);
        CouponInfo couponInfo = couponInfoService.selectById(couponId);
        return initSuccessObjectResult(couponInfoService.toCouponInfoDto(couponInfo));
    }

    /**
     * 根据优惠券活动标识获取优惠券模板
     *
     * @param activeType
     * @return
     */
    @PostMapping("/active/{activeType}")
    @ApiOperation("根据优惠券活动标识获取优惠券模板")
    public ReturnData<List<CouponInfoDto>> getActiveCouponInfoList(@PathVariable("activeType") String activeType) {
        CouponInfo queryCouponInfo = new CouponInfo();
        queryCouponInfo.setActiveFlag(activeType);
        queryCouponInfo.setDelFlag(0);
        EntityWrapper<CouponInfo> entityWrapper = new EntityWrapper<>(queryCouponInfo);
        List<CouponInfo> couponInfoList = couponInfoService.selectList(entityWrapper);
        return initSuccessObjectResult(couponInfoService.toCouponInfoDto(couponInfoList));
    }

    /**
     * 批量获取优惠券列表
     *
     * @param couponIds
     * @return
     */
    @PostMapping("/batch")
    @ApiOperation("批量获取优惠券列表")
    public ReturnData<List<CouponInfoDto>> batchGetCouponInfoList(@RequestBody List<Integer> couponIds) {
        List<CouponInfo> couponInfoList = couponInfoService.batchCouponInfos(couponIds);
        return initSuccessObjectResult(couponInfoService.toCouponInfoDto(couponInfoList));
    }

    @PostMapping("/todayNum/batch")
    @ApiOperation("批量获取优惠券当天发放数量")
    public ReturnData<List<CouponNumDto>> batchTodayNum(@RequestBody List<Integer> couponIds) {
        return initSuccessObjectResult(couponInfoService.batchTodayNums(couponIds));
    }

    @PostMapping("/todayNum/{couponId}")
    @ApiOperation("获取优惠券当天发放数量")
    public ReturnData<CouponNumDto> todayNum(@PathVariable("couponId") Integer couponId) {
        return initSuccessObjectResult(couponInfoService.toDayNum(couponId));
    }

    @PostMapping("/issue/{couponId}")
    @ApiOperation("已发放优惠券计数")
    public ReturnData issued(@PathVariable("couponId") Integer couponId) {
        couponInfoService.issued(couponId);
        return initSuccessResult();
    }
}

